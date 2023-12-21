package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication")
public class LoginEndpoint {

    private final UserService userService;
    private final UserMapper userMapper;

    public LoginEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) {
        return userService.login(userLoginDto);
    }

    @Secured("ROLE_USER")
    @GetMapping
    public UserDetailDto getUser(@RequestHeader("Authorization") String authToken) {
        return userMapper.entityToUserDetailDto(
            userService.getUser(authToken)
        );
    }

    @PermitAll
    @PutMapping
    public UserDetailDto update(@RequestBody UserDetailDto userDetailDto) {
        return userService.update(userDetailDto);
    }

    @PermitAll
    @DeleteMapping("/{email}")
    public UserDetailDto delete(@PathVariable String email) {
        return userService.delete(email);
    }

    @PermitAll
    @PutMapping("/signOut")
    public UserDetailDto signOut(@RequestHeader("Authorization") String authToken, @RequestBody String flatName) {
        return userService.signOut(flatName, authToken);
    }

}
