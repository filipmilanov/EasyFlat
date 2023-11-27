package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DigitalStorageRepository extends JpaRepository<DigitalStorage, Long> {
    @Query("SELECT i FROM Item i WHERE i.digitalStorage.storId = :storageId AND "
        + "(:title IS NULL OR LOWER(i.productName) LIKE LOWER(CONCAT('%', :title, '%'))) AND "
        + "(:brand IS NULL OR LOWER(i.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND "
        + "(:expireDateStart IS NULL OR "
        + "(i.expireDate >= :expireDateStart AND "
        + "(:expireDateEnd IS NULL OR i.expireDate <= :expireDateEnd))) AND "
        + "(:fillLevel IS NULL OR i.quantityCurrent = :fillLevel)")
    List<Item> searchItems(@Param("storageId") Long storageId,
                           @Param("title") String title,
                           @Param("brand") String brand,
                           @Param("expireDateStart") LocalDate expireDateStart,
                           @Param("expireDateEnd") LocalDate expireDateEnd,
                           @Param("fillLevel") Long fillLevel);
}
