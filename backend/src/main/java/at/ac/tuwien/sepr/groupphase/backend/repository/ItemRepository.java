package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByGeneralNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrBoughtAtContainingIgnoreCase(
        String generalName,
        String brand,
        String broughtAt);

    @Query("UPDATE Item i "
        + "SET i.quantityCurrent = :quantity "
        + "WHERE i.itemId = :itemId "
        + "AND i.digitalStorage.storageId = :storageId")
    Item updateItemQuantity(@Param("storageId") long storageId, @Param("itemId") long itemId, @Param("quantity") long quantity);

    @Query("SELECT i FROM Item i WHERE i.digitalStorage.storageId = :storageId AND "
        + "(:title IS NULL OR LOWER(i.generalName) LIKE LOWER(CONCAT('%', :title, '%'))) AND "
        + "(:fillLevel IS NULL OR "
        + "(:fillLevel = 'full' AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) > 0.4) OR "
        + "(:fillLevel = 'nearly_empty' AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) > 0.2 AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) < 0.4) OR "
        + "(:fillLevel = 'empty' AND ((cast(i.quantityCurrent as float ))/(cast(i.quantityTotal as float ))) < 0.2)) AND "
        + "(:alwaysInStock IS NULL OR TYPE(i) = :alwaysInStock) ")
    List<Item> searchItems(@Param("storageId") Long storageId,
                           @Param("title") String title,
                           @Param("fillLevel") String fillLevel,
                           @Param("alwaysInStock") Class alwaysInStock);

    @Query("SELECT i FROM Item i WHERE i.digitalStorage.storageId = :storageId AND "
        + ":generalName = i.generalName ")
    List<Item> getItemWithGeneralName(@Param("storageId") Long storageId,
                                      @Param("generalName") String generalName);

}
