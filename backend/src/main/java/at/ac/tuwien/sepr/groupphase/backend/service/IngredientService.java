package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import java.util.List;


public interface IngredientService {

    /**
     * Search for all Ingredients which has a given id.
     *
     * @param ids a list of Ids
     * @return a list of all ingredients with a given id
     */
    List<Ingredient> findAllByIds(List<Long> ids);

    /**
     * Creates new Ingredient in the database.
     *
     * @param ingredients a list of IngredientDto which will be created
     * @return a list of persisted ingredients
     * @throws ConflictException if a given ingredient has a id
     */
    List<Ingredient> createAll(List<IngredientDto> ingredients) throws ConflictException;

    /**
     * Search for all Ingredients which has a given name.
     *
     * @param names a list of names
     * @return a list of all ingredients with a given name
     */
    List<Ingredient> findByNames(List<String> names);
}
