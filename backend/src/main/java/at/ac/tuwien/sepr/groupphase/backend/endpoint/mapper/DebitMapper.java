package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DebitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Debit;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.SplitBy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UserMapper.class})
public abstract class DebitMapper {

    @Mapping(target = "user", source = "debit.id.user")
    @Mapping(target = "value", source = "debit.percent")
    @Mapping(target = "splitBy", expression = "java( splitBy )")
    public abstract DebitDto entityToDebitDto(Debit debit,
                                              SplitBy splitBy);


    public List<DebitDto> entityListToDebitDtoList(Expense expense) {
        return expense.getDebitUsers().stream()
            .map(debit ->
                entityToDebitDto(debit, expense.getSplitBy())
            ).toList();
    }
}
