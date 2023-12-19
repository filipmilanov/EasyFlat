package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
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

import java.time.LocalDate;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ml;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
    private CustomUserDetailService customUserDetailService;

    private final String BASE_URI = "/api/v1/item";
    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(customUserDetailService.getUser(any(String.class))).thenReturn(applicationUser);
    }

    @Test
//    @Disabled("This test is not working because of it depends on previous tests")
    public void givenItemWhenCreateThenItemIsCreated() throws Exception {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(1L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .boughtAt("Hofer")
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .build();

        String body = objectMapper.writeValueAsString(itemDto);

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
                itemDto.ean(),
                itemDto.generalName(),
                itemDto.productName(),
                itemDto.brand(),
                itemDto.quantityCurrent(),
                itemDto.quantityTotal(),
                itemDto.unit(),
                itemDto.expireDate(),
                itemDto.description(),
                itemDto.priceInCent(),
                itemDto.digitalStorage(),
                itemDto.boughtAt()
            );
        assertThat(
            item.ingredients().stream()
                .map(IngredientDto::name)
                .toList()
        ).isEqualTo(
            itemDto.ingredients().stream()
                .map(IngredientDto::name)
                .toList()
        );
    }

    @Test
    public void givenInvalidStorageWhenCreateThenValidationException() throws Exception {
        // given

        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(1L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("2314")
            .generalName("")
            .productName(null)
            .brand("")
            .quantityCurrent(100.0)
            .quantityTotal(-200.0)
            .unit(UnitDtoBuilder.builder().build())
            .description("")
            .priceInCent(-1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .boughtAt("Hofer")
            .build();

        String body = objectMapper.writeValueAsString(itemDto);

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
                ;
                String[] errors = content.split(",");
                assertEquals(7, errors.length);
            }
        );
    }

    @Test
    public void givenInvalidStorageWhenCreateThenAuthenticationException() throws Exception {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("Test")
            .storageId(-909L)
            .build();
        List<IngredientDto> ingredientDtoList = List.of(
            IngredientDtoBuilder.builder()
                .name("Ingredient 1")
                .build(),
            IngredientDtoBuilder.builder()
                .name("Ingredient 2")
                .build()
        );

        ItemDto itemDto = ItemDtoBuilder.builder()
            .ean("0123456789123")
            .generalName("Test")
            .productName("MyTest")
            .brand("Hofer")
            .quantityCurrent(100.0)
            .quantityTotal(200.0)
            .unit(ml)
            .expireDate(LocalDate.now().plusYears(1))
            .description("This is valid description")
            .priceInCent(1234L)
            .digitalStorage(digitalStorageDto)
            .ingredients(ingredientDtoList)
            .boughtAt("Hofer")
            .build();

        String body = objectMapper.writeValueAsString(itemDto);

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
}