package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventAdminDto;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.UpdateEventAdminRequestDto;
import ru.practicum.model.EventState;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventDto> searchEvent(@RequestParam(required = false) List<Long> users,
                                      @RequestParam(required = false) List<EventState> states,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Admin. Search event");
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventService.searchEvent(new EventAdminDto(users, states, categories, rangeStart, rangeEnd, pageRequest));
    }

    @PatchMapping("/{eventId}")
    public EventDto updateAdminByEvent(@PathVariable long eventId,
                                       @RequestBody @Valid UpdateEventAdminRequestDto eventDto) {
        log.info("Admin. Update event with id: {}, {}", eventId, eventDto);
        return eventService.updateAdminByEvent(eventId, eventDto);
    }

}
