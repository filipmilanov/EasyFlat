package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DigitalStorageRepository extends JpaRepository<DigitalStorage, Long> {

    @Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE digital_storage_seq RESTART WITH 1", nativeQuery = true)
    void resetSequence();

    List<DigitalStorage> findByTitleContaining(String title);

    @Query("UPDATE Item i "
        + "SET i.quantityCurrent = :quantity "
        + "WHERE i.itemId = :itemId "
        + "AND i.digitalStorage.storId = :storageId")
    Item updateItemQuantity(@Param("storageId") long storageId, @Param("itemId") long itemId, @Param("quantity") long quantity);

    @Query("SELECT i FROM Item i WHERE i.digitalStorage.storId = :storageId AND "
        + "(:title IS NULL OR LOWER(i.productName) LIKE LOWER(CONCAT('%', :title, '%'))) AND "
        + "(:brand IS NULL OR LOWER(i.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND "
        + "(:expireDateStart IS NULL OR  i.expireDate >= :expireDateStart) AND "
        + "(:expireDateEnd IS NULL OR (i.expireDate <= :expireDateEnd)) AND "
        + "(:fillLevel IS NULL OR "
        + "(:fillLevel = 'full' AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) > 0.4) OR "
        + "(:fillLevel = 'nearly_empty' AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) > 0.2 AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) < 0.4) OR "
        + "(:fillLevel = 'empty' AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) < 0.2)) AND "
        + "(:alwaysInStock IS NULL OR TYPE(i) = :alwaysInStock) ")
    List<Item> searchItems(@Param("storageId") Long storageId,
                           @Param("title") String title,
                           @Param("brand") String brand,
                           @Param("expireDateStart") LocalDate expireDateStart,
                           @Param("expireDateEnd") LocalDate expireDateEnd,
                           @Param("fillLevel") String fillLevel,
                           @Param("alwaysInStock") Class alwaysInStock);


    @Query("SELECT i FROM Item i WHERE i.digitalStorage.storId = :storId AND "
        + ":generalName = i.generalName")
    List<Item> getItemWithGeneralName(@Param("storId") Long storId,
                                      @Param("generalName") String generalName);
}



