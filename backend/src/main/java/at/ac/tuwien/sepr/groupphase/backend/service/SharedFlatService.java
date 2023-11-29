package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;

public interface SharedFlatService {
    WgDetailDto create(WgDetailDto sharedFlat);

    WgDetailDto login(WgDetailDto wgDetailDto);
}
