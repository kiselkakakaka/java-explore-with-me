package ru.practicum.ewm.main.event;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id = :initiatorId AND e.id = :eventId")
    Event findByIdAndInitiatorId(@Param("eventId") Long eventId,
                                 @Param("initiatorId") Long initiatorId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate >= :start) " +
            "AND (e.eventDate <= :end)")
    Page<Event> searchAdmin(@Param("users") Collection<Long> users,
                            @Param("states") Collection<EventState> states,
                            @Param("categories") Collection<Long> categories,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = ru.practicum.ewm.main.event.EventState.PUBLISHED " +
            "AND (:text IS NULL OR (" +
            "LOWER(e.annotation) LIKE :text OR " +
            "LOWER(e.description) LIKE :text" +
            ")) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate >= :start) " +
            "AND (e.eventDate <= :end)")
    Page<Event> searchPublic(@Param("text") String text,
                             @Param("categories") Collection<Long> categories,
                             @Param("paid") Boolean paid,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             Pageable pageable);
}