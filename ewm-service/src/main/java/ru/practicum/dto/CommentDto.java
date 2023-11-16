package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {

    private long id;

    private String text;

    private long eventId;

    private long userId;

    private LocalDateTime created;

}
