package es.uca.allergio.backend.repositories;

import es.uca.allergio.backend.entities.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {
    List<Ingredient> findAllByOrderByIdAsc();
}
