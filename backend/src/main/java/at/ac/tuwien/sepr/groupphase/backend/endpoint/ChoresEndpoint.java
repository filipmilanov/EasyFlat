package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/chores")
public class ChoresEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreService choreService;

    private final ChoreMapper choreMapper;

    public ChoresEndpoint(ChoreService choreService, ChoreMapper choreMapper) {
        this.choreService = choreService;
        this.choreMapper = choreMapper;
    }

    @PostMapping
    public ChoreDto createChore(@RequestBody ChoreDto chore) throws AuthenticationException {
        LOGGER.trace("createChore({})", chore);
        return choreService.createChore(chore);
    }

    @PermitAll
    @GetMapping()
    public List<ChoreDto> getChores(@RequestParam(name = "searchParams", required = false) String searchParams) throws AuthenticationException {
        LOGGER.trace("getChores({})", searchParams);
        List<Chore> lists = choreService.getChores(searchParams);
        return choreMapper.entityListToDtoList(lists);
    }

    @PutMapping
    public List<ChoreDto> assignChores() throws AuthenticationException {
        LOGGER.trace("assignChores()");
        return this.choreService.assignChores();
    }
}
