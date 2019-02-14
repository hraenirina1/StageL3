package mg.orange.automatisation.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.orange.automatisation.entities.BDServeur;
//import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.PServeur;

public interface BdServeurDAO extends JpaRepository<BDServeur, Long> {
	public List<BDServeur> findByPserveurAndStatus(PServeur serv,String Status);
	public List<BDServeur> findByPserveur(PServeur serv);
	public List<BDServeur> findByStatus(String Status);
	//public List<BDServeur> findByReseau(Reseau reseau);
}
