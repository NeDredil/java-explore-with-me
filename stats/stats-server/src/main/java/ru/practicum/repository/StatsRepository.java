package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Stats;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query(value = "SELECT new ru.practicum.model.ViewStats(" +
            "s.app.name as app, s.uri as uri, COUNT(s.ip) as hits) " +
            "FROM Stats s " +
            "WHERE s.timestamp between :start AND :end " +
            "AND uri in ( :uris ) " +
            "GROUP BY s.app.name, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.model.ViewStats(" +
            "s.app.name as app, s.uri as uri, COUNT(s.ip) as hits) " +
            "FROM Stats s " +
            "WHERE s.timestamp between :start AND :end " +
            "GROUP BY s.app.name, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsWithOutUris(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.model.ViewStats(" +
            "s.app.name as app, s.uri as uri, COUNT(DISTINCT s.ip) as hits) " +
            "FROM Stats s " +
            "WHERE s.timestamp between :start AND :end " +
            "AND uri in ( :uris ) " +
            "GROUP BY s.app.name, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.model.ViewStats(" +
            "s.app.name as app, s.uri as uri, COUNT(DISTINCT s.ip) as hits) " +
            "FROM Stats s " +
            "WHERE s.timestamp between :start AND :end " +
            "GROUP BY s.app.name, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsUniqueWithOutUris(LocalDateTime start, LocalDateTime end);

}
