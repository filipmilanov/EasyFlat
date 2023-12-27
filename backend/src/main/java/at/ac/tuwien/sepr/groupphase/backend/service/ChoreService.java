package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PreferenceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;

public interface ChoreService {

    public PreferenceDto updatePref(PreferenceDto preference);

    ChoreDto createChore(ChoreDto chore);
}
