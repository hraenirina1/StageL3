package mg.orange.automatisation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.orange.automatisation.entities.Log;

public interface LogDAO extends JpaRepository<Log, Long>{

}
