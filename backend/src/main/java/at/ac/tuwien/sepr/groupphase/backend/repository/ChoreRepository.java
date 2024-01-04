package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoreRepository extends JpaRepository<Chore, Long> {
    @Query("SELECT c FROM chore c WHERE c.sharedFlat.id = :id")
    List<Chore> findAllBySharedFlatId(@Param("id") Long id);

    @Query("SELECT c FROM chore c WHERE c.name = :name")
    Chore findByName(@Param("name") String name);

    @Query("SELECT c FROM chore c WHERE c.user = :user")
    boolean existsByUserId(@Param("user") Long user
    );
}
