package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalStorageRepository extends JpaRepository<DigitalStorage, Long> {

    List<DigitalStorage> findByTitleContainingAndSharedFlatIs(String title, SharedFlat sharedFlat);
}



