package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedFlatRepository extends JpaRepository<SharedFlat, Long> {
    SharedFlat findFirstByName(String name);

    void deleteByName(String name);

    SharedFlat findFlatByName(String flatName);

    Integer findFlatIdByName(String flatName);
}
