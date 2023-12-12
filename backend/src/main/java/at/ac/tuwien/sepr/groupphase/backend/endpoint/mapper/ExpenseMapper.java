package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ExpenseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UserMapper.class, SharedFlatMapper.class, DebitMapper.class})
public abstract class ExpenseMapper {

    @Mapping(target = "debitUsers", source = "expense")
    public abstract ExpenseDto entityToExpenseDto(Expense expense);

    @Mapping(target = "splitBy", source = "debitUsers")
    @Mapping(target = "debitUsers", source = "expenseDto")
    public abstract Expense expenseDtoToExpense(ExpenseDto expenseDto);

    public SplitBy splitByFromDebitDtoList(List<DebitDto> debitUsersDtoList) {
        return debitUsersDtoList.stream()
            .findAny()
            .orElse(DebitDtoBuilder.builder().splitBy(null).build())
            .splitBy();
    }

}
