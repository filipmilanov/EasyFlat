package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.SharedFlatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SharedFlatServiceTest {
    @Mock
    private SharedFlatService sharedFlatService;

    @Test
    void testCreateNewSharedFlat_Success() throws Exception {
        String authToken = "someAuthToken";
        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setName("name");
        sharedFlat.setPassword("password");
        WgDetailDto wgDetailDto = new WgDetailDto();
        wgDetailDto.setName("name");
        wgDetailDto.setPassword("password");

        when(sharedFlatService.create(any(SharedFlat.class), any(String.class)))
            .thenReturn(wgDetailDto);

        WgDetailDto result = sharedFlatService.create(sharedFlat, authToken);

        assertEquals(wgDetailDto, result);
    }

    @Test
    void testCreateNewSharedFlat_Failure() throws Exception {
        String authToken = "someAuthToken";
        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setName("name");
        sharedFlat.setPassword("password");
        WgDetailDto wgDetailDto = new WgDetailDto();
        wgDetailDto.setName("flatName");
        wgDetailDto.setPassword("flatPassword");

        when(sharedFlatService.create(any(SharedFlat.class), any(String.class)))
            .thenReturn(wgDetailDto);

        WgDetailDto result = sharedFlatService.create(sharedFlat, authToken);

        assertEquals(wgDetailDto, result);
    }
}
