package mg.orange.automatisation.metier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import mg.orange.automatisation.dao.IPDAO;

@ComponentScan
public class ServeurMetier {
	@Autowired
	private IPDAO ipdao;
	public ServeurMetier() {
		ipdao.findAll().size();
	}

}
