package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final SharedFlatRepository sharedFlatRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, SharedFlatRepository sharedFlatRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer,
                                   UserMapper userMapper, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findUserByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) throws ValidationException {
        userValidator.validateForLogIn(userLoginDto);
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    @Override
    public String register(UserDetailDto userDetailDto) throws ValidationException {
        userValidator.validateForRegister(userDetailDto);
        LOGGER.debug("Registering a new user");

        if (userRepository.findUserByEmail(userDetailDto.getEmail()) != null) {
            throw new BadCredentialsException("User with this email already exists");
        }

        ApplicationUser newUser = new ApplicationUser();
        newUser.setFirstName(userDetailDto.getFirstName());
        newUser.setLastName(userDetailDto.getLastName());
        newUser.setEmail(userDetailDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
        newUser.setAdmin(false);
        userRepository.save(newUser);

        UserDetails userDetails = loadUserByUsername(userDetailDto.getEmail());
        if (userDetails != null) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }

        throw new BadCredentialsException("Failed to register the user");
    }

    @Override
    public ApplicationUser getUser(String authToken) {
        String email = jwtTokenizer.getEmailFromToken(authToken);
        return userRepository.findUserByEmail(email);
    }

    @Override
    public UserDetailDto update(UserDetailDto userDetailDto) throws ValidationException {
        userValidator.validateForUpdate(userDetailDto);
        if (userRepository.findUserByEmail(userDetailDto.getEmail()) != null) {
            ApplicationUser user = userRepository.findUserByEmail(userDetailDto.getEmail());
            user.setFirstName(userDetailDto.getFirstName());
            user.setLastName(userDetailDto.getLastName());
            user.setEmail(userDetailDto.getEmail());
            user.setSharedFlat(user.getSharedFlat());
            if (userDetailDto.getPassword().length() >= 8) {
                user.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
            }
            ApplicationUser returnUser = userRepository.save(user);
            return userMapper.entityToUserDetailDto(returnUser);
        }
        throw new BadCredentialsException("User with this email doesn't exists");
    }

    @Override
    public UserDetailDto delete(Long id) {
        if (userRepository.findApplicationUserById(id) != null) {
            ApplicationUser deletedUser = userRepository.findApplicationUserById(id);
            userRepository.delete(deletedUser);
            return userMapper.entityToUserDetailDto(deletedUser);
        }
        throw new BadCredentialsException("User with this email doesn't exists");
    }

    @Override
    public UserDetailDto signOut(String flatName, String authToken) {
        LOGGER.trace("signOut({}, {})", flatName, authToken);
        String userEmail = jwtTokenizer.getEmailFromToken(authToken);
        ApplicationUser user = userRepository.findUserByEmail(userEmail);
        SharedFlat userFlat = user.getSharedFlat();
        if (userFlat == null) {
            throw new BadCredentialsException("");
        }

        if (userFlat.getName().equals(flatName)) {
            user.setSharedFlat(null);
            user.setAdmin(false);
            ApplicationUser updatedUser = userRepository.save(user);
            boolean exist = userRepository.existsBySharedFlat(userFlat);
            if (!exist) {
                sharedFlatRepository.deleteById(userFlat.getId());
            }
            return userMapper.entityToUserDetailDto(updatedUser);
        }
        throw new BadCredentialsException("");

    }

}
