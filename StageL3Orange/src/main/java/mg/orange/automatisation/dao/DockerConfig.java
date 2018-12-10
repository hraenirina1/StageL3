package mg.orange.automatisation.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.orange.automatisation.entities.dockerConfig;

public interface DockerConfig extends JpaRepository<dockerConfig, Long> {

}
