package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class FlatService implements SharedFlatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    public FlatService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
    }

    @Override
    public String sharedFlatLogin(WGLoginDto wgLoginDto) {
        String flatName = wgLoginDto.getName();
        String password = wgLoginDto.getPassword();

        if ("exampleFlat".equals(flatName) && "examplePassword".equals(password)) {
            return "Login successful"; // Return a success message or token
        } else {
            return "Invalid credentials"; // Return an error message
        }
    }

    @Override
    public WGCreateDto createFlat(WGCreateDto wgCreateDto) {
        // Add your logic here to handle the creation of a flat
        // For example, you might save the flat details to a database

        // Simulating a successful creation by returning the same DTO
        // In practice, you'd save the flat to a database and return the saved DTO
        return wgCreateDto;
    }
}
