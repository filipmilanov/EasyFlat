package at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class Authorization {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AuthService authService;

    public Authorization(AuthService authService) {
        this.authService = authService;
    }

    public void authenticateUser(List<Long> allowedUser, String errorMessage) throws AuthenticationException {
        LOGGER.trace("authenticateUser({}, {})", id, errorMessage);

        ApplicationUser user = authService.getUserFromToken();
        if (user == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exists"));
        }

        if (!allowedUser.contains(user.getId())) {
            throw new AuthenticationException("Authentication failed", List.of(errorMessage));
        }
    }

    public void authenticateUser(List<Long> id) throws AuthenticationException {
        LOGGER.trace("authenticateUser({})", id);

        authenticateUser(id, "User does not have access to this resource");
    }
}
