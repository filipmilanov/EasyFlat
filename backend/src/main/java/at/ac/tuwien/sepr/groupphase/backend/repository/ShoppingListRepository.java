package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    /**
     * Retrieves a ShoppingList by its shopListId.
     *
     * @param shopListId The ID of the ShoppingList to retrieve.
     * @return An Optional containing the found ShoppingList, if present.
     */
    Optional<ShoppingList> getByShopListId(Long shopListId);

    /**
     * Retrieves all ShoppingLists associated with a SharedFlat.
     *
     * @param sharedFlat The SharedFlat entity to find associated ShoppingLists.
     * @return A list of ShoppingLists associated with the given SharedFlat.
     */
    List<ShoppingList> findBySharedFlatIs(SharedFlat sharedFlat);

    /**
     * Finds a ShoppingList by name and its associated SharedFlat.
     *
     * @param name       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return The found ShoppingList if it exists for the given name and SharedFlat.
     */
    ShoppingList findByNameAndSharedFlatIs(String name, SharedFlat sharedFlat);

    /**
     * Finds a ShoppingList by name and its associated SharedFlat.
     *
     * @param shopId       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return The found ShoppingList if it exists for the given name and SharedFlat.
     */
    @Query("SELECT sl FROM ShoppingList sl WHERE sl.shopListId = :shopId AND sl.sharedFlat = :sharedFlat")
    ShoppingList findByIdAndSharedFlatIs(Long shopId, SharedFlat sharedFlat);

    /**
     * Retrieves a ShoppingList by name and its associated SharedFlat.
     *
     * @param name       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return An Optional containing the found ShoppingList, if present.
     */
    Optional<ShoppingList> getByNameAndSharedFlatIs(String name, SharedFlat sharedFlat);

    /**
     * Retrieves a ShoppingList by its shopListId and its associated SharedFlat.
     *
     * @param id         The ID of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return An Optional containing the found ShoppingList, if present.
     */
    Optional<ShoppingList> getByShopListIdAndSharedFlatIs(Long id, SharedFlat sharedFlat);

    /**
     * Searches for ShoppingLists based on ShoppingList name.
     *
     * @param name       The name of the ShoppingList.
     * @param sharedFlat The SharedFlat entity associated with the ShoppingList.
     * @return A list of ShoppingLists based on the criteria.
     */
    List<ShoppingList> findAllByNameContainingIgnoreCaseAndSharedFlatIs(String name, SharedFlat sharedFlat);

    /**
     * Deletes a ShoppingList by its ID.
     *
     * @param listId The ID of the ShoppingList to be deleted.
     */
    @Modifying
    @Query("DELETE FROM ShoppingList sl WHERE sl.shopListId = :listId")
    void deleteByListId(@Param("listId") Long listId);
}
