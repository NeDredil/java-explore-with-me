package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto saveEvent(@PathVariable long userId,
                              @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Private. Save event: {}, user id: {}", newEventDto, userId);
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getAllEventByInitiatorId(@PathVariable long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Private. Get all event by user id: {}", userId);
        return eventService.getAllEventByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("Private. Get event by id: {}, user id: {}", eventId, userId);
        return eventService.getEventByIdForUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable long userId,
                                @PathVariable long eventId,
                                @RequestBody @Valid UpdateEventUserRequestDto updateEvent) {
        log.info("Private. Update event with id: {}, user id: {}, {}", eventId, userId, updateEvent);
        return eventService.updateEvent(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    List<ParticipationRequestDto> getRequestByEvent(@PathVariable long userId,
                                                    @PathVariable long eventId) {
        log.info("Private. Get request by event id: {}, user id: {}", eventId, userId);
        return eventService.getRequestByEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResultDto updateEventRequest(@PathVariable long userId,
                                                         @PathVariable long eventId,
                                                         @RequestBody(required = false) @Valid
                                                         EventRequestStatusUpdateRequestDto requestsByEvent) {
        log.info("Private. Update event request, event id: {}, user id: {}", eventId, userId);
        return eventService.updateEventRequest(userId, eventId, requestsByEvent);
    }

}
