package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ItemFromOpenFoodFactsApiMapper {

    private final IngredientService ingredientService;

    public ItemFromOpenFoodFactsApiMapper(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    public OpenFoodFactsItemDto mapFromJsonNode(JsonNode rootNode) throws ConflictException {

        long status = rootNode.path("status").asLong();

        if (status != 0) {
            String eanCode = Optional.ofNullable(rootNode.path("code").asText()).orElse("");

            String generalName = Optional.of(rootNode.path("product").path("generic_name_en").asText().toLowerCase()).orElse("");

            String productName = Optional.ofNullable(rootNode.path("product").path("product_name_en").asText()).orElse("");

            String brand = Optional.ofNullable(rootNode.path("product").path("brands").asText()).orElse("");

            Long totalQuantity = Optional.of(rootNode.path("product").path("product_quantity").asLong()).orElse(-1L);

            JsonNode unitPath = rootNode.path("product").path("ecoscore_data").path("adjustments").path("packaging").path("packagings");
            String unit = "";
            if (unitPath.isArray() && !unitPath.isEmpty()) {
                JsonNode firstPackagingNode = unitPath.get(0);
                if (firstPackagingNode != null) {
                    unit = firstPackagingNode.path("quantity_per_unit_unit").asText();
                }
            }

            String description = Optional.ofNullable(rootNode.path("product").path("category_properties").path("ciqual_food_name:en").asText()).orElse("");

            String boughtAt = Optional.ofNullable(rootNode.path("product").path("stores").asText()).orElse("");

            String ingredientList = Optional.ofNullable(rootNode.path("product").path("ingredients_text_en").asText()).orElse("");

            List<Ingredient> ingredients = null;

            if (!ingredientList.isEmpty()) {
                List<IngredientDto> ingredientDtoList = Arrays.stream(ingredientList.split(","))
                    .map(String::trim)
                    .map(ingredientName -> IngredientDtoBuilder.builder()
                        .name(ingredientName)
                        .build())
                    .toList();

                ingredients = ingredientService.findIngredientsAndCreateMissing(ingredientDtoList);
            }

            return new OpenFoodFactsItemDto(
                eanCode,
                !Objects.equals(generalName, "") ? generalName : productName.toLowerCase(),
                productName,
                brand,
                totalQuantity,
                unit,
                description,
                boughtAt,
                ingredients
            );
        } else {
            return null;
        }
    }
}
