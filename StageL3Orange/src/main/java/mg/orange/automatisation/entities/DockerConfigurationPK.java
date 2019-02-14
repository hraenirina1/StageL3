package mg.orange.automatisation.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class DockerConfigurationPK implements Serializable {
	
	@Column(name = "docker_serveur_id")
    private Long id_docker_serveur;
	
	@Column(name = "config_id")
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

	public DockerConfigurationPK(Long id_docker_serveur, Long id_config) {
		super();
		this.id_docker_serveur = id_docker_serveur;
		this.id_config = id_config;
	}	
	
}