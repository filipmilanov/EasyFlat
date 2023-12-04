package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SharedFlatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SharedFlatRepositoryTest {

    @Mock
    private SharedFlatRepository sharedFlatRepository;

    @InjectMocks
    private SharedFlatService sharedFlatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindFirstByName() {
        // Mock data
        String flatName = "Flat1";
        SharedFlat sharedFlat = new SharedFlat(/* add necessary shared flat details */);

        // Mock repository method
        when(sharedFlatRepository.findFirstByName(flatName))
            .thenReturn(sharedFlat);

        // Call the repository method
        SharedFlat result = sharedFlatRepository.findFirstByName(flatName);

        // Assert the result
        assertEquals(sharedFlat, result);
    }

    @Test
    void testDeleteByName() {
        String flatName = "Flat1";

        sharedFlatRepository.deleteByName(flatName);

        verify(sharedFlatRepository, times(1)).deleteByName(flatName);
    }
}

