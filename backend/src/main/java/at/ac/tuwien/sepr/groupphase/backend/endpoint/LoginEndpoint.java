package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/authentication")
public class LoginEndpoint {

    private final UserService userService;
    private final UserMapper userMapper;

    public LoginEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PermitAll
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) throws ValidationException, ConflictException {
        return userService.login(userLoginDto);
    }

    @PermitAll
    @GetMapping
    public UserDetailDto getUser(@RequestHeader("Authorization") String authToken) {
        return userMapper.entityToUserDetailDto(
            userService.getUser(authToken)
        );
    }

    @PermitAll
    @PutMapping("/{id}")
    public UserDetailDto update(@PathVariable long id, @RequestBody UserDetailDto userDetailDto) throws ValidationException, ConflictException {
        return userService.update(userDetailDto);
    }


    @PermitAll
    @DeleteMapping("/{id}")
    public UserDetailDto delete(@PathVariable long id) {
        return userService.delete(id);
    }


    @PermitAll
    @PutMapping("/signOut")
    public UserDetailDto signOut(@RequestBody String flatName, @RequestHeader("Authorization") String authToken) {
        return userService.signOut(flatName, authToken);
    }

    @PermitAll
    @GetMapping("/users")
    public List<UserDetailDto> getUsers(@RequestHeader("Authorization") String authToken) {
        return userService.getAllOtherUsers(authToken);
    }

    @PermitAll
    @PutMapping("/admin")
    public UserDetailDto setAdmin(@RequestBody Long selectedUserId) {
        return userService.setAdminToTheFlat(selectedUserId);
    }

}
