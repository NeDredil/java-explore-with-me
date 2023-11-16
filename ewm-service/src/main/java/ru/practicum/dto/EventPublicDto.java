package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import ru.practicum.model.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventPublicDto {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private boolean onlyAvailable;
    private EventSort sort;
    private PageRequest pageRequest;
    private HttpServletRequest request;
}
