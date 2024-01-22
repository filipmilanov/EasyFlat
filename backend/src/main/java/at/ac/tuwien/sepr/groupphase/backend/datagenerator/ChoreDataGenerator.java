package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.ChoreRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

@Profile({"generateData", "test", "unitTest"})
@Component("ChoreDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator"})
public class ChoreDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final ChoreRepository choreRepository;

    public ChoreDataGenerator(ChoreRepository choreRepository) {
        this.choreRepository = choreRepository;
    }

    @PostConstruct
    public void generateChores() {
        for (long flatId = 1; flatId <= 5; flatId++) {
            SharedFlat sharedFlat = new SharedFlat();
            sharedFlat.setId(flatId);

            for (int i = 1; i <= NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
                Chore chore = new Chore();
                chore.setName("Chore" + ((flatId - 1) * NUMBER_OF_ENTITIES_TO_GENERATE + i));
                chore.setDescription("This is description for " + chore.getName());
                chore.setPoints(i * 3);
                chore.setEndDate(LocalDate.now().plusDays(i));
                chore.setSharedFlat(sharedFlat);
                choreRepository.save(chore);
            }
        }
    }






}
