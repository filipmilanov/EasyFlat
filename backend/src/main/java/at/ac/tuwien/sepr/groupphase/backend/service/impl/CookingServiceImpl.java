package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.CookingEndPoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RecipeSuggestionDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeIngredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.RecipeSuggestion;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeSuggestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@Service
public class CookingServiceImpl implements CookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RestTemplate restTemplate;

    private final DigitalStorageServiceImpl digitalStorageService;

    private final String apiKey = "3b683601a4f44cd38d367ab0a1db032d";
    private final RecipeSuggestionRepository repository;

    private String apiUrl = "https://api.spoonacular.com/recipes/findByIngredients";


    public CookingServiceImpl(RestTemplate restTemplate, RecipeSuggestionRepository repository, DigitalStorageServiceImpl digitalStorageService) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.digitalStorageService = digitalStorageService;
    }

    @Override
    public List<RecipeSuggestionDto> getRecipeSuggestion(Long storId) throws ValidationException {

        List<ItemListDto> alwaysInStockItems = digitalStorageService.searchItems(storId, new ItemSearchDto(null, true, null, null, null));
        List<ItemListDto> notAlwaysInStockItems = digitalStorageService.searchItems(storId, new ItemSearchDto(null, false, null, null, null));

        List<ItemListDto> items = new LinkedList<>();
        items.addAll(alwaysInStockItems);
        items.addAll(notAlwaysInStockItems);

        String requestString = getRequestString(items);
        ResponseEntity<List<RecipeDto>> exchange = restTemplate.exchange(requestString, HttpMethod.GET, null, new ParameterizedTypeReference<List<RecipeDto>>() {
        });


        List<RecipeSuggestionDto> recipeSuggestions = new LinkedList<>();
        if (exchange.getBody() != null) {
            for (RecipeDto recipeDto : exchange.getBody()) {
                String recipeId = String.valueOf(recipeDto.id());
                String newReqString = "https://api.spoonacular.com/recipes/" + recipeId + "/information" + "?apiKey=" + apiKey + "&includeNutrition=false";

                ResponseEntity<RecipeSuggestionDto> response = restTemplate.exchange(newReqString, HttpMethod.GET, null, new ParameterizedTypeReference<RecipeSuggestionDto>() {
                });
                if (response.getBody() != null) {
                    recipeSuggestions.add(response.getBody());
                }
            }
        }


        return recipeSuggestions;
    }

    private String getRequestString(List<ItemListDto> items) {
        List<String> ingredients = new LinkedList<>();
        for (ItemListDto item : items) {
            ingredients.add(item.generalName());
        }

        String requestString = apiUrl;
        requestString += "?apiKey=" + apiKey;
        boolean isFirst = true;
        for (String ingredient : ingredients) {
            if (isFirst) {
                requestString += "&ingredients=" + ingredient;
                isFirst = false;
            } else {
                requestString += ",+" + ingredient;
            }
        }
        requestString += "&number=1";
        return requestString;
    }
}
