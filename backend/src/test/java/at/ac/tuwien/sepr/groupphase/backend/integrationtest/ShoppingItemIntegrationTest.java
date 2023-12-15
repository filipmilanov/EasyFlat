package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingItemIntegrationTest implements TestData  {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    @Autowired
    private ItemMapper itemMapper;

    private final String baseUri = "/api/v1/shopping";
    private final ShoppingListDto shoppingListDto = new ShoppingListDto(1L, "Default");
    private final ApplicationUser testUser = new ApplicationUser(null, "", "", "user@email.com", "password", Boolean.FALSE, null);

    @Test
    @Order(1)
    public void createValidUserAndValidShoppingList() throws Exception {
        userRepository.save(testUser);
        MvcResult mvcResult = mockMvc.perform(post(baseUri + "/list-create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shoppingListDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }


    @Test
    public void testCreateValidShoppingItem_then201() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(this.baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validShoppingItemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    public void createInvalidShoppingItem_then409() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(this.baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidShoppingItemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());

    }

    @Test
    public void testUpdateValidShoppingItem_then200() throws Exception {
        ShoppingItem saved = shoppingItemRepository.save(itemMapper.dtoToShopping(validShoppingItemDto, null, null));
        ShoppingItemDto updated = new ShoppingItemDto(
            saved.getItemId(),
            "1234567890123",
            "apple",
            "apple2", // updated product name
            "hoffer",
            10L,
            20L,
            kg,
            LocalDate.now().plusDays(7),
            "Description",
            500L,
            true,
            5L,
            "Store",
            null,
            null,
            null,
            new ArrayList<>(Collections.singleton(new ItemLabelDto(null, "fruit", "#ff0000"))), // Labels
            new ShoppingListDto(1L, "Default"));
        MvcResult mvcResult = mockMvc.perform(put(this.baseUri + "/" + updated.itemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    public void updateInvalidShoppingItem_then409() throws Exception {
        ShoppingItem saved = shoppingItemRepository.save(itemMapper.dtoToShopping(validShoppingItemDto, null, null));
        ShoppingItemDto updated = new ShoppingItemDto(
            saved.getItemId(),
            "1234567890123",
            "apple",
            "apple1",
            "hoffer",
            10L,
            20L,
            g,
            LocalDate.now().plusDays(7),
            "Description",
            500L,
            true,
            null, // minimumQuantity set to null, although alwaysInStock is true
            "Store",
            null,
            null,
            null,
            new ArrayList<>(Collections.singleton(new ItemLabelDto(null, "fruit", "#ff0000"))), // Labels
            new ShoppingListDto(1L, "Default"));
        MvcResult mvcResult = mockMvc.perform(put(this.baseUri + "/" + updated.itemId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("user@email.com", USER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

}
