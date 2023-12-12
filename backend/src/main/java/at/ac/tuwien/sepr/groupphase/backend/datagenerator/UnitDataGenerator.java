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

@Profile({"default", "generateData", "test"})
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

        UnitDto dl = new UnitDto("dl", null, Set.of());
        UnitDto cl = new UnitDto("cl", 10L, Set.of(dl));
        UnitDto ml = new UnitDto("ml", 10L, Set.of(cl));
        UnitDto l = new UnitDto("l", 1000L, Set.of(ml));

        UnitDto cup = new UnitDto("cup", 125L, Set.of(g));
        UnitDto tbsp = new UnitDto("tbsp", 15L, Set.of(ml));


        UnitDto pcs = new UnitDto("pcs", null, Set.of());
        UnitDto tablespoons = new UnitDto("tablespoons", 15L, Set.of(g));

        UnitDto pound = new UnitDto("pound", 15L, Set.of(g));
        UnitDto gallon = new UnitDto("gallon", 15L, Set.of(g));
        UnitDto sheets = new UnitDto("sheets", 15L, Set.of(g));


        unitService.create(g);
        unitService.create(kg);

        unitService.create(dl);
        unitService.create(cl);
        unitService.create(ml);
        unitService.create(l);

        unitService.create(cup);
        unitService.create(tbsp);
        unitService.create(pcs);
        unitService.create(tablespoons);
        unitService.create(pound);
        unitService.create(gallon);
        unitService.create(sheets);

    }


}
