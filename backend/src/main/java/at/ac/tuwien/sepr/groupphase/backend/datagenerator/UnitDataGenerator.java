package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UnitDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

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

        UnitDto g = new UnitDto("g", null, null);
        UnitDto kg = new UnitDto("kg", 1000L, g);

        UnitDto dl = new UnitDto("dl", null, null);
        UnitDto cl = new UnitDto("cl", 10L, dl);
        UnitDto ml = new UnitDto("ml", 10L, cl);
        UnitDto l = new UnitDto("l", 1000L, ml);

        unitService.create(g);
        unitService.create(kg);

        unitService.create(dl);
        unitService.create(cl);
        unitService.create(ml);
        unitService.create(l);
    }


}
