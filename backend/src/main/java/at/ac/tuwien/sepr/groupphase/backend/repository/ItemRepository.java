package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByGeneralNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrBoughtAtContainingIgnoreCase(
        String generalName,
        String brand,
        String broughtAt);

}
