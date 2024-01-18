package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemOrderType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class StorageEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;


    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private SharedFlatService sharedFlatService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    private final String BASE_URI = "/api/v1/storage";
    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }

    @Test

    public void givenStorageIdAndSearchParametersWhenGetItemsThenItemsRetrieved() throws Exception {
        // Given

        String endpointUrl = BASE_URI + "/items";

        // when
        ItemSearchDto itemSearchDto = new ItemSearchDto(false, null, null, null, null);

        MvcResult mvcResult = this.mockMvc.perform(get(endpointUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("alwaysInStock", String.valueOf(itemSearchDto.alwaysInStock()))
                .param("productName", itemSearchDto.productName()))
            .andDo(print())
            .andReturn();

        // Then
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
            () -> assertNotNull(mvcResult.getResponse())
        );
    }

    @Test
    public void givenStorageIdAndOrderTypeNameWhenGetItemsThenItemsRetrievedInCorrectOrder() throws Exception {
        // Given

        String endpointUrl = BASE_URI + "/items";


        ItemSearchDto itemSearchDto = ItemSearchDtoBuilder.builder()
            .alwaysInStock(false)
            .orderType(ItemOrderType.GENERAL_NAME)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(get(endpointUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("alwaysInStock", String.valueOf(itemSearchDto.alwaysInStock()))
                .param("orderType", itemSearchDto.orderType().toString())
                .param("desc", false + ""))
            .andDo(print())
            .andReturn();

        // Assertions
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
            () -> assertNotNull(mvcResult.getResponse()),
            () -> assertThat(mvcResult.getResponse()
                .getContentAsString()
                .substring(1, mvcResult.getResponse().getContentAsString().length() - 1)
                .toLowerCase()
                .split("\\{\"generalName\"")
            ).isSorted()
        );
    }

}