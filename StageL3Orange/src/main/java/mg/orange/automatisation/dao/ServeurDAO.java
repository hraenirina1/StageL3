package mg.orange.automatisation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.orange.automatisation.entities.Serveur;


public interface ServeurDAO extends JpaRepository<Serveur, Long> {
}
