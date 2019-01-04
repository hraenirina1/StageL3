package mg.orange.automatisation.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.Serveur;

public interface BdServeurDAO extends JpaRepository<BdServeur, Long> {
	public List<BdServeur> findByServeurAndStatus(Serveur serv,String Status);
	public List<BdServeur> findByServeur(Serveur serv);
	public List<BdServeur> findByStatus(String Status);
	public List<BdServeur> findByReseau(Reseau reseau);
}
