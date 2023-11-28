package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DigitalStorageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class StorageEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DigitalStorageRepository digitalStorageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DigitalStorageMapper digitalStorageMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;


    private final String BASE_URI = "/api/v1/storage";


    @Test
    public void givenStorageWhenCreateThenStorageCreated() throws Exception {
        // given
        DigitalStorageDto digitalStorageDto = new DigitalStorageDto(null, "MyTestStorage");

        String body = objectMapper.writeValueAsString(digitalStorageDto);

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

        DigitalStorage digitalStorageResponse = objectMapper.readValue(response.getContentAsString(),
            DigitalStorage.class);

        assertThat(digitalStorageResponse).extracting(DigitalStorage::getStorId).isNotNull();
        assertThat(digitalStorageResponse).extracting(DigitalStorage::getTitle).isEqualTo(digitalStorageDto.title());
    }

    @Test
    public void givenInvalidStorageWhenCreateThenException() throws Exception {
        // given
        DigitalStorageDto digitalStorageDto = new DigitalStorageDto(-11L, "MyTestStorage");

        String body = objectMapper.writeValueAsString(digitalStorageDto);

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
                assertThat(content).contains("The Id must be null");
            }
        );
    }


    @Test
    public void givenStorageIdAndSearchParametersWhenGetItemsThenItemsRetrieved() throws Exception {
        // Given
        Long storageId = 1L;
        String endpointUrl = BASE_URI + "/" + storageId;


        ItemSearchDto itemSearchDto = new ItemSearchDto(null, false, null, null, null, null, null, null);

        MvcResult mvcResult = this.mockMvc.perform(get(endpointUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .param("alwaysInStock", String.valueOf(itemSearchDto.alwaysInStock()))
                .param("productName", itemSearchDto.productName())
                .param("brand", itemSearchDto.brand())
                .param("fillLevel", itemSearchDto.fillLevel())
                .param("expireDateStart", itemSearchDto.expireDateStart() != null ? itemSearchDto.expireDateStart().toString() : null)
                .param("expireDateEnd", itemSearchDto.expireDateEnd() != null ? itemSearchDto.expireDateEnd().toString() : null))
            .andDo(print())
            .andReturn();

        // Assertions
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
            () -> assertNotNull(mvcResult.getResponse())

        );
    }

}