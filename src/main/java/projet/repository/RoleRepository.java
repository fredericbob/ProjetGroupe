package projet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import projet.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
