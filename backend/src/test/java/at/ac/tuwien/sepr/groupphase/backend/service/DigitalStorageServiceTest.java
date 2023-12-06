package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
class DigitalStorageServiceTest {

    @Autowired
    private DigitalStorageService service;


    @Autowired
    private TestDataGenerator testDataGenerator;

    @BeforeEach
    private void cleanUp() {
        testDataGenerator.cleanUp();
    }


    @Test
    void givenDigitalStorageIdWhenFindByIdThenDigitalStorageIsReturned() {
        // given
        Long id = 1L;


        // when
        Optional<DigitalStorage> actual = service.findById(id);

        // then
        assertTrue(actual.isPresent());
        assertThat(actual.get().getStorId()).isEqualTo(id);
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
    void givenNothingWhenFindAllThenAllDigitalStoragesAreReturned() {
        // when
        List<DigitalStorage> actual = service.findAll(null);

        // then
        assertThat(actual).hasSizeGreaterThanOrEqualTo(5);
    }


    @Test
    void givenValidStorageWhenCreateThenStorageIsPersistedAndHasId() throws ConflictException, ValidationException {
        // given
        DigitalStorageDto digitalStorageDto = DigitalStorageDtoBuilder.builder()
            .title("MyTestStorage")
            .build();

        // when
        DigitalStorage actual = service.create(digitalStorageDto);

        // then
        Optional<DigitalStorage> persisted = service.findById(actual.getStorId());

        assertTrue(persisted.isPresent());
        assertThat(actual).isEqualTo(persisted.get());
        assertThat(actual.getTitle()).isEqualTo(digitalStorageDto.title());
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
    void givenInvalidStorageWhenSearchItemsThenValidationExceptionIsThrown() {
        // given
        Long iD= -1111L;
        ItemSearchDto searchParams = new ItemSearchDto(null,null,null, null,null);

        // when + then
        assertThrows(ValidationException.class, () -> service.searchItems(iD,searchParams));
    }

}