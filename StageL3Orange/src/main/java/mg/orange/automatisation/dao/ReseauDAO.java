package mg.orange.automatisation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.orange.automatisation.entities.Reseau;

public interface ReseauDAO extends JpaRepository<Reseau, Long>{
	
}
