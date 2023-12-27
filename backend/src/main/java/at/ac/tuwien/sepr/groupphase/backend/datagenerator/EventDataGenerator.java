package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.SharedFlat;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

@Profile({"generateData", "test"})
@Component("EventDataGenerator")
@DependsOn({"CleanDatabase", "SharedFlatDataGenerator"})
public class EventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventsRepository eventsRepository;

    public EventDataGenerator(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    public void generateEvents() {

        SharedFlat sharedFlat = new SharedFlat();
        sharedFlat.setId(1L);

        Event test1 = new Event();
        test1.setTitle("House Meeting");
        test1.setDescription("Discussing important matters regarding the shared living space.");
        test1.setSharedFlat(sharedFlat);
        test1.setDate(LocalDate.now().plusDays(7)); // Set a date one week from now

        Event test2 = new Event();
        test2.setTitle("Cleaning Day");
        test2.setDescription("A day dedicated to cleaning and maintaining the shared areas.");
        test2.setSharedFlat(sharedFlat);
        test2.setDate(LocalDate.now().plusDays(14)); // Set a date two weeks from now

        Event test3 = new Event();
        test3.setTitle("Movie Night");
        test3.setDescription("Gathering for a cozy movie night in the common area.");
        test3.setSharedFlat(sharedFlat);
        test3.setDate(LocalDate.now().plusDays(21)); // Set a date three weeks from now

        eventsRepository.save(test1);
        eventsRepository.save(test2);
        eventsRepository.save(test3);
    }
}
