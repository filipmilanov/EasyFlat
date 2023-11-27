package at.ac.tuwien.sepr.groupphase.backend.endpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.WGLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.service.SharedFlatService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class SharedFlatEndpoint {
    private final SharedFlatService wgService;
    public SharedFlatEndpoint(SharedFlatService sharedFlatService) {
        this.wgService = sharedFlatService;
    }

    @PermitAll
    @PostMapping
    public String loginWG(@RequestBody WGLoginDto wgLoginDto) {
        return wgService.sharedFlatLogin(wgLoginDto);
    }

    public WGCreateDto create(@RequestBody WGCreateDto wgCreateDto){
        return wgService.createFlat(wgCreateDto);
    }
}
