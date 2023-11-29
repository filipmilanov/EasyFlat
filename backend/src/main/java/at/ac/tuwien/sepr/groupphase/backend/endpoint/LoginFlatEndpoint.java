package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/wgLogin")
public class LoginFlatEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatService sharedFlatService;

    @Autowired
    public LoginFlatEndpoint(SharedFlatService sharedFlatService) {
        this.sharedFlatService = sharedFlatService;
    }

    @PermitAll
    @PostMapping
    public WgDetailDto loginWg(@RequestBody WgDetailDto wgDetailDto) {
        return sharedFlatService.login(wgDetailDto);
    }
}
