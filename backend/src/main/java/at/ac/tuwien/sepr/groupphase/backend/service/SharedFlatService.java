package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;


public interface SharedFlatService {

    /**
     * Find a shared flat by its id.
     *
     * @param id  the id of the shared flat
     * @param jwt the jwt of the user
     * @return the shared flat
     */
    SharedFlat findById(Long id, String jwt) throws AuthenticationException;

    WgDetailDto create(SharedFlat sharedFlat) throws ConflictException, ValidationException;

    WgDetailDto loginWg(SharedFlat wgDetailDto);

    WgDetailDto delete() throws AuthorizationException;
}

