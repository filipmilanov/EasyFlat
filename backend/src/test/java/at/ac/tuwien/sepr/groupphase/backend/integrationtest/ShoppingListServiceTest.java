package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DigitalStorageSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthorizationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.AuthService;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import at.ac.tuwien.sepr.groupphase.backend.service.UnitService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ShoppingListServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.validator.ShoppingItemValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
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
    private AuthService authService;

    @Mock
    private Logger logger;

    @Test
    @Disabled
    public void testCreate() throws AuthenticationException, ValidationException, ConflictException, AuthorizationException {

        String jwt = "mockedJWT";

        // Mock the necessary method calls
        when(customUserDetailService.getUser(jwt)).thenReturn(testUser);
        when(shoppingListService.getShoppingLists("", jwt)).thenReturn(List.of(new ShoppingList().setId(1L).setName("Default")));
        when(digitalStorageService.findAll(new DigitalStorageSearchDto(null, null))).thenReturn(Collections.emptyList());
        when(unitService.findAll()).thenReturn(Collections.emptyList());

        // Use doCallRealMethod() to allow the real implementation to be called

        doNothing().when(shoppingItemValidator).validateForCreate(
            eq(validShoppingItemDto),
            any(),
            any(),
            any()
        );

        // Act
        shoppingListService.create(validShoppingItemDto, jwt);
        when(authService.getUserFromToken()).thenReturn(testUser);
        List<ShoppingItem> result = shoppingListService.getItemsById(validShoppingItemDto.shoppingList().id(), new ShoppingItemSearchDto(null, null, null), jwt);

        // Assert
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size())
        );
    }


}
