package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;


public interface SharedFlatService {

    /**
     * Find a shared flat by its id.
     *
     * @param id  the id of the shared flat
     * @param jwt the jwt of the user
     * @return the shared flat
     */
    SharedFlat findById(Long id, String jwt) throws AuthenticationException;

    WgDetailDto create(SharedFlat sharedFlat, ApplicationUser user) throws Exception;

    WgDetailDto loginWg(SharedFlat wgDetailDto, ApplicationUser user);

    WgDetailDto delete(ApplicationUser user) throws AuthorizationException;
}

