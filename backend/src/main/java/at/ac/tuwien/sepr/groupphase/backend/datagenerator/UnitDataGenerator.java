package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@Profile({"default", "generateData", "test", "presentationData"})
@Component("UnitDataGenerator")
public class UnitDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UnitService unitService;

    public UnitDataGenerator(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostConstruct
    public void generate() throws ValidationException, ConflictException {
        LOGGER.info("generate()");

        UnitDto g = new UnitDto("g", null, Set.of());
        UnitDto kg = new UnitDto("kg", 1000L, Set.of(g));


        UnitDto ml = new UnitDto("ml", 10L, Set.of());
        UnitDto l = new UnitDto("l", 1000L, Set.of(ml));


        UnitDto cups = new UnitDto("cups", 125L, Set.of(g));
        UnitDto cup = new UnitDto("cup", 125L, Set.of(g));
        UnitDto pound = new UnitDto("pound", 455L, Set.of(g));


        UnitDto pcs = new UnitDto("pcs", null, Set.of());
        UnitDto servings = new UnitDto("servings", 1L, Set.of(pcs));
        UnitDto sheets = new UnitDto("sheets", 1L, Set.of(pcs));


        UnitDto tablespoons = new UnitDto("tablespoons", 15L, Set.of(ml));
        UnitDto teaspoon = new UnitDto("teaspoon", 15L, Set.of(ml));
        UnitDto tbsp = new UnitDto("tbsp", 15L, Set.of(ml));
        UnitDto gallon = new UnitDto("gallon", 3785L, Set.of(ml));



        unitService.create(g);
        unitService.create(kg);
        unitService.create(ml);
        unitService.create(l);
        unitService.create(cup);
        unitService.create(tbsp);
        unitService.create(pcs);
        unitService.create(tablespoons);
        unitService.create(pound);
        unitService.create(gallon);
        unitService.create(sheets);
        unitService.create(cups);
        unitService.create(teaspoon);
        unitService.create(servings);
    }


}
