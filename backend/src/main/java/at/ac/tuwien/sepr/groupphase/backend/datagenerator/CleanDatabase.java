package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.IngredientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component("CleanDatabase")
public class CleanDatabase {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemRepository itemRepository;
    private final IngredientRepository ingredientRepository;
    private final DigitalStorageRepository digitalStorageRepository;

    public CleanDatabase(ItemRepository itemRepository, IngredientRepository ingredientRepository, DigitalStorageRepository digitalStorageRepository) {
        this.itemRepository = itemRepository;
        this.ingredientRepository = ingredientRepository;
        this.digitalStorageRepository = digitalStorageRepository;
    }

    @PostConstruct
    private void cleanDatabase() {
        LOGGER.debug("cleaning database");
        itemRepository.deleteAll();
        itemRepository.resetSequence();

        ingredientRepository.deleteAll();
        ingredientRepository.resetSequence();

        digitalStorageRepository.deleteAll();
        digitalStorageRepository.resetSequence();
    }
}
