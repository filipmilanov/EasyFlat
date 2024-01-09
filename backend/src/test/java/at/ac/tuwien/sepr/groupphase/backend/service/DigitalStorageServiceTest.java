package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
class DigitalStorageServiceTest {

    @Autowired
    private DigitalStorageService service;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SharedFlatService sharedFlatService;

    private ApplicationUser applicationUser;

    @BeforeEach
    public void cleanUp() throws ValidationException, ConflictException {
        testDataGenerator.cleanUp();

        applicationUser = userRepository.findById(1L).orElseThrow();
        when(authService.getUserFromToken()).thenReturn(applicationUser);
    }


    @Test
    void givenDigitalStorageIdWhenFindByIdThenDigitalStorageIsReturned() {
        // given
        Long id = 1L;


        // when
        Optional<DigitalStorage> actual = service.findById(id);

        // then
        assertAll(
            () -> assertTrue(actual.isPresent()),
            () -> assertThat(actual.get().getStorageId()).isEqualTo(id)
        );
    }

    @Test
    void givenInvalidDigitalStorageIdWhenFindByIdThenNothingIsReturned() {
        // given
        Long id = -1L;

        // when
        Optional<DigitalStorage> actual = service.findById(id);

        // then
        assertTrue(actual.isEmpty());
    }

    @Test
    void givenNothingWhenFindAllThenAllDigitalStoragesOfActiveUserAreReturned() throws AuthenticationException {
        // when
        List<DigitalStorage> actual = service.findAll(null);

        // then
        assertThat(actual).hasSizeGreaterThanOrEqualTo(1);
    }


    @Test
    @Disabled("Test does not work, because it tries to create second digital storage for a WG, " +
        "but one WG can have only one DS. It is still here, because of the opportunity to extend " +
        "the functionality of the app. ")
    void givenValidStorageWhenCreateThenStorageIsPersistedAndHasId() throws Exception {
        // given
        when(jwtTokenizer.getEmailFromToken(any(String.class))).thenReturn(applicationUser.getEmail());

        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setName("TestWG");
        sharedFlat.setPassword("1234");

        WgDetailDto wgDetailDto = sharedFlatService.create(sharedFlat);
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("MyTestStorage")
            .sharedFlat(wgDetailDto)
            .build();

        // when
        DigitalStorage actual = service.create(digitalStorageDto);

        // then
        Optional<DigitalStorage> persisted = service.findById(actual.getStorageId());

        assertAll(
            () -> assertTrue(persisted.isPresent()),
            () -> assertThat(actual).isEqualTo(persisted.get()),
            () -> assertThat(actual.getTitle()).isEqualTo(digitalStorageDto.title())
        );
    }

    @Test
    void givenInvalidStorageWhenCreateThenValidationExceptionIsThrown() {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("")
            .build();

        // when + then
        assertThrows(ValidationException.class, () -> service.create(digitalStorageDto));
    }

    @Test
    @Disabled
    void givenInvalidStorageWhenSearchItemsThenValidationExceptionIsThrown() {
        // given
        Long iD = -1111L;
        ItemSearchDto searchParams = new ItemSearchDto(null, null, null, null);

        // when + then
        assertThrows(ValidationException.class, () -> service.searchItems(searchParams));
    }

    @Test
    void givenValidSearchParamsWhenSearchItemsThenReturnList() throws ValidationException, AuthenticationException {
        // given
        ItemSearchDto searchParams = new ItemSearchDto(false, null, null, null);
        ItemListDto itemListDto = ItemListDtoBuilder.builder()
            .generalName("apples")
            .quantityCurrent(1.0)
            .quantityTotal(1.0)
            .storageId(1L)
            .unit(UnitDtoBuilder.builder().name("kg").build())
            .build();

        // when
        List<ItemListDto> result = service.searchItems(searchParams);

        // then
        assertAll(
            () -> assertThat(result).isNotEmpty(),
            () -> assertThat(result).contains(itemListDto)
        );
    }

    @Test
    void givenInvalidSearchParamsWhenSearchItemsThenThrowValidationException() {
        // given
        ItemSearchDto invalidSearchParams = new ItemSearchDto(null, null, null, null);

        // when + then
        assertThrows(ValidationException.class, () -> service.searchItems(invalidSearchParams));
    }
}