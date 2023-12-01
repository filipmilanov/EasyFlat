package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;

public interface SharedFlatService {
    WgDetailDto create(SharedFlat sharedFlat, String authToken) throws Exception;

    WgDetailDto loginWg(SharedFlat wgDetailDto, String authToken);

    WgDetailDto delete(String name);
}
