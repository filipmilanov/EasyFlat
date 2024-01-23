package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.SharedFlatEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SharedFlatEndpointTest {

    @Mock
    private SharedFlatService sharedFlatService;

    @InjectMocks
    private SharedFlatEndpoint sharedFlatEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() throws Exception {
        String authToken = "someAuthToken";
        WgDetailDto sharedFlat = new WgDetailDto();
        sharedFlat.setName("name");
        sharedFlat.setPassword("password");
        WgDetailDto wgDetailDto = new WgDetailDto();
        wgDetailDto.setName("name");
        wgDetailDto.setPassword("password");

        when(sharedFlatService.create(any(WgDetailDto.class)))
            .thenReturn(wgDetailDto);

        WgDetailDto result = sharedFlatEndpoint.create(sharedFlat);

        assertEquals(wgDetailDto, result);
    }
}


