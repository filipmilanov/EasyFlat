package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
class DigitalStorageServiceTest {

    @Autowired
    private DigitalStorageService service;

    @Test
    void givenValidStorageWhenCreateThenStorageIsPersistedAndHasId() throws ValidationException {
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
}