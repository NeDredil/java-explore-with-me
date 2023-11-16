package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.EventStateAction;
import ru.practicum.model.Location;

import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class UpdateEventAdminRequestDto {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;


}
