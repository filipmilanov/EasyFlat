package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByPaidByIs(ApplicationUser user);

    @Query("SELECT e FROM Expense e "
        + "WHERE (:title IS NULL OR UPPER(e.title) LIKE UPPER(CONCAT('%', :title, '%')))"
        + "AND (:paidById IS NULL OR e.paidBy.id = :paidById)"
        + "AND ((:startOfDay IS NULL AND :endOfDay IS NULL) OR (e.createdAt BETWEEN :startOfDay AND :endOfDay))"
        + "AND (:amountInCents IS NULL OR e.amountInCents >= :amountInCents)")
    List<Expense> findByCriteria(@Param("title") String title,
                                 @Param("paidById") Long paidById,
                                 @Param("amountInCents") Double amountInCents,
                                 @Param("startOfDay") LocalDateTime startOfDay,
                                 @Param("endOfDay") LocalDateTime endOfDay
    );

    List<Expense> findByPaidByIsIn(Set<ApplicationUser> users);

    List<Expense> findAllByPeriodInDaysIsNotNull();
}

