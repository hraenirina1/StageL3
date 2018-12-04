package mg.orange.automatisation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.orange.automatisation.entities.IP;

public interface IPDAO extends JpaRepository<IP, Long>{
	
}
