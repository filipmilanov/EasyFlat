package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.finance;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@RecordBuilder
public record ExpenseSearchDto(
    String title,
    Long paidById,
    Double amountInCents,
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate createdAt
) {
}
