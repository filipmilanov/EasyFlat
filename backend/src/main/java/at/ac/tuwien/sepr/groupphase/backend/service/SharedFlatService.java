package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;


public interface SharedFlatService {

    /**
     * Find a shared flat by its id.
     *
     * @param id  the id of the shared flat
     * @param jwt the jwt of the user
     * @return the shared flat
     */
    SharedFlat findById(Long id, String jwt) throws AuthenticationException;

    /**
     * Create a shared flat.
     *
     * @param sharedFlat The shared flat to be created
     * @param authToken  The authentication token for authorization
     * @return WgDetailDto representing the created shared flat
     * @throws Exception if an error occurs during the creation process
     */
    WgDetailDto create(SharedFlat sharedFlat, String authToken) throws Exception;

    /**
     * Log in to a shared flat.
     *
     * @param wgDetailDto The shared flat details for login
     * @param authToken   The authentication token for authorization
     * @return WgDetailDto representing the logged-in shared flat
     */
    WgDetailDto loginWg(SharedFlat wgDetailDto, String authToken);

    /**
     * Delete a shared flat.
     *
     * @param email The email associated with the shared flat to be deleted
     * @return WgDetailDto representing the deleted shared flat
     */
    WgDetailDto delete(String email);
}

