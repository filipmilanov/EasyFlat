package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ChoreMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.ChoreService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.interfaces.ChoreValidator;
import com.itextpdf.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    private final ChoreValidator choreValidator;

    public ChoreServiceImpl(ChoreRepository choreRepository, ChoreMapper choreMapper, AuthService authService, UserRepository userRepository, PreferenceRepository preferenceRepository, ChoreValidator choreValidator) {
        this.choreRepository = choreRepository;
        this.choreMapper = choreMapper;
        this.authService = authService;
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.choreValidator = choreValidator;
    }

    @Secured("ROLE_USER")
    public ChoreDto createChore(ChoreDto choreDto) throws AuthenticationException, ValidationException, ConflictException {
        LOGGER.trace("createChore({})", choreDto);
        this.choreValidator.validateForCreate(choreDto);
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
    public List<Chore> getChores(ChoreSearchDto searchParams) throws AuthenticationException {
        LOGGER.trace("createChore({})", searchParams);
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        return choreRepository.searchChores(
            (searchParams.userName() != null) ? searchParams.userName() : null,
            (searchParams.endDate() != null) ? searchParams.endDate() : null,
            applicationUser.getSharedFlat().getId());
    }

    @Override
    @Secured("ROLE_USER")
    public List<ChoreDto> assignChores() throws AuthenticationException {
        LOGGER.trace("assignChores()");
        //check the user
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        //all chores from this flat
        List<Chore> chores = choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
        List<Chore> choresAfterAssign = choreRepository.findAllBySharedFlatIdWhereUserIsNull(applicationUser.getSharedFlat().getId());
        if (choresAfterAssign.size() == 0) {
            throw new UnsupportedOperationException("All of the chores are assigned to users");
        } else if (choresAfterAssign.size() == chores.size()) {
            return assignChoresHelp(chores);
        } else {
            return secondAssign(choresAfterAssign);
        }
    }

    private List<ChoreDto> secondAssign(List<Chore> choresAfterAssign) throws AuthenticationException {
        LOGGER.trace("secondAssign({})", choresAfterAssign);
        //check the user
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        //all chores for this flat
        List<Chore> chores = choreRepository.findAllBySharedFlatId(applicationUser.getSharedFlat().getId());
        //all users from this flat
        List<ApplicationUser> users = userRepository.findAllBySharedFlat(applicationUser.getSharedFlat());
        //sort the users by points
        sortUsersByPoints(users);
        List<ApplicationUser> notAssignedUsers = new ArrayList<>();
        //5-5 or 5-6
        if (chores.size() >= users.size() && (chores.size() - choresAfterAssign.size()) % users.size() == 0) {
            for (int i = users.size() - choresAfterAssign.size(); i < users.size(); i++) {
                Chore toAssign = getRandomChore(choresAfterAssign);
                toAssign.setUser(users.get(i));
                choresAfterAssign.remove(toAssign);
                choreRepository.save(toAssign);
            }
            //3-5 then some users have one chore more than the others
            //5-3 then some users have no chores
        } else {
            while (!choresAfterAssign.isEmpty()) {
                int globalCount = (int) Math.floor((double) (chores.size() - choresAfterAssign.size()) / users.size());
                for (int j = users.size() - 1; j >= 0; j--) {
                    if (choreRepository.allChoresByUserId(users.get(j).getSharedFlat().getId(), users.get(j).getId()).size() <= globalCount) {
                        List<Chore> choresUser = getPreferences(users.get(j));
                        int count = 0;
                        for (Chore choreToAssign : choresUser) {
                            if (choreToAssign.getUser() == null) {
                                choreToAssign.setUser(users.get(j));
                                choresAfterAssign.remove(choreToAssign);
                                choreRepository.save(choreToAssign);
                                count++;
                                break;
                            }
                        }
                        if (count == 0) {
                            notAssignedUsers.add(users.get(j));
                        }
                    }
                }
                for (ApplicationUser user : notAssignedUsers) {
                    if (choresAfterAssign.size() == 0) {
                        break;
                    }
                    Chore toAssign = getRandomChore(choresAfterAssign);
                    toAssign.setUser(user);
                    choresAfterAssign.remove(toAssign);
                    choreRepository.save(toAssign);
                }
                if (choresAfterAssign.size() == 0) {
                    break;
                }
            }
        }

        return choreMapper.entityListToDtoList(choreRepository.findAllBySharedFlatId(applicationUser.getId()));
    }

    private List<ChoreDto> assignChoresHelp(List<Chore> chores) throws AuthenticationException {
        LOGGER.trace("assignChoresHelp({})", chores);
        //check the user
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        //all users from this flat
        List<ApplicationUser> users = userRepository.findAllBySharedFlat(applicationUser.getSharedFlat());
        //all chores without user from this flat are stored in chores list.

        //sort the users list by points
        this.sortUsersByPoints(users);
        //list for users, which preferences are already taken.
        List<ApplicationUser> notAssignedUsers = new ArrayList<>();

        //5users - 6 chores
        //5users - 5 chores
        if (users.size() <= chores.size()) {
            for (int i = 0; i < users.size(); i++) { // size 5
                //list of chores for one user
                List<Chore> choresUser = getPreferences(users.get(i)); //max size 4
                int count = 0;
                //loop for the chores of the user
                for (int j = 0; j < choresUser.size(); j++) {
                    if (choresUser.get(j) != null && choresUser.get(j).getUser() == null) {
                        Chore toUpdate = choresUser.get(j);
                        chores.remove(toUpdate);
                        toUpdate.setUser(users.get(i));
                        count++;
                        choreRepository.save(toUpdate);
                        break;
                    }
                }
                //if the user didn't take any chore he is added to the list of notAssignedUsers
                if (count == 0) {
                    notAssignedUsers.add(users.get(i));
                }
            }
            //assign randomly all not assigned Users
            for (ApplicationUser user : notAssignedUsers) {
                Chore toAssign = getRandomChore(chores);
                toAssign.setUser(user);
                chores.remove(toAssign);
                choreRepository.save(toAssign);
            }
            // rest of the chores that are not assigned
            //recursively add the rest of the chores
            if (!chores.isEmpty()) {
                assignChoresHelp(chores);
            }

        } else {
            //20users - 5chores
            for (int i = users.size() - chores.size(); i < users.size(); i++) {
                //list of chores for one user
                List<Chore> choresUser = getPreferences(users.get(i));
                int count = 0;
                //loop for the chores of the user
                for (int j = 0; j < choresUser.size(); j++) {
                    if (choresUser.get(j) != null && choresUser.get(j).getUser() == null) {
                        Chore toUpdate = choresUser.get(j);
                        chores.remove(toUpdate);
                        toUpdate.setUser(users.get(i));
                        count++;
                        choreRepository.save(toUpdate);
                        break;
                    }
                }
                //if the user didn't take any chore he is added to the list of notAssignedUsers
                if (count == 0) {
                    notAssignedUsers.add(users.get(i));
                }
            }
            //assign randomly all not assigned Users
            for (ApplicationUser user : notAssignedUsers) {
                Chore toAssign = getRandomChore(chores);
                toAssign.setUser(user);
                chores.remove(toAssign);
                choreRepository.save(toAssign);
            }
        }

        return choreMapper.entityListToDtoList(choreRepository.findAllBySharedFlatId(applicationUser.getId()));
    }

    @Override
    public List<Chore> getChoresByUser() throws AuthenticationException {
        LOGGER.trace("getChoresByUser()");
        ApplicationUser applicationUser = authService.getUserFromToken();
        if (applicationUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }
        return choreRepository.findAllByUser(applicationUser);
    }

    @Override
    public List<Chore> deleteChores(List<Long> choreIds) {
        LOGGER.trace("deleteChores({})", choreIds);
        List<Chore> toDelete = choreRepository.findAllById(choreIds);
        if (toDelete.size() != choreIds.size()) {
            throw new NotFoundException("The given chores do not exist in the persistent data");
        }
        choreRepository.deleteAllById(choreIds);
        return toDelete;
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
        List<Chore> toReturn = new ArrayList<>();
        if (!preferenceRepository.existsByUserId(user)) {
            return toReturn;
        }
        Preference preference = preferenceRepository.findByUserId(user);
        if (preference.getFirstId() != null) {
            Optional<Chore> firstChore = choreRepository.findById(preference.getFirstId());
            if (firstChore.isPresent()) {
                Chore choreToAdd = firstChore.get();
                toReturn.add(choreToAdd);
            }
        }
        if (preference.getSecondId() != null) {
            Optional<Chore> secondChore = choreRepository.findById(preference.getSecondId());
            if (secondChore.isPresent()) {
                Chore choreToAdd = secondChore.get();
                toReturn.add(choreToAdd);
            }
        }
        if (preference.getThirdId() != null) {
            Optional<Chore> thirdChore = choreRepository.findById(preference.getThirdId());
            if (thirdChore.isPresent()) {
                Chore choreToAdd = thirdChore.get();
                toReturn.add(choreToAdd);
            }
        }
        if (preference.getFourthId() != null) {
            Optional<Chore> fourthChore = choreRepository.findById(preference.getFourthId());
            if (fourthChore.isPresent()) {
                Chore choreToAdd = fourthChore.get();
                toReturn.add(choreToAdd);
            }
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


    @Override
    public List<ApplicationUser> getUsers() throws AuthenticationException {
        ApplicationUser existingUser = authService.getUserFromToken();
        if (existingUser == null) {
            throw new AuthenticationException("Authentication failed", List.of("User does not exist"));
        }

        return userRepository.findAllBySharedFlat(existingUser.getSharedFlat());
    }

    @Override
    public ApplicationUser updatePoints(Long userId, Integer points) {
        ApplicationUser existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        existingUser.setPoints(points);
        return userRepository.save(existingUser);
    }

    public byte[] generatePdf(String htmlContent) throws IOException {
        // Create a temporary file for storing the HTML content
        Path tempFilePath = Files.createTempFile("my-pdf", ".html");

        // Write the HTML content to the temporary file
        Files.write(tempFilePath, htmlContent.getBytes(StandardCharsets.UTF_8));

        // Parse the XHTML content and create a Document object
        Document document = Jsoup.parse(tempFilePath.toFile(), "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        // Convert the Document to a byte array representing the PDF content
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);

            // Render the XHTML content directly to the OutputStream
            renderer.setDocumentFromString(htmlContent, tempFilePath.toUri().toURL().toString());
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}

