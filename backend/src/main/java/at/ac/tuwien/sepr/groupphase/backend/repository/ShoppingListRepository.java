package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> getByShopListId(Long shopListId);

    List<ShoppingList> findBySharedFlatIs(SharedFlat sharedFlat);

    ShoppingList findByNameAndSharedFlatIs(String name, SharedFlat sharedFlat);

    Optional<ShoppingList> getByNameAndSharedFlatIs(String name, SharedFlat sharedFlat);

    Optional<ShoppingList> getByShopListIdAndSharedFlatIs(Long id, SharedFlat sharedFlat);
}
