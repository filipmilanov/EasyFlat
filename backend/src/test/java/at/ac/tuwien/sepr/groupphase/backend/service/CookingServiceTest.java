package at.ac.tuwien.sepr.groupphase.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CookingServiceTest {

    @Autowired
    private  CookingService cookingService;



    //To test cooking we need real products in testDB.
    @Test
    void testGetRecipeSuggestions(){

    }
}
