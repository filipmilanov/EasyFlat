package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingRepository extends JpaRepository<ShoppingItem, Long> {
    @Query("SELECT e FROM ShoppingItem e WHERE e.shoppingList.shopListId = :listId")
    List<ShoppingItem> findByShoppingListId(@Param("listId") Long listId);

    @Query("SELECT i FROM ShoppingItem i LEFT JOIN i.labels il WHERE i.shoppingList.name = :name AND "
        + "(:productName IS NULL OR LOWER(i.generalName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND "
        + "(:label IS NULL OR LOWER(il.labelValue) LIKE LOWER(CONCAT('%', :label, '%')))")
    List<ShoppingItem> searchItems(@Param("name") String name,
                                   @Param("productName") String productName,
                                   @Param("label") String label);
}
