package es.uca.allergio.backend.repositories;

import es.uca.allergio.backend.entities.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {
    List<Ingredient> findAllByOrderByIdAsc();

    Optional<Ingredient> findByName(String ingredientName);
}
