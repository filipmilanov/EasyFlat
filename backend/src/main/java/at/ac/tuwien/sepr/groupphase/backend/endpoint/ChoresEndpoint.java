package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RepeatChoreRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/chores")
public class ChoresEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreService choreService;

    private final ChoreMapper choreMapper;

    private final UserMapper userMapper;

    public ChoresEndpoint(ChoreService choreService, ChoreMapper choreMapper, UserMapper userMapper) {
        this.choreService = choreService;
        this.choreMapper = choreMapper;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ChoreDto createChore(@RequestBody ChoreDto chore) throws AuthenticationException, ValidationException, ConflictException {
        LOGGER.trace("createChore({})", chore);
        return choreService.createChore(chore);
    }

    @PermitAll
    @GetMapping()
    public List<ChoreDto> getChores(ChoreSearchDto searchParams) throws AuthenticationException {
        LOGGER.trace("getChores({})", searchParams);
        List<Chore> lists = choreService.getChores(searchParams);
        return choreMapper.entityListToDtoList(lists);
    }

    @PutMapping
    public List<ChoreDto> assignChores() throws AuthenticationException {
        LOGGER.trace("assignChores()");
        return this.choreService.assignChores();
    }

    @GetMapping("/user")
    public List<ChoreDto> getChoresByUser() throws AuthenticationException {
        LOGGER.trace("getChoresByUser()");
        List<Chore> chores = choreService.getChoresByUser();
        return choreMapper.entityListToDtoList(chores);
    }

    @DeleteMapping("/delete")
    public List<ChoreDto> deleteChores(@RequestParam(name = "choreIds") String choreIdsString) {
        LOGGER.trace("deleteChores({})", choreIdsString);
        List<Long> choreIds = Arrays.stream(choreIdsString.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());

        List<Chore> deletedChores = choreService.deleteChores(choreIds);
        return choreMapper.entityListToDtoList(deletedChores);
    }

    @GetMapping("/users")
    public List<UserDetailDto> getUsers() throws AuthenticationException {
        LOGGER.trace("getUsers()");

        List<ApplicationUser> users = choreService.getUsers();

        return userMapper.entityListToDtoList(users);
    }

    @PatchMapping("/{userId}")
    public UserDetailDto updatePoints(@PathVariable Long userId, @RequestBody UserDetailDto searchParams) {
        LOGGER.trace("updatePoints({}, {})", userId, searchParams);

        Integer points = searchParams.getPoints();

        ApplicationUser updatedChore = choreService.updatePoints(userId, points);

        return userMapper.entityToUserDetailDto(updatedChore);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateChoreListPdf() throws AuthenticationException, IOException {
        byte[] pdfBytes = choreService.generatePdf();

        return new ResponseEntity<>(pdfBytes, HttpStatus.OK);
    }

    @PatchMapping("/repeat")
    public ChoreDto repeatChore(@RequestBody RepeatChoreRequest request) {
        LOGGER.trace("repeatChore({})", request);
        Long id = request.getId();
        Date date = request.getDate();
        return choreService.repeatChore(id, date);
    }


}
