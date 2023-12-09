package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExpenseServiceTest {

    @Autowired
    private ExpenseService service;

    @Autowired
    private TestDataGenerator testDataGenerator;

    @BeforeEach
    public void cleanUp() {
        testDataGenerator.cleanUp();
    }

    @Test
    void findById() {
    }

    @Test
    void create() {
    }
}