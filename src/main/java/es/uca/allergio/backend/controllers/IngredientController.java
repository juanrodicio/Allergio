package es.uca.allergio.backend.controllers;

import es.uca.allergio.backend.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class IngredientController {

    @Autowired
    IngredientService ingredientService;

    @RequestMapping(value = "api/convertIngredients", method = RequestMethod.GET)
    public Set<String> convertIngredients(@RequestParam(value="ingredients") String ingredients) {
        return ingredientService.convertIngredients(ingredients);
    }
}
