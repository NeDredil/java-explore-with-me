package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto save(NewCompilationDto compilationDto);

    CompilationDto update(long compId, NewCompilationDto compilationDto);

    void delete(long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long compId);

}
