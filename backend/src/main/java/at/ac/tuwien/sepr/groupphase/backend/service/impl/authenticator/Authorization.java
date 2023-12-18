package at.ac.tuwien.sepr.groupphase.backend.service.impl.authenticator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class Authorization {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CustomUserDetailService customUserDetailService;

    public Authorization(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    public void authenticateUser(String jwt, List<Long> allowedUser, String errorMessage) throws AuthorizationException {
        LOGGER.trace("authenticateUser({}, {}, {})", jwt, id, errorMessage);

        ApplicationUser user = customUserDetailService.getUser(jwt);
        if (user == null) {
            throw new AuthorizationException("Authentication failed", List.of("User does not exists"));
        }

        if (!allowedUser.contains(user.getId())) {
            throw new AuthorizationException("Authentication failed", List.of(errorMessage));
        }
    }

    public void authenticateUser(String jwt, List<Long> id) throws AuthorizationException {
        LOGGER.trace("authenticateUser({}, {})", jwt, id);

        authenticateUser(jwt, id, "User does not have access to this resource");
    }
}
