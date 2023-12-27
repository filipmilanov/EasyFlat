package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ItemFromOpenFoodFactsApiMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final IngredientService ingredientService;
    private final UnitService unitService;
    private final UnitMapper unitMapper;

    public ItemFromOpenFoodFactsApiMapper(IngredientService ingredientService,
                                          UnitService unitService,
                                          UnitMapper unitMapper) {
        this.ingredientService = ingredientService;
        this.unitService = unitService;
        this.unitMapper = unitMapper;
    }

    public OpenFoodFactsItemDto mapFromJsonNode(OpenFoodFactsResponseDto openFoodFactsResponseDto) throws ConflictException {
        LOGGER.trace("mapFromJsonNode({})", openFoodFactsResponseDto);

        if (openFoodFactsResponseDto.status()) {

            String ean = openFoodFactsResponseDto.eanCode();
            String generalName = Optional.ofNullable(
                openFoodFactsResponseDto.product().genericName()
            ).orElse(
                Optional.ofNullable(
                    openFoodFactsResponseDto.product().genericNameEn()
                ).orElse(
                    openFoodFactsResponseDto.product().genericNameDe()
                )
            );
            String productName = Optional.ofNullable(
                openFoodFactsResponseDto.product().productName()
            ).orElse(
                Optional.ofNullable(
                    openFoodFactsResponseDto.product().productNameEn()
                ).orElse(
                    openFoodFactsResponseDto.product().productNameEn()
                )
            );
            String brand = openFoodFactsResponseDto.product().brands();
            Long totalQuantity = openFoodFactsResponseDto.product().productQuantity();
            String unit = openFoodFactsResponseDto.product().ecoscoreData().adjustments().packaging().packagings().get(0).unit();
            String description = openFoodFactsResponseDto.product().categoryProperties().description();
            String boughtAt = openFoodFactsResponseDto.product().boughtAt();
            List<OpenFoodFactsIngredientDto> ingredientList = openFoodFactsResponseDto.product().ingredients();

            List<Ingredient> ingredients = null;

            if (!ingredientList.isEmpty()) {
                // Create a pattern to match non-letter characters - because every ingredient should only consist of letters
                Pattern nonLetterPattern = Pattern.compile("[^\\p{L}]+");

                List<IngredientDto> ingredientDtoList = ingredientList.stream()
                    .map(OpenFoodFactsIngredientDto::text) // Extract text
                    .map(text -> nonLetterPattern.matcher(text).replaceAll("")) // Remove non-letter characters
                    .map(cleanedText -> IngredientDtoBuilder.builder()
                        .name(cleanedText)
                        .build())
                    .collect(Collectors.toList());

                ingredients = ingredientService.findIngredientsAndCreateMissing(ingredientDtoList);
            }

            UnitDto unitDto = null;
            try {
                unitDto = unitMapper.entityToUnitDto(unitService.findByName(unit));
            } catch (NotFoundException e) {
                LOGGER.info("Unit {} not found in database", unit);
            }

            return new OpenFoodFactsItemDto(
                ean,
                !Objects.equals(generalName, "") ? generalName : productName,
                productName,
                brand,
                totalQuantity,
                unitDto,
                description,
                boughtAt,
                ingredients
            );
        } else {
            throw new NotFoundException("EAN not found in API");
        }
    }
}
