package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Category;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventShortDto {

    private LocalDateTime eventDate;

    private String annotation;

    private Category category;

    private Long confirmedRequests;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

}
