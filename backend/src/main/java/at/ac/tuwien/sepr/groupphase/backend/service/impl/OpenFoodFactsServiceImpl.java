package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpenFoodFactsItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi.OpenFoodFactsResponseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemFromOpenFoodFactsApiMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.OpenFoodFactApiException;
import at.ac.tuwien.sepr.groupphase.backend.service.OpenFoodFactsService;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class OpenFoodFactsServiceImpl implements OpenFoodFactsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RestTemplate restTemplate;
    private final ItemFromOpenFoodFactsApiMapper itemFromApiMapper;
    private final String openFoodFactsApi = "https://world.openfoodfacts.net/api/v2/product/";

    public OpenFoodFactsServiceImpl(RestTemplate restTemplate,
                                    ItemFromOpenFoodFactsApiMapper itemFromApiMapper) {
        this.restTemplate = restTemplate;
        this.itemFromApiMapper = itemFromApiMapper;
    }

    @Override
    public OpenFoodFactsItemDto findByEan(Long ean)
        throws ConflictException, RestClientException, JsonProcessingException, OpenFoodFactApiException {
        LOGGER.trace("findByEan({})", ean);

        String requestString = openFoodFactsApi + ean;

        try {
            // Get data from API using EAN code
            OpenFoodFactsResponseDto jsonResponse = restTemplate.getForObject(requestString, OpenFoodFactsResponseDto.class);

            if (jsonResponse == null) {
                throw new NotFoundException("JSON could not be obtained from API");
            }

            // Map the data to an ItemDto
            return itemFromApiMapper.mapFromJsonNode(jsonResponse);
        } catch (HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_GATEWAY) {
                // Handle the Bad Gateway error specifically
                throw new OpenFoodFactApiException("Open Food Facts API Error", List.of("Could not connect to API"));
            } else {
                // Re-throw other server errors
                throw new OpenFoodFactApiException("Open Food Facts API Error", List.of("Could not connect to API"));
            }
        } catch (RestClientException e) {
            if (e.getMessage().contains("404")) {
                throw new NotFoundException("EAN could not be found in API");
            } else {
                throw new OpenFoodFactApiException("Open Food Facts API Error", List.of("Error accessing the API"));
            }
        }
    }
}
