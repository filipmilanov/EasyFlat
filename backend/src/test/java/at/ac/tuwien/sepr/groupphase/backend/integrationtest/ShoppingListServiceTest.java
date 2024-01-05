package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ShoppingListServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ShoppingItemValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ShoppingListServiceTest implements TestData {

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService; // Replace with the actual service class name

    @Mock
    private ShoppingItemValidator shoppingItemValidator;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ShoppingItemRepository shoppingItemRepository;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private DigitalStorageService digitalStorageService;

    @Mock
    private LabelService labelService;

    @Mock
    private UnitService unitService;

    @Mock
    private ShoppingListMapper shoppingListMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private Logger logger;


    @Test
    public void testCreate() throws AuthenticationException, ValidationException, ConflictException {
        String jwt = "mockedJWT";

        // Mock the necessary method calls
        //when(shoppingListService.getShoppingLists("", jwt)).thenReturn(new ShoppingList());
        when(customUserDetailService.getUser(jwt)).thenReturn(testUser);
        when(digitalStorageService.findAll(null, jwt)).thenReturn(Collections.emptyList());
        when(unitService.findAll()).thenReturn(Collections.emptyList());

        doNothing().when(shoppingItemValidator).validateForCreate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        // Act
        ShoppingItem result = shoppingListService.create(validShoppingItemDto, jwt);

        verify(customUserDetailService).getUser(jwt);
        verify(digitalStorageService).findAll(null, jwt);
        verify(unitService).findAll();
        verify(shoppingItemValidator).validateForCreate(
            eq(validShoppingItemDto),
            anyList(),
            anyList(),
            anyList()
        );
        verify(shoppingItemRepository).save(any());

        // Assert
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals("pear", result.getGeneralName())
        );
    }


}
