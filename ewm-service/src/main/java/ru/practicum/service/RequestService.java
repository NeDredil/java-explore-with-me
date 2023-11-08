package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto save(long userId, long eventId);

    List<ParticipationRequestDto> getRequestByUser(long userId);

    ParticipationRequestDto cancelRequestByUser(long userId, long requestId);

}
