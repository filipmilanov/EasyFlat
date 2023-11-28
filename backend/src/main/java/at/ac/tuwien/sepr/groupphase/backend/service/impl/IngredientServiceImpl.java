package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.IngredientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.IngredientValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final IngredientRepository ingredientRepository;
    private final IngredientValidator ingredientValidator;
    private final IngredientMapper ingredientMapper;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, IngredientValidator ingredientValidator, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientValidator = ingredientValidator;
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    public List<Ingredient> findAllByIds(List<Long> ids) {
        LOGGER.trace("findAllByIds({})", ids);
        if (ids == null) {
            return List.of();
        }

        return ingredientRepository.findAllById(ids);
    }

    @Override
    public List<Ingredient> createAll(List<IngredientDto> ingredientDtoList) throws ConflictException {
        LOGGER.trace("createAll({})", ingredientDtoList);

        ingredientValidator.checkIngredientListForCreate(ingredientDtoList);

        List<Ingredient> ingredientList = ingredientMapper.dtoListToEntityList(ingredientDtoList);
        return ingredientRepository.saveAll(ingredientList);
    }

    @Override
    public List<Ingredient> findByNames(List<String> names) {
        LOGGER.trace("findByNames({})", names);

        return ingredientRepository.findByNames(names);
    }
}
