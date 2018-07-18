package es.uca.allergio.backend.repositories;

import es.uca.allergio.backend.entities.IngredientRowData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRowDataRepository extends CrudRepository<IngredientRowData, Integer> {
}
