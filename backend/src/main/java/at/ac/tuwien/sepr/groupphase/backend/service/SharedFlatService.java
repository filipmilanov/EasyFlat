package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;

public interface SharedFlatService {
    WGDetailDto create(WGDetailDto sharedFlat);
}
