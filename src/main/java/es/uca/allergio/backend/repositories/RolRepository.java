package es.uca.allergio.backend.repositories;

import es.uca.allergio.backend.entities.Rol;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends CrudRepository<Rol,Integer> {

    Optional<Rol> findByName(String name);
}
