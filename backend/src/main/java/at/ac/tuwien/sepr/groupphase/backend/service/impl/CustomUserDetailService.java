package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SharedFlatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.UserValidator;
import jakarta.transaction.Transactional;
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
import java.util.NoSuchElementException;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final SharedFlatRepository sharedFlatRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    private final ChoreRepository choreRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, SharedFlatRepository sharedFlatRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer,
                                   UserMapper userMapper, UserValidator userValidator, ChoreRepository choreRepository) {
        this.userRepository = userRepository;
        this.sharedFlatRepository = sharedFlatRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.choreRepository = choreRepository;
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
    public String login(UserLoginDto userLoginDto) throws ValidationException, ConflictException {
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
        throw new ConflictException("Username or password is incorrect or account is locked");
    }

    @Override
    public String register(UserDetailDto userDetailDto) throws ValidationException, ConflictException {
        userValidator.validateForRegister(userDetailDto);
        LOGGER.debug("Registering a new user");

        if (userRepository.findUserByEmail(userDetailDto.getEmail()) != null) {
            throw new ConflictException("User with this email already exists");
        }

        ApplicationUser newUser = new ApplicationUser();
        newUser.setFirstName(userDetailDto.getFirstName());
        newUser.setLastName(userDetailDto.getLastName());
        newUser.setEmail(userDetailDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
        newUser.setAdmin(false);
        newUser.setPoints(0);
        userRepository.save(newUser);

        UserDetails userDetails = loadUserByUsername(userDetailDto.getEmail());
        if (userDetails != null) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }

        throw new ConflictException("Failed to register the user");
    }

    @Override
    public ApplicationUser getUser(String authToken) {
        String email = jwtTokenizer.getEmailFromToken(authToken);
        return userRepository.findUserByEmail(email);
    }

    @Override
    @Transactional
    public UserDetailDto update(UserDetailDto userDetailDto) throws ValidationException, ConflictException {
        userValidator.validateForUpdate(userDetailDto);
        if (userRepository.findUserByEmail(userDetailDto.getEmail()) != null) {
            ApplicationUser user = userRepository.findUserByEmail(userDetailDto.getEmail());
            user.setFirstName(userDetailDto.getFirstName());
            user.setLastName(userDetailDto.getLastName());
            user.setEmail(userDetailDto.getEmail());
            user.setSharedFlat(user.getSharedFlat());
            user.setPoints(user.getPoints());
            if (userDetailDto.getPassword().length() >= 8) {
                user.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
            }
            ApplicationUser returnUser = userRepository.save(user);
            return userMapper.entityToUserDetailDto(returnUser);
        }
        throw new ConflictException("User with this email doesn't exists");
    }

    @Override
    @Transactional
    public UserDetailDto delete(Long id) {
        if (userRepository.findApplicationUserById(id) != null) {
            ApplicationUser deletedUser = userRepository.findApplicationUserById(id);
            List<Chore> chores = choreRepository.allChoresByUserId(deletedUser.getSharedFlat().getId(), deletedUser.getId());
            if (!chores.isEmpty()) {
                for (Chore chore : chores) {
                    chore.setUser(null);
                    choreRepository.save(chore);
                }
            }
            userRepository.delete(deletedUser);
            return userMapper.entityToUserDetailDto(deletedUser);
        }
        throw new BadCredentialsException("User with this email doesn't exists");
    }

    @Override
    @Transactional
    public UserDetailDto signOut(String flatName, String authToken) {
        LOGGER.trace("signOut({})", flatName);
        String email = jwtTokenizer.getEmailFromToken(authToken);
        ApplicationUser user = findApplicationUserByEmail(email);
        SharedFlat userFlat = user.getSharedFlat();
        if (userFlat == null) {
            throw new BadCredentialsException("");
        }
        if (userFlat.getName().equals(flatName)) {
            user.setSharedFlat(null);
            user.setAdmin(false);
            ApplicationUser updatedUser = userRepository.save(user);
            boolean exist = userRepository.existsBySharedFlat(userFlat);
            if (!exist) {  // are there users
                List<Chore> chores = choreRepository.findAllBySharedFlatId(userFlat.getId());
                if (!chores.isEmpty()) {    //are there chores
                    choreRepository.deleteAll();
                }
                sharedFlatRepository.deleteById(userFlat.getId());
            }
            return userMapper.entityToUserDetailDto(updatedUser);
        }
        throw new BadCredentialsException("");

    }


    @Override
    public List<UserDetailDto> getAllOtherUsers(String authToken) {
        LOGGER.trace("getAllOtherUsers()");
        String email = jwtTokenizer.getEmailFromToken(authToken);
        ApplicationUser user = userRepository.findUserByEmail(email);
        Long userFlatId = user.getSharedFlat().getId();
        List<ApplicationUser> users = userRepository.findAllByFlatId(userFlatId);
        users.remove(user);
        return userMapper.entityListToUserDetailDtoList(users);
    }

    @Override
    public UserDetailDto setAdminToTheFlat(Long selectedUserId) {
        LOGGER.trace("setAdminToTheFlat({})", selectedUserId);
        if (selectedUserId == null) {
            throw new BadCredentialsException("");
        }
        ApplicationUser user = userRepository.findApplicationUserById(selectedUserId);
        if (user != null) {
            user.setAdmin(true);
            userRepository.save(user);
        } else {
            throw new NoSuchElementException("User with this id does not exist");
        }
        return userMapper.entityToUserDetailDto(user);
    }

    @Override
    public List<ApplicationUser> findFlatmates(String jwt) {
        LOGGER.debug("findFlatmates()");

        ApplicationUser user = this.getUser(jwt);
        SharedFlat flat = user.getSharedFlat();
        return flat.getUsers().stream().toList();
    }
}
