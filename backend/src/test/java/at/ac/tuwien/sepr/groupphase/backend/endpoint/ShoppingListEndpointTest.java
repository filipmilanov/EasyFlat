package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LabelMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.LabelRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ShoppingListEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    ShoppingItemRepository shoppingItemRepository;

    private final String BASE_URI = "/api/v1/shopping";
    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }


    @Test
    void createShoppingItem() throws Exception {
        ShoppingItemDto shoppingItemDto = this.generateShoppingItemDto();

        String body = objectMapper.writeValueAsString(shoppingItemDto);

        MvcResult mvcResult = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ShoppingItemDto actual = objectMapper.readValue(response.getContentAsString(), ShoppingItemDto.class);

        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
            () -> assertThat(actual).isEqualTo(shoppingItemDto.withId(actual.itemId()))
        );
    }

    @Test
    void createList() throws Exception {
        ShoppingListDto shoppingListDto = this.generateShoppingListDto();

        String body = objectMapper.writeValueAsString(shoppingListDto);

        MvcResult mvcResult = mockMvc.perform(post(BASE_URI + "/list-create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shoppingListDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ShoppingListDto actual = objectMapper.readValue(response.getContentAsString(), ShoppingListDto.class);

        assertAll(
            () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE),
            () -> assertThat(actual).isEqualTo(shoppingListDto.withId(actual.id()))
        );
    }

    @Test
    public void testUpdate() throws Exception {
        Long existingId = 1L;
        ShoppingItemDto updatedDto = ShoppingItemDtoBuilder.builder()
            .itemId(existingId)
            .productName("Butter Croissant")
            .generalName("Snacks")
            .quantityCurrent(3.0)
            .priceInCent(350L)
            .boughtAt("Local Bakery")
            .alwaysInStock(false) //updated
            .minimumQuantity(null) // updated
            .unit(new UnitDto("pcs", null, Set.of())) // updated
            .shoppingList(new ShoppingListDto(2L, "Tech", 2)) // updated
            .build();

        MvcResult mvcResult = mockMvc.perform(put(BASE_URI + "/" + updatedDto.itemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ShoppingItemDto actual = objectMapper.readValue(response.getContentAsString(), ShoppingItemDto.class);

        assertAll(
            () -> assertNotNull(actual.itemId()),
            // check updated fields
            () -> assertEquals(updatedDto.alwaysInStock(), actual.alwaysInStock()),
            () -> assertEquals(updatedDto.minimumQuantity(), actual.minimumQuantity()),
            () -> assertEquals(updatedDto.unit(), actual.unit()),
            () -> assertEquals(updatedDto.shoppingList().id(), actual.shoppingList().id())
            );
    }

/*
    @Test
    public void updateInvalidShoppingItem_then409() throws Exception {
        MvcResult mvcResult = mockMvc.perform(put(BASE_URI + "/" + updated.itemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

 */

    private ShoppingItemDto generateShoppingItemDto() {
        UnitDto testUnitDto = UnitDtoBuilder.builder()
            .name("g")
            .subUnit(Set.of())
            .build();
        List<IngredientDto> ingredientDtoList = new ArrayList<>();
        ingredientDtoList.add(new IngredientDto(1L, "Ingredient 1"));
        ingredientDtoList.add(new IngredientDto(2L, "Ingredient 2"));
        ingredientDtoList.add(new IngredientDto(3L, "Ingredient 3"));
        List<ItemLabel> labels = labelRepository.findAll();
        ShoppingItemDto validShoppingItemDto = ShoppingItemDtoBuilder.builder()
            .ean("1234567890123")
            .generalName("fruit")
            .productName("apple")
            .brand("clever")
            .quantityCurrent(3.0)
            .quantityTotal(3.0)
            .unit(testUnitDto)
            .description("Manufactured in Bulgaria")
            .priceInCent(210L)
            .alwaysInStock(false)
            .boughtAt("billa")
            .ingredients(ingredientDtoList)
            .shoppingList(new ShoppingListDto(1L, "Shopping List (Default)", 0))
            .labels(labelMapper.itemLabelListToItemLabelDtoList(labels))
            .build();
        return validShoppingItemDto;
    }

    private ShoppingListDto generateShoppingListDto() {
        return new ShoppingListDto(null, "Test List", 0);
    }
}
