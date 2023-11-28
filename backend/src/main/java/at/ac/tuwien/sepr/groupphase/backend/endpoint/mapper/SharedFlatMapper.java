package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.stereotype.Component;

@Component
public class SharedFlatMapper {
    public WgDetailDto entityToWgDetailDto(SharedFlat sharedFlat) {
        WgDetailDto wgDetailDto = new WgDetailDto();
        wgDetailDto.setName(sharedFlat.getName());
        wgDetailDto.setPassword(sharedFlat.getPassword());
        return wgDetailDto;
    }
}
