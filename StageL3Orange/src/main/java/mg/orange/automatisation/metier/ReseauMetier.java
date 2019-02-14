package mg.orange.automatisation.metier;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.orange.automatisation.dao.ReseauDAO;
import mg.orange.automatisation.entities.Reseau;

@Service
public class ReseauMetier {
	@Autowired
	private ReseauDAO reseaudao;
	public ReseauMetier() {
	}
	
	public List<Reseau> listeReseau()
	{
		return reseaudao.findAll();
	}

}
