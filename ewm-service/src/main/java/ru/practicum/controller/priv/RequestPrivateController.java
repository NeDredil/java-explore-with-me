package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@PathVariable long userId,
                                               @RequestParam long eventId) {
        log.info("Save Request user id: {}, event id: {}", userId, eventId);
        return requestService.save(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestByUser(@PathVariable long userId) {
        log.info("GET Requests by user with id: {}", userId);
        return requestService.getRequestByUser(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByUser(@PathVariable long userId,
                                                       @PathVariable long requestId) {
        log.info("Cancel Request by user with id: {}, event with id: {}", userId, requestId);
        return requestService.cancelRequestByUser(userId, requestId);
    }

}
