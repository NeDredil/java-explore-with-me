package ru.practicum.mapper;

import ru.practicum.dto.*;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.Constant.formatter;

public class EventMapper {

    private EventMapper() {
    }

    public static Event fromNewEventDtoToEvent(NewEventDto dto) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(LocalDateTime.parse(dto.getEventDate(), formatter))
                .location(dto.getLocation())
                .paid(dto.getPaid() == null ? false : dto.getPaid())
                .participantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() == null ? true : dto.getRequestModeration())
                .title(dto.getTitle())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .build();
    }

    public static EventDto toEventDto(Event event) {
        return EventDto.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .id(event.getId())
                .initiator(UserMapper.toShortUserDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toShortUserDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle()).build();
    }

    public static List<EventShortDto> listToEventShortDto(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public static void updateAdmin(UpdateEventAdminRequestDto eventUR, Event event) {
        event.setAnnotation(eventUR.getAnnotation() == null ? event.getAnnotation() : eventUR.getAnnotation());
        event.setDescription(eventUR.getDescription() == null ? event.getDescription() : eventUR.getDescription());
        event.setLocation(eventUR.getLocation() == null ? event.getLocation() : eventUR.getLocation());
        event.setPaid(eventUR.getPaid() == null ? event.isPaid() : eventUR.getPaid());
        event.setParticipantLimit(eventUR.getParticipantLimit() == null ? event.getParticipantLimit() : eventUR.getParticipantLimit());
        event.setRequestModeration(eventUR.getRequestModeration() == null ? event.isRequestModeration() : eventUR.getRequestModeration());
        event.setTitle(eventUR.getTitle() == null ? event.getTitle() : eventUR.getTitle());
    }

    public static void updateEvent(UpdateEventUserRequestDto eventUR, Event event) {
        event.setAnnotation(eventUR.getAnnotation() == null ? event.getAnnotation() : eventUR.getAnnotation());
        event.setDescription(eventUR.getDescription() == null ? event.getDescription() : eventUR.getDescription());
        event.setLocation(eventUR.getLocation() == null ? event.getLocation() : eventUR.getLocation());
        event.setPaid(eventUR.getPaid() == null ? event.isPaid() : eventUR.getPaid());
        event.setParticipantLimit(eventUR.getParticipantLimit() == null ? event.getParticipantLimit() : eventUR.getParticipantLimit());
        event.setRequestModeration(eventUR.getRequestModeration() == null ? event.isRequestModeration() : eventUR.getRequestModeration());
        event.setTitle(eventUR.getTitle() == null ? event.getTitle() : eventUR.getTitle());
    }

}
