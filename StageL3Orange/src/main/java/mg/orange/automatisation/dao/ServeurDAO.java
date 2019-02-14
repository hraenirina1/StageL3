package mg.orange.automatisation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.orange.automatisation.entities.PServeur;


public interface ServeurDAO extends JpaRepository<PServeur, Long> {
}
