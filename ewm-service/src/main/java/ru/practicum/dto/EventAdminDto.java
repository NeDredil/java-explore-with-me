package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import ru.practicum.model.EventState;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventAdminDto {

    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    private String rangeStart;

    private String rangeEnd;

    private PageRequest pageRequest;

}
