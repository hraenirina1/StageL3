package mg.orange.automatisation.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.dockerserveur;

public interface DockerServeurDAO extends JpaRepository<dockerserveur, Long> {
	public List<dockerserveur> findBybdserveur(BdServeur dbserv);
}
