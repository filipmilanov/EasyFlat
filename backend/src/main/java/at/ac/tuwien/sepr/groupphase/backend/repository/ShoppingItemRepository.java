package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {
    /**
     * Finds ShoppingItems by ShoppingList ID.
     *
     * @param listId The ID of the ShoppingList.
     * @return A list of ShoppingItems associated with the given ShoppingList ID.
     */
    @Query("SELECT e FROM ShoppingItem e WHERE e.shoppingList.shopListId = :listId")
    List<ShoppingItem> findByShoppingListId(@Param("listId") Long listId);

    /**
     * Searches for ShoppingItems based on ShoppingList name, product name, and label.
     *
     * @param name        The name of the ShoppingList.
     * @param productName The name of the product to search for (can be null).
     * @param label       The label value to search for (can be null).
     * @return A list of ShoppingItems based on the criteria.
     */
    @Query("SELECT i FROM ShoppingItem i LEFT JOIN i.labels il WHERE i.shoppingList.name = :name AND "
        + "(:productName IS NULL OR LOWER(i.generalName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND "
        + "(:label IS NULL OR LOWER(il.labelValue) LIKE LOWER(CONCAT('%', :label, '%')))")
    List<ShoppingItem> searchItems(@Param("name") String name,
                                   @Param("productName") String productName,
                                   @Param("label") String label);

    ShoppingItem findFirstByProductName(String productName);

    List<ShoppingItem> searchItemsByShoppingListNameAndShoppingListSharedFlatIdAndProductName(String shoppingListName, Long shardFlatId, String banana);
}
