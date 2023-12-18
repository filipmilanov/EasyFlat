package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<DigitalStorageItem, Long> {

    List<DigitalStorageItem> findAllByItemCache_GeneralNameContainingIgnoreCaseOrItemCache_BrandContainingIgnoreCaseOrItemCache_ProductNameContainingIgnoreCase(
        String generalName,
        String brand,
        String productName);

}
