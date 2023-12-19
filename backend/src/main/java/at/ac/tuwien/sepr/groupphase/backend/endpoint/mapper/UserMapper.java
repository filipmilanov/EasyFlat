package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class UserMapper {
    public abstract UserLoginDto entityToUserLoginDto(ApplicationUser applicationUser);

    @Mapping(source = "applicationUser.sharedFlat.name", target = "flatName")
    @Mapping(source = "applicationUser.id", target = "id")
    public abstract UserDetailDto entityToUserDetailDto(ApplicationUser applicationUser);

}
