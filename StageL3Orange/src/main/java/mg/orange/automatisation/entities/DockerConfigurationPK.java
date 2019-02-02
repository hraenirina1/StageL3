package mg.orange.automatisation.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class DockerConfigurationPK implements Serializable {

    private Long id_docker_serveur;

    private Long id_config;

	public Long getId_docker_serveur() {
		return id_docker_serveur;
	}

	public void setId_docker_serveur(Long id_docker_serveur) {
		this.id_docker_serveur = id_docker_serveur;
	}

	public Long getId_config() {
		return id_config;
	}

	public void setId_config(Long id_config) {
		this.id_config = id_config;
	}

	public DockerConfigurationPK() {
	}	
}