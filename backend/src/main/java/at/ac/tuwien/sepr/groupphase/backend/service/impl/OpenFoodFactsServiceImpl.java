package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemFromOpenFoodFactsApiMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.service.OpenFoodFactsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

@Service
public class OpenFoodFactsServiceImpl implements OpenFoodFactsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ItemFromOpenFoodFactsApiMapper itemFromApiMapper;
    private final String openFoodFactsApi = "https://world.openfoodfacts.net/api/v2/product/";

    public OpenFoodFactsServiceImpl(RestTemplate restTemplate,
                                    ObjectMapper objectMapper,
                                    ItemFromOpenFoodFactsApiMapper itemFromApiMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.itemFromApiMapper = itemFromApiMapper;
    }

    @Override
    public OpenFoodFactsItemDto findByEan(Long ean)
        throws ConflictException, RestClientException, JsonProcessingException {
        LOGGER.trace("findByEan({})", ean);

        String requestString = openFoodFactsApi + ean;

        // Get data from API using EAN code
        String jsonResponse = restTemplate.getForObject(requestString, String.class);

        // Use JsonNode to get the values we need
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        return itemFromApiMapper.mapFromJsonNode(rootNode);
    }
}
