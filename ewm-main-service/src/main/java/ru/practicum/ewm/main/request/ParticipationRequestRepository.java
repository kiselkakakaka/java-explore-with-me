package ru.practicum.ewm.main.request;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    @Query("SELECT pr.event.id, COUNT(pr) " +
            "FROM ParticipationRequest pr " +
            "WHERE pr.status = ru.practicum.ewm.main.request.RequestStatus.CONFIRMED " +
            "AND pr.event.id IN :eventIds " +
            "GROUP BY pr.event.id")
    List<Object[]> getConfirmedRequestsForEvents(@Param("eventIds") List<Long> eventIds);
}
