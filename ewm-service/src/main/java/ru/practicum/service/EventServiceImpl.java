package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.Constant.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final StatisticService statisticService;

    @Transactional
    @Override
    public EventDto saveEvent(long userId, NewEventDto newEventDto) {
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), formatter);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new UnexpectedTypeException(String.format(EVENT_DATE_ERROR, eventDate));
        }
        Event event = EventMapper.fromNewEventDtoToEvent(newEventDto);
        event.setCategory(categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_CATEGORY, newEventDto.getCategory()))));
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId))));
        locationRepository.save(newEventDto.getLocation());
        return EventMapper.toEventDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public EventDto getEventById(long eventId, HttpServletRequest request) {
        statisticService.saveStats(request);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_EVENT, eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event was not found or not published");
        }
        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setViews(statisticService.getViews(event));
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventDto getEventByIdForUser(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_EVENT, eventId)));
        checkInitiatorEvent(event, userId);
        return EventMapper.toEventDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllEventByInitiatorId(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        List<EventShortDto> eventsShortDto = new ArrayList<>();
        for (Event event : events) {
            EventShortDto eventShortDto = EventMapper.toEventShortDto(event);
            eventShortDto.setViews(statisticService.getViews(event));
            eventsShortDto.add(eventShortDto);
        }
        return eventsShortDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> getEvent(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                   boolean onlyAvailable, EventSort sort, int from, int size, HttpServletRequest request) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        statisticService.saveStats(request);
        if (text == null && categories == null && paid == null && rangeStart == null && rangeEnd == null) {
            return new ArrayList<>();
        }
        BooleanExpression finalCondition = makeExpressionForGet(text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAll(finalCondition, pageRequest).stream().collect(Collectors.toList());
        if (onlyAvailable) {
            events.stream()
                    .filter(event -> (event.getParticipantLimit() - event.getConfirmedRequests()) > 0)
                    .collect(Collectors.toList());
        }
        List<EventDto> eventsDto = new ArrayList<>();
        for (Event event : events) {
            EventDto eventDto = EventMapper.toEventDto(event);
            eventDto.setViews(statisticService.getViews(event));
            eventsDto.add(eventDto);
        }
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventsDto.stream().sorted(Comparator.comparing(EventDto::getViews)).collect(Collectors.toList());
        } else if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            eventsDto.stream().sorted(Comparator.comparing(EventDto::getEventDate)).collect(Collectors.toList());
        }
        return eventsDto;
    }

    private BooleanExpression makeExpressionForGet(String text, List<Long> categories, Boolean paid,
                                                   String rangeStart, String rangeEnd) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (rangeEnd != null && rangeStart != null
                && LocalDateTime.parse(rangeStart, formatter).isAfter(LocalDateTime.parse(rangeEnd, formatter))) {
            throw new UnexpectedTypeException("The beginning of the range cannot be later than the end of the range.");
        }
        if (text != null) {
            List<BooleanExpression> textInAnnotOrDescr = new ArrayList<>();
            String textLowerCase = text.toLowerCase();
            textInAnnotOrDescr.add(event.annotation.toLowerCase().like("%" + textLowerCase + "%"));
            textInAnnotOrDescr.add(event.description.toLowerCase().like("%" + textLowerCase + "%"));
            BooleanExpression expressionText = textInAnnotOrDescr.stream().reduce(BooleanExpression::or).get();
            conditions.add(expressionText);
        }
        if (categories != null)
            for (Long catId : categories) {
                conditions.add(event.category.id.eq(catId));
            }
        if (paid != null)
            conditions.add(event.paid.eq(paid));
        if (rangeStart != null) {
            LocalDateTime rangeStartDate = LocalDateTime.parse(rangeStart, formatter);
            conditions.add(event.eventDate.after(rangeStartDate));
        } else {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (rangeEnd != null) {
            LocalDateTime rangeEndDate = LocalDateTime.parse(rangeEnd, formatter);
            conditions.add(event.eventDate.before(rangeEndDate));
        }
        return conditions.stream().reduce(BooleanExpression::and).get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> searchEvent(List<Long> users, List<EventState> states, List<Long> categories,
                                      String rangeStart, String rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<BooleanExpression> conditions = makeExpressionForSearch(users, states, categories, rangeStart, rangeEnd);
        List<Event> events;
        if (!conditions.isEmpty()) {
            BooleanExpression finalCondition = conditions.stream().reduce(BooleanExpression::and).get();
            events = eventRepository.findAll(finalCondition, pageRequest).stream().collect(Collectors.toList());
        } else {
            events = eventRepository.findAll(pageRequest).stream().collect(Collectors.toList());
        }
        List<EventDto> eventsDto = new ArrayList<>();
        for (Event event : events) {
            EventDto eventDto = EventMapper.toEventDto(event);
            eventDto.setViews(statisticService.getViews(event));
            eventsDto.add(eventDto);
        }
        return eventsDto;
    }

    private List<BooleanExpression> makeExpressionForSearch(List<Long> users, List<EventState> states,
                                                            List<Long> categories, String rangeStart, String rangeEnd) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (users != null) {
            for (Long userId : users) {
                conditions.add(event.initiator.id.eq(userId));
            }
        }
        if (states != null) {
            for (EventState state : states) {
                conditions.add(event.state.stringValue().eq(state.toString()));
            }
        }
        if (categories != null) {
            for (Long catId : categories) {
                conditions.add(event.category.id.eq(catId));
            }
        }
        if (rangeStart != null) {
            LocalDateTime rangeStartDate = LocalDateTime.parse(rangeStart, formatter);
            conditions.add(event.eventDate.after(rangeStartDate));
        }
        if (rangeEnd != null) {
            LocalDateTime rangeEndDate = LocalDateTime.parse(rangeEnd, formatter);
            conditions.add(event.eventDate.before(rangeEndDate));
        }
        return conditions;
    }

    @Transactional
    @Override
    public EventDto updateAdminByEvent(long eventId, UpdateEventAdminRequestDto eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_EVENT, eventId)));
        LocalDateTime eventDate = event.getEventDate();
        if (eventDto.getEventDate() != null) {
            eventDate = LocalDateTime.parse(eventDto.getEventDate(), formatter);
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new UnexpectedTypeException(String.format(EVENT_DATE_ERROR, eventDate));
            }
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Cannot publish the event because it's not in the right state: "
                    + event.getState());
        }

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        EventMapper.updateAdmin(eventDto, event);
        if (eventDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory()).orElseThrow(
                    () -> new NotFoundException(String.format(NOT_FOUND_CATEGORY, eventDto.getCategory()))));
        }
        if (eventDto.getLocation() != null) {
            locationRepository.save(eventDto.getLocation());
        }
        EventDto eventDtoAfterSave = EventMapper.toEventDto(eventRepository.save(event));
        eventDtoAfterSave.setEventDate(eventDate.format(formatter));
        eventDtoAfterSave.setViews(statisticService.getViews(event));
        return eventDtoAfterSave;
    }

    @Transactional
    @Override
    public EventDto updateEvent(long userId, long eventId, UpdateEventUserRequestDto updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_EVENT, eventId)));
        if (updateEvent == null) {
            return EventMapper.toEventDto(event);
        }
        LocalDateTime eventDate;
        if (updateEvent.getEventDate() != null) {
            eventDate = LocalDateTime.parse(updateEvent.getEventDate(), formatter);
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new UnexpectedTypeException(String.format(EVENT_DATE_ERROR, eventDate));
            }
            event.setEventDate(eventDate);
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Cannot edit an event once it is published.");
        }
        checkInitiatorEvent(event, userId);
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (updateEvent.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else if (updateEvent.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
            }
        }
        if (updateEvent.getLocation() != null) {
            locationRepository.save(updateEvent.getLocation());
        }
        EventMapper.updateEvent(updateEvent, event);
        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEvent.getCategory()).orElseThrow(
                    () -> new NotFoundException(String.format(NOT_FOUND_CATEGORY, updateEvent.getCategory()))));
        }
        return EventMapper.toEventDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestByEvent(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_EVENT, eventId)));
        checkInitiatorEvent(event, userId);
        List<ParticipationRequest> allRequestById = requestRepository.findAllByEventId(eventId);
        return RequestMapper.listToParticipationRequestDto(allRequestById);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResultDto updateEventRequest(long userId, long eventId,
                                                                EventRequestStatusUpdateRequestDto requestsByEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_EVENT, eventId)));
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ValidationException("The limit has been reached for the event.");
        }
        if (requestsByEvent == null) {
            throw new ValidationException("RequestIds was not found in request");
        }
        List<ParticipationRequest> requestsByIds = requestRepository.findAllByIdIn(requestsByEvent.getRequestIds());
        checkInitiatorEvent(event, userId);
        long countApproveReq = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        checkForUpdate(requestsByEvent, event, requestsByIds, countApproveReq);
        requestsByIds.forEach(req -> req.setStatus(requestsByEvent.getStatus()));
        List<ParticipationRequest> requests = requestRepository.saveAll(requestsByIds);

        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        if (countApproveReq + requestsByEvent.getRequestIds().size() == event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            List<ParticipationRequest> requestsByEventIdAndStatus = requestRepository
                    .findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
            requestsByEventIdAndStatus.forEach(req -> req.setStatus(RequestStatus.REJECTED));
            rejectedRequests = requestRepository.saveAll(requestsByEventIdAndStatus);

        }

        rejectedRequests.addAll(requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.REJECTED));
        if (requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            long confirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(confirmedRequests + requestsByEvent.getRequestIds().size());
            eventRepository.save(event);
        }

        EventRequestStatusUpdateResultDto resultDto = new EventRequestStatusUpdateResultDto();
        if (requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED)) {
            resultDto.setConfirmedRequests(RequestMapper.listToParticipationRequestDto(requests));
        } else {
            resultDto.setRejectedRequests(RequestMapper.listToParticipationRequestDto(requests));
        }
        resultDto.setRejectedRequests(RequestMapper.listToParticipationRequestDto(rejectedRequests));
        return resultDto;

    }

    private void checkForUpdate(EventRequestStatusUpdateRequestDto requestsByEvent,
                                Event event, List<ParticipationRequest> requestsByIds, long countApproveReq) {
        if ((countApproveReq == event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED))
                || (countApproveReq + requestsByEvent.getRequestIds().size() > event.getParticipantLimit()
                && requestsByEvent.getStatus().equals(RequestStatus.CONFIRMED))) {
            throw new ValidationException("The participant limit has been reached");
        }
        for (ParticipationRequest request : requestsByIds) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ValidationException("Request must have status PENDING");
            }
        }
    }

    private void checkInitiatorEvent(Event event, long userId) {
        if (event.getInitiator().getId() != userId) {
            throw new ValidationException(String.format(USER_NOT_INITIATOR, userId, event.getId()));
        }
    }

}
