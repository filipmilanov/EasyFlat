package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/wgCreate")
public class SharedFlatEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatService sharedFlatService;

    private final CustomUserDetailService customUserDetailService;

    @Autowired
    public SharedFlatEndpoint(SharedFlatService sharedFlatService, CustomUserDetailService customUserDetailService) {
        this.sharedFlatService = sharedFlatService;
        this.customUserDetailService = customUserDetailService;
    }

    @Secured("ROLE_USER")
    @PostMapping
    public WgDetailDto create(@RequestBody SharedFlat wgDetailDto) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ApplicationUser user  =  customUserDetailService.findApplicationUserByEmail((String) authentication.getPrincipal());
        return sharedFlatService.create(wgDetailDto, user);
    }
}
