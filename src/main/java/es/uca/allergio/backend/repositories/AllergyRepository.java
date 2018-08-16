package es.uca.allergio.backend.repositories;

import es.uca.allergio.backend.entities.Allergy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllergyRepository extends CrudRepository<Allergy, Integer> {

    Optional<Allergy> findByName(String allergyName);
}
