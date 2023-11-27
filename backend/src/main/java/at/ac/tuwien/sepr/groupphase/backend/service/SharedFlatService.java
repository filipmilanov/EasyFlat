package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGLoginDto;

public interface SharedFlatService {
    /**
     * Log in a user.
     *
     * @param wgLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String sharedFlatLogin(WGLoginDto wgLoginDto);

    WGCreateDto createFlat(WGCreateDto wgCreateDto);
}
