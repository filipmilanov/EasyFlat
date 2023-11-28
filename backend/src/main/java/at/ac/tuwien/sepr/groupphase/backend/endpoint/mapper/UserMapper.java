package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserLoginDto entityToUserLoginDto(ApplicationUser applicationUser) {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail(applicationUser.getEmail());
        userLoginDto.setPassword(applicationUser.getPassword());
        return userLoginDto;
    }

    public UserDetailDto entityToUserDetailDto(ApplicationUser applicationUser) {
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setFirstName(applicationUser.getFirstName());
        userDetailDto.setLastName(applicationUser.getLastName());
        userDetailDto.setEmail(applicationUser.getEmail());
        userDetailDto.setPassword(applicationUser.getPassword());
        return userDetailDto;
    }
}
