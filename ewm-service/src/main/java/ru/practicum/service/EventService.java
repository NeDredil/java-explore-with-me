package ru.practicum.service;

import ru.practicum.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventDto> searchEvent(EventAdminDto  eventAdminDto);

    EventDto updateAdminByEvent(long eventId, UpdateEventAdminRequestDto eventDto);

    EventDto saveEvent(long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventByInitiatorId(long userId, int from, int size);

    EventDto getEventByIdForUser(long userId, long eventId);

    EventDto updateEvent(long userId, long eventId, UpdateEventUserRequestDto updateEvent);

    List<ParticipationRequestDto> getRequestByEvent(long userId, long eventId);

    EventRequestStatusUpdateResultDto updateEventRequest(long userId, long eventId,
                                                         EventRequestStatusUpdateRequestDto requestsByEvent);

    List<EventDto> getEvent(EventPublicDto eventPublicDto);

    EventDto getEventById(long eventId, HttpServletRequest request);

}
