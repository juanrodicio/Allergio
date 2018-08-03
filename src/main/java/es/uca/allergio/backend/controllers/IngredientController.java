package es.uca.allergio.backend.controllers;

import es.uca.allergio.backend.entities.Ingredient;
import es.uca.allergio.backend.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @RequestMapping(value = "api/convertIngredients", method = RequestMethod.GET)
    public Set<String> convertIngredients(@RequestParam(value="ingredients") String ingredients) {
        return ingredientService.convertIngredients(ingredients);
    }

    @RequestMapping(value = "api/addIngredient", method = RequestMethod.POST)
    public Boolean addIngredient(@RequestParam(value="ingredientName") String ingredientName) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientName);
        return ingredientService.addIngredient(ingredient);
    }

    @RequestMapping(value = "api/deleteIngredient/{ingredientName}", method = RequestMethod.DELETE)
    public Boolean deleteIngredient(@PathVariable("ingredientName") String ingredientName) {
        return ingredientService.deleteIngredient(ingredientName);
    }

    @RequestMapping(value = "api/allIngredients", method = RequestMethod.GET)
    public List<String> allIngredients() {
        List<String> ingredientList = new ArrayList<>();
        ingredientService.findAll().forEach(ingredient -> ingredientList.add(ingredient.getName()));
        return ingredientList;
    }
}
