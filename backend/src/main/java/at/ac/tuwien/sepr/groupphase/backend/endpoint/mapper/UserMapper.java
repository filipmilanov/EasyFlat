package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
public abstract class UserMapper {
    public abstract UserLoginDto entityToUserLoginDto(ApplicationUser applicationUser);

    public abstract UserDetailDto entityToUserDetailDto(ApplicationUser applicationUser);
}
