package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserLoginDto mapToUserLoginDto(ApplicationUser applicationUser) {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail(applicationUser.getEmail());
        userLoginDto.setPassword(applicationUser.getPassword());
        return userLoginDto;
    }
}
