package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.LoginFlatEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoginFlatEndPointTest {

    @Mock
    private SharedFlatService sharedFlatService;

    @InjectMocks
    private LoginFlatEndpoint loginFlatEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginWg() {
        String authToken = "someAuthToken";
        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setName("name");
        sharedFlat.setPassword("password");
        WgDetailDto wgDetailDto = new WgDetailDto();
        wgDetailDto.setName("name");
        wgDetailDto.setPassword("password");

        when(sharedFlatService.loginWg(any(SharedFlat.class), any(ApplicationUser.class)))
            .thenReturn(wgDetailDto);

        WgDetailDto result = loginFlatEndpoint.loginWg(sharedFlat);

        assertEquals(wgDetailDto, result);
    }

    @Test
    void testDelete() throws AuthorizationException {
        String email = "example@example.com";
        WgDetailDto wgDetailDto = new WgDetailDto();
        wgDetailDto.setName("name");
        wgDetailDto.setPassword("password");

        when(sharedFlatService.delete(any(ApplicationUser.class)))
            .thenReturn(wgDetailDto);

        WgDetailDto result = loginFlatEndpoint.delete();

        assertEquals(wgDetailDto, result);
    }
}
