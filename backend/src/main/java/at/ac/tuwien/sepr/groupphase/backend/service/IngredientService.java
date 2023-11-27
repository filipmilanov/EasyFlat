package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;

import java.util.List;


public interface IngredientService {

    /**
     * Search for all Ingredients which has a given id.
     *
     * @param ids a list of Ids
     * @return a list of all ingredients with a given id
     */
    List<Ingredient> findAllByIds(List<Long> ids);
}
