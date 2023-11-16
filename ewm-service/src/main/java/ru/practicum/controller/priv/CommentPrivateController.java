package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveComment(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Save comment for event with id: {}, user id: {}, {}", eventId, userId, newCommentDto);
        return commentService.saveComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comments/{comId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long comId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Update comment with id: {}, user id: {}, {}", comId, userId, newCommentDto);
        return commentService.updateComment(userId, comId, newCommentDto);
    }

    @DeleteMapping("/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long comId) {
        log.info("Delete comment with id: {}, user id: {}", comId, userId);
        commentService.deleteComment(userId, comId);
    }

}