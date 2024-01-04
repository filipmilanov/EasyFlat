package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ChoreServiceImpl implements ChoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ChoreRepository choreRepository;

    private final ChoreMapper choreMapper;

    private final AuthService authService;

    private final UserRepository userRepository;

    private final PreferenceRepository preferenceRepository;

    public ChoreServiceImpl(ChoreRepository choreRepository, ChoreMapper choreMapper, AuthService authService, UserRepository userRepository, PreferenceRepository preferenceRepository) {
        this.choreRepository = choreRepository;
        this.choreMapper = choreMapper;
        this.authService = authService;
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Secured("ROLE_USER")
    public ChoreDto createChore(ChoreDto choreDto) throws AuthenticationException {
        LOGGER.trace("createChore({})", choreDto);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        Chore chore = choreMapper.choreDtoToEntity(choreDto);
        chore.setSharedFlat(applicationUser.getSharedFlat());
        Chore savedChore = choreRepository.save(chore);
        return choreMapper.entityToChoreDto(savedChore);
    }

    @Override
    @Secured("ROLE_USER")
    public List<Chore> getChores(String searchParams) throws AuthenticationException {
        LOGGER.trace("createChore({})", searchParams);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        return choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
    }

    @Override
    @Secured("ROLE_USER")
    public List<ChoreDto> assignChores() throws AuthenticationException {
        LOGGER.trace("assignChores()");
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        List<ApplicationUser> users = userRepository.findAllBySharedFlat(applicationUser.getSharedFlat());
        List<Chore> chores = choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
        this.sortUsersByPoints(users);

        //implement please an algorithm, for every user form users you take the first preference.first(), which has as preference.user = null.
        //if you assign a chore to a user remove it form the list of chores
        //if all the preferences for the given users are with user (preference.user != null), than add this user to new List named random.
        //after you go to every user assign randomly the rest of the chores from the list chores


        return choreMapper.entityListToDtoList(choreRepository.findAllBySharedFlatId(applicationUser.getId()));

    }

    private Chore getRandomChore(List<Chore> chores) {
        if (chores == null || chores.isEmpty()) {
            throw new IllegalArgumentException("List is empty or null");
        }

        Random rand = new Random();
        int randomIndex = rand.nextInt(chores.size());
        return chores.get(randomIndex);
    }

    private List<Chore> getPreferences(ApplicationUser user) {
        Preference preference = preferenceRepository.findByUserId(user);
        List<Chore> toReturn = new ArrayList<>();
        Optional<Chore> firstChore = choreRepository.findById(preference.getFirstId());
        if (firstChore.isPresent()) {
            Chore choreToAdd = firstChore.get();
            toReturn.add(choreToAdd);
        }
        Optional<Chore> secondChore = choreRepository.findById(preference.getSecondId());
        if (secondChore.isPresent()) {
            Chore choreToAdd = secondChore.get();
            toReturn.add(choreToAdd);
        }
        Optional<Chore> thirdChore = choreRepository.findById(preference.getThirdId());
        if (thirdChore.isPresent()) {
            Chore choreToAdd = thirdChore.get();
            toReturn.add(choreToAdd);
        }
        Optional<Chore> fourthChore = choreRepository.findById(preference.getFourthId());
        if (fourthChore.isPresent()) {
            Chore choreToAdd = fourthChore.get();
            toReturn.add(choreToAdd);
        }
        return toReturn;
    }


    private void sortUsersByPoints(List<ApplicationUser> users) {
        int n = users.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                ApplicationUser user1 = users.get(j);
                ApplicationUser user2 = users.get(j + 1);
                if (user1.getPoints() < user2.getPoints()) {
                    ApplicationUser temp = users.get(j);
                    users.set(j, users.get(j + 1));
                    users.set(j + 1, temp);
                }
            }
        }
    }


}

