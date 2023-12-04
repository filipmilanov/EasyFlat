package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ItemStatsRepository extends JpaRepository<ItemStats, Long> {
}
