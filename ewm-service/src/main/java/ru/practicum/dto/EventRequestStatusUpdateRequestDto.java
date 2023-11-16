package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.RequestStatus;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequestDto {

    private List<Long> requestIds;

    private RequestStatus status;

}
