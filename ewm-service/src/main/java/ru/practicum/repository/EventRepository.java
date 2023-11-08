package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiatorId(long userId, PageRequest pageRequest);

    List<Event> findAllByIdIn(List<Long> ids);

    boolean existsAllByCategoryId(Long catId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

}
