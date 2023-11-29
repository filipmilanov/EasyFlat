package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;

public interface SharedFlatService {
    WgDetailDto create(SharedFlat sharedFlat);

    WgDetailDto loginWg(WgDetailDto wgDetailDto);

}
