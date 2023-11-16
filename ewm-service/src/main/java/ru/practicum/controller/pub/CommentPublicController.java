package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{comId}")
    public CommentDto getCommentById(@PathVariable Long comId) {
        log.info("Get comment with id: {}", comId);
        return commentService.getCommentById(comId);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getCommentsByEventId(@PathVariable Long eventId,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get comments by event id: {}, from: {}, size: {}", eventId, from, size);
        return commentService.getCommentsByEventId(eventId, from, size);
    }

}
