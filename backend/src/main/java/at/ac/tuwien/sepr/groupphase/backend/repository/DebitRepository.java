package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebitRepository extends JpaRepository<Debit, Long> {
}
