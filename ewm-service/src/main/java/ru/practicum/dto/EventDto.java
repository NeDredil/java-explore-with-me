package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Category;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventDto {

    private LocalDateTime createdOn;

    private String description;

    private String eventDate;

    private Location location;

    private Long participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String annotation;

    private Category category;

    private Long confirmedRequests;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

}
