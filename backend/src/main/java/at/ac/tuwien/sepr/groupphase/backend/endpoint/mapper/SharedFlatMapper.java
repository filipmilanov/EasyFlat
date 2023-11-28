package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import org.springframework.stereotype.Component;

@Component
public class SharedFlatMapper {
    public WGDetailDto entityToWGDetailDto(SharedFlat sharedFlat){
        WGDetailDto wgDetailDto = new WGDetailDto();
        wgDetailDto.setName(sharedFlat.getName());
        wgDetailDto.setPassword(sharedFlat.getPassword());
        return wgDetailDto;
    }
}
