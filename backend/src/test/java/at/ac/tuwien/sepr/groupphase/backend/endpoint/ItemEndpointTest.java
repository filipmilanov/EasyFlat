package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorageItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.invalidItemDto;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.itemDtoWithInvalidDigitalStorage;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.validItemDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ItemEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Autowired
    private ItemService itemService;

    private final String BASE_URI = "/api/v1/item";
    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test
    @DisplayName("Given valid item when create then item is created")
    public void givenItemWhenCreateThenItemIsCreated() throws Exception {
        // given

        String body = objectMapper.writeValueAsString(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ItemDto item = objectMapper.readValue(response.getContentAsString(),
            ItemDto.class);

        Assertions.assertThat(item)
            .extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                ItemDto::digitalStorage,
                ItemDto::boughtAt
            )
            .containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage(),
                validItemDto.boughtAt()
            );
        assertThat(
            item.ingredients().stream()
                .map(IngredientDto::name)
                .toList()
        ).isEqualTo(
            validItemDto.ingredients().stream()
                .map(IngredientDto::name)
                .toList()
        );
    }

    @Test
    @DisplayName("Given item when create then item is created with alternative names")
    public void givenInvalidStorageWhenCreateThenValidationException() throws Exception {
        // given


        String body = objectMapper.writeValueAsString(invalidItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                String[] errors = content.split(",");
                assertEquals(7, errors.length);
            }
        );
    }

    @Test
    @DisplayName("Given item when create then item is created with alternative names")
    public void givenInvalidStorageWhenCreateThenAuthenticationException() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(itemDtoWithInvalidDigitalStorage);

        // when
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        // then
        assertAll(
            () -> assertEquals(HttpStatus.CONFLICT.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                assertThat(content).contains("not");
            }
        );
    }

    @Test
    @DisplayName("Does findAllDelivers all items with limit")
    public void doesFindAllDeliversAllItemsWithLimit() throws Exception {
        // given
        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI)
                .param("limit", "5")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> {
                String content = response.getContentAsString();
                List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
                });
                assertThat(items.size()).isEqualTo(5);
            }
        );
    }

    @Test
    @DisplayName("Does findById delivers item")
    public void doesFindByIdDeliversItem() throws Exception {
        // given
        DigitalStorageItem item = itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/" + item.getItemId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        ItemDto item2 = objectMapper.readValue(content, ItemDto.class);
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(item2.itemId()).isEqualTo(item.getItemId()),
            () -> assertThat(item2).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                item2.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByField boughtAt delivers relevant items")
    public void doesFindByFieldDeliversRelevantItems() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/search")
                .param("boughtAt", "Hofer")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(1),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByField brand delivers relevant items")
    public void doesFindByFieldDeliversRelevantItemsBrand() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/search")
                .param("brand", "Hofer")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(1),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByField generalName delivers relevant items")
    public void doesFindByFieldDeliversRelevantItemsGeneralName() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/search")
                .param("brand", "Test")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(1),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }

    @Test
    @DisplayName("Does findByGeneralName delivers correct results")
    public void doesFindByGeneralNameDeliversCorrectResults() throws Exception {
        // given
        itemService.create(validItemDto);

        // when
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URI + "/general-name/Test")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        List<ItemDto> items = objectMapper.readValue(content, new TypeReference<>() {
        });
        // then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertThat(items.size()).isEqualTo(1),
            () -> assertThat(items.get(0)).extracting(
                ItemDto::ean,
                ItemDto::generalName,
                ItemDto::productName,
                ItemDto::brand,
                ItemDto::quantityCurrent,
                ItemDto::quantityTotal,
                ItemDto::unit,
                ItemDto::expireDate,
                ItemDto::description,
                ItemDto::priceInCent,
                (i) -> i.digitalStorage().storageId(),
                ItemDto::boughtAt
            ).containsExactly(
                validItemDto.ean(),
                validItemDto.generalName(),
                validItemDto.productName(),
                validItemDto.brand(),
                validItemDto.quantityCurrent(),
                validItemDto.quantityTotal(),
                validItemDto.unit(),
                validItemDto.expireDate(),
                validItemDto.description(),
                validItemDto.priceInCent(),
                validItemDto.digitalStorage().storageId(),
                validItemDto.boughtAt()
            ),
            () -> assertThat(
                items.get(0).ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            ).isEqualTo(
                validItemDto.ingredients().stream()
                    .map(IngredientDto::name)
                    .toList()
            )
        );
    }


}