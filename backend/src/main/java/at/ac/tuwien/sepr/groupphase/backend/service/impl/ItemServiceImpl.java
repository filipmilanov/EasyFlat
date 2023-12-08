package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemFromApiDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemStats;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemStatsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.IngredientService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ItemValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemRepository itemRepository;
    private final DigitalStorageService digitalStorageService;
    private final IngredientService ingredientService;
    private final ItemMapper itemMapper;
    private final ItemValidator itemValidator;
    private final ItemStatsRepository itemStatsRepository;
    private final Validator validator;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String openFoodFactsApi = "https://world.openfoodfacts.net/api/v2/product/";

    public ItemServiceImpl(ItemRepository itemRepository, DigitalStorageService digitalStorageService,
                           IngredientService ingredientService, ItemMapper itemMapper,
                           ItemValidator itemValidator, Validator validator,
                           ItemStatsRepository itemStatsRepository, RestTemplate restTemplate,
                           ObjectMapper objectMapper) {
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.ingredientService = ingredientService;
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.itemStatsRepository = itemStatsRepository;
        this.validator = validator;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Item> findById(Long id) {
        LOGGER.trace("findById({})", id);
        if (id == null) {
            return Optional.empty();
        }

        return itemRepository.findById(id);
    }

    @Override
    @Transactional
    public Item create(ItemDto itemDto) throws ConflictException, ValidationException {
        LOGGER.trace("create({})", itemDto);

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        itemValidator.validateForCreate(itemDto, digitalStorageList);

        List<Ingredient> ingredientList = findIngredientsAndCreateMissing(itemDto.ingredients());

        ItemStats curr = new ItemStats();
        curr.setDateOfPurchase(LocalDate.now());
        curr.setAmountSpendOn(itemDto.priceInCent());
        curr.setItemStatId(itemDto.itemId());
        List<ItemStats> itemStats = new ArrayList<>();
        itemStats.add(curr);
        itemStatsRepository.save(curr);

        Item item;
        if (itemDto.alwaysInStock()) {
            item = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, null);
        } else {
            item = itemMapper.dtoToEntity(itemDto, ingredientList, null);
        }
        Item createdItem = itemRepository.save(item);
        createdItem.setIngredientList(ingredientList);
        return createdItem;
    }

    @Override
    @Transactional
    public Item update(ItemDto itemDto) throws ConflictException, ValidationException {
        LOGGER.trace("update({})", itemDto);

        if (itemDto.alwaysInStock() == null) {
            itemDto = itemDto.withAlwaysInStock(false);
        }

        List<DigitalStorage> digitalStorageList = digitalStorageService.findAll(null);
        Item presistedItem = this.findById(itemDto.itemId()).orElseThrow(() -> new NotFoundException("Given Id does not exists in the Database!"));
        itemValidator.validateForUpdate(itemDto, digitalStorageList);

        List<Ingredient> ingredientList = findIngredientsAndCreateMissing(itemDto.ingredients());

        Item item;
        if (itemDto.alwaysInStock()) {
            item = itemMapper.dtoToAlwaysInStock(itemDto, ingredientList, null);
        } else {
            item = itemMapper.dtoToEntity(itemDto, ingredientList, null);
        }

        // necessary because JPA cannot convert an Entity to another Entity
        if (item.alwaysInStock() != presistedItem.alwaysInStock()) {
            this.delete(itemDto.itemId());
        }

        Item updatedItem = itemRepository.save(item);
        updatedItem.setIngredientList(ingredientList);
        return updatedItem;
    }

    @Override
    public void delete(Long id) {
        LOGGER.trace("delete({})", id);

        itemRepository.deleteById(id);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public ItemFromApiDto findItemByEan(Long ean) {

        LOGGER.trace("findItemByEan({})", ean);

        String requestString = openFoodFactsApi + ean;

        try {
            // Get data from API using EAN code
            String jsonResponse = restTemplate.getForObject(requestString, String.class);

            // Use JsonNode to get the values we need
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            String eanCode = rootNode.path("code").asText();

            String generalName = rootNode.path("product").path("generic_name_en").asText().toLowerCase();

            String productName = rootNode.path("product").path("product_name_en").asText();

            String brand = rootNode.path("product").path("brands").asText();

            Long totalQuantity = rootNode.path("product").path("product_quantity").asLong();

            String unit = rootNode.path("product").path("ecoscore_data").path("adjustments").path("packaging").path("packagings").get(0).path("quantity_per_unit_unit").asText();

            String description = rootNode.path("product").path("category_properties").path("ciqual_food_name:en").asText();

            String boughtAt = rootNode.path("product").path("stores").asText();

            Long status = rootNode.path("status").asLong();

            String statusText = rootNode.path("status_verbose").asText();

            return new ItemFromApiDto(
                eanCode,
                !Objects.equals(generalName, "") ? generalName : productName.toLowerCase(),
                productName,
                brand,
                totalQuantity,
                unit,
                description,
                boughtAt,
                status,
                statusText
            );

        } catch (HttpClientErrorException e) {
            LOGGER.error("Client error while fetching item by EAN: {}", ean);
        } catch (HttpServerErrorException e) {
            LOGGER.error("Server error while fetching item by EAN: {}", ean);
        } catch (ResourceAccessException e) {
            LOGGER.error("Connection error while fetching item by EAN: {}", ean);
        } catch (RestClientException e) {
            LOGGER.error("Unknown error while fetching item by EAN: {}", ean);
        } catch (IOException e) {
            LOGGER.error("Error parsing JSON response for item with EAN: {}", ean);
        }

        return null;
    }

    private List<Ingredient> findIngredientsAndCreateMissing(List<IngredientDto> ingredientDtoList) throws ConflictException {
        if (ingredientDtoList == null) {
            return List.of();
        }
        List<Ingredient> ingredientList = ingredientService.findByTitle(
            ingredientDtoList.stream()
                .map(IngredientDto::name)
                .toList()
        );

        List<IngredientDto> missingIngredients = ingredientDtoList.stream()
            .filter(ingredientDto ->
                ingredientList.stream()
                    .noneMatch(ingredient ->
                        ingredient.getTitle().equals(ingredientDto.name())
                    )
            ).toList();

        if (!missingIngredients.isEmpty()) {
            List<Ingredient> createdIngredients = ingredientService.createAll(missingIngredients);
            ingredientList.addAll(createdIngredients);
        }
        return ingredientList;
    }
}
