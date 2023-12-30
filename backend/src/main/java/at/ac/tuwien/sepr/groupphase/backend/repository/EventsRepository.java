package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EventsRepository extends JpaRepository<Event, Long> {

    List<Event> getBySharedFlatIs(SharedFlat sharedFlat);

    @Query("SELECT e FROM Event e JOIN e.labels l "
        + "WHERE ((:labelName IS NULL OR :labelName = '') OR l.labelName LIKE CONCAT('%', :labelName, '%')) "
        + "AND e.sharedFlat.id = :sharedFlatId")
    List<Event> findEventsByLabelNameAndSharedFlatId(
        @Param("labelName") String labelName,
        @Param("sharedFlatId") Long sharedFlatId
    );


}
