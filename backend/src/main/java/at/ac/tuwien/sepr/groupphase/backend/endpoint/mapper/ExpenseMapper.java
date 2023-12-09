package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UserMapper.class, SharedFlatMapper.class, DebitMapper.class})
public abstract class ExpenseMapper {

    @Mapping(target = "debitUsers", source = "expense")
    public abstract ExpenseDto entityToExpenseDto(Expense expense);
}
