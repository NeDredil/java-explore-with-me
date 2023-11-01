package ru.practicum;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {
    LocalDateTime start;
    LocalDateTime end;
    List<String> uris;
    Boolean unique;
}