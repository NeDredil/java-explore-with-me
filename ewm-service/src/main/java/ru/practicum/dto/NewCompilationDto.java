package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
public class NewCompilationDto {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;

}
