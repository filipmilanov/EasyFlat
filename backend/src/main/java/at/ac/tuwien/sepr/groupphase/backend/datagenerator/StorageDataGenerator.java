package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.repository.DigitalStorageRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component("StorageDataGenerator")
@DependsOn("CleanDatabase")
public class StorageDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ENTITIES_TO_GENERATE = 5;
    private final DigitalStorageRepository digitalStorageRepository;

    public StorageDataGenerator(DigitalStorageRepository digitalStorageRepository) {
        this.digitalStorageRepository = digitalStorageRepository;
    }

    @PostConstruct
    private void generateDigitalStorages() {
        LOGGER.debug("generating {} Digital Storages", NUMBER_OF_ENTITIES_TO_GENERATE);
        for (int i = 0; i < NUMBER_OF_ENTITIES_TO_GENERATE; i++) {
            DigitalStorage message = new DigitalStorage();
            message.setTitle("Storage " + (i + 1));
            LOGGER.debug("saving message {}", message);
            digitalStorageRepository.save(message);
        }
    }
}
