package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventPublicDto;
import ru.practicum.model.EventSort;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventPublicController {

    private final EventService eventPublicService;

    @GetMapping
    List<EventDto> getEvent(@RequestParam(required = false) String text,
                            @RequestParam(required = false) List<Long> categories,
                            @RequestParam(required = false) Boolean paid,
                            @RequestParam(required = false) String rangeStart,
                            @RequestParam(required = false) String rangeEnd,
                            @RequestParam(defaultValue = "false") boolean onlyAvailable,
                            @RequestParam(required = false) EventSort sort,
                            @RequestParam(defaultValue = "0") int from,
                            @RequestParam(defaultValue = "10") int size,
                            HttpServletRequest request) {
        log.info("Public. Get event");
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventPublicService.getEvent(new EventPublicDto(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, pageRequest, request));
    }

    @GetMapping("/{id}")
    EventDto getEventById(@PathVariable long id, HttpServletRequest request) {
        log.info("Public. Get event by id: {}", id);
        return eventPublicService.getEventById(id, request);
    }

}
