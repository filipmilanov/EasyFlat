package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;


public interface SharedFlatService {

    /**
     * Find a shared flat by its id.
     *
     * @param id  the id of the shared flat
     * @param jwt the jwt of the user
     * @return the shared flat
     */
    SharedFlat findById(Long id, String jwt) throws AuthorizationException;

    WgDetailDto create(SharedFlat sharedFlat, String authToken) throws Exception;

    WgDetailDto loginWg(SharedFlat wgDetailDto, String authToken);

    WgDetailDto delete(String email);
}

