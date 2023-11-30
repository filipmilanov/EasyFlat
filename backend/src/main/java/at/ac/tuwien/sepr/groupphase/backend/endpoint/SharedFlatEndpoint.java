package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WgDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
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
@RequestMapping(value = "/api/v1/wgCreate")
public class SharedFlatEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SharedFlatService sharedFlatService;

    @Autowired
    public SharedFlatEndpoint(SharedFlatService sharedFlatService) {
        this.sharedFlatService = sharedFlatService;
    }

    @PermitAll
    @PostMapping
    public WgDetailDto create(@RequestBody SharedFlat wgDetailDto) throws Exception {
        return sharedFlatService.create(wgDetailDto);
    }
}
