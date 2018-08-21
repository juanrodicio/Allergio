package es.uca.allergio.backend.services;

import es.uca.allergio.backend.entities.Allergy;
import es.uca.allergio.backend.entities.Ingredient;
import es.uca.allergio.backend.entities.User;
import es.uca.allergio.backend.repositories.AllergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AllergyService {

    @Autowired
    private AllergyRepository allergyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private IngredientService ingredientService;

    public Boolean addAllergy(Allergy allergy) {

        if(!existsAllergy(allergy.getName())) {
            allergyRepository.save(allergy);
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteAllergy(String allergyName) {

        Optional<Allergy> allergy = allergyRepository.findByName(allergyName);

        if(allergy.isPresent()) {
            Allergy foundAllergy = allergy.get();

            for (User user:
                 foundAllergy.getUsers()) {
                userService.deleteAllergyFromUser(user.getUsername(), foundAllergy.getName());
            }

            for (Ingredient ingredient:
                 foundAllergy.getRelatedIngredients()) {
                ingredientService.deleteAllergyFromIngredient(ingredient.getName(), foundAllergy.getName());
            }

            allergyRepository.delete(foundAllergy);
            return true;
        }
        return false;
    }

    private Boolean existsAllergy(String allergyName) {
        return allergyRepository.findByName(allergyName).isPresent();
    }

    public List<Allergy> findAll() {
        List<Allergy> allergies = new ArrayList<>();
        allergyRepository.findAll().forEach(allergies::add);
        return allergies;
    }

    public Allergy findByName(String allergyName) {
        Optional<Allergy> allergy = allergyRepository.findByName(allergyName);
        return allergy.get();
    }

    public void saveAllergy (Allergy allergy) {
        allergyRepository.save(allergy);
    }
}
