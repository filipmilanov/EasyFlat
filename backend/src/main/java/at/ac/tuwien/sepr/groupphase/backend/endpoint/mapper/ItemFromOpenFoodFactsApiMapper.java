package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsIngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ItemFromOpenFoodFactsApiMapper {

    private final IngredientService ingredientService;

    public ItemFromOpenFoodFactsApiMapper(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    public OpenFoodFactsItemDto mapFromJsonNode(OpenFoodFactsResponseDto openFoodFactsResponseDto) throws ConflictException {


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

            return new OpenFoodFactsItemDto(
                ean,
                !Objects.equals(generalName, "") ? generalName : productName,
                productName,
                brand,
                totalQuantity,
                unit,
                description,
                boughtAt,
                ingredients
            );
        } else {
            throw new NotFoundException("EAN not found in API");
        }
    }
}
