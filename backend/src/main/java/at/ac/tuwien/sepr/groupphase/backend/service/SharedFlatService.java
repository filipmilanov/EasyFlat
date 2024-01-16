package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
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
    SharedFlat findById(Long id, String jwt) throws AuthorizationException;

    /**
     * Create a shared flat.
     *
     * @param sharedFlat The shared flat to be created
     * @return WgDetailDto representing the created shared flat
     * @throws Exception if an error occurs during the creation process
     */
    WgDetailDto create(SharedFlat sharedFlat) throws ConflictException, ValidationException;

    /**
     * Log in to a shared flat.
     *
     * @param wgDetailDto The shared flat details for login
     * @return WgDetailDto representing the logged-in shared flat
     */
    WgDetailDto loginWg(SharedFlat wgDetailDto);

    /**
     * Delete a shared flat.
     *
     * @return WgDetailDto representing the deleted shared flat
     */
    WgDetailDto delete(String email);
}

