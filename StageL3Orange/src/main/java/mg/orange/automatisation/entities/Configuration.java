package mg.orange.automatisation.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;


@SuppressWarnings("serial")
@Entity
public class Configuration implements Serializable {
	
	@EmbeddedId
	private DockerConfigurationPK id_configdocker;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@MapsId("docker_serveur_id")
	@JoinColumn(name = "docker_serveur_id")
	private DockerServeur docker;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @MapsId("config_id")
	@JoinColumn(name = "config_id")
	private Config config;	
	
	@Column(columnDefinition="TEXT")
	private String Valeur;
			
	public Configuration() {
	}

	public Configuration(Config config, DockerServeur docker, String valeur) {
		super();
		this.config = config;
		this.docker = docker;
		this.id_configdocker = new DockerConfigurationPK(docker.getId_appareil(), config.getId_config());
		Valeur = valeur;
	}
	
	public DockerConfigurationPK getId_configdocker() {
		return id_configdocker;
	}

	public void setId_configdocker(DockerConfigurationPK id_configdocker) {
		this.id_configdocker = id_configdocker;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public DockerServeur getDocker() {
		return docker;
	}

	public void setDocker(DockerServeur docker) {
		this.docker = docker;
	}

	public String getValeur() {
		return Valeur;
	}

	public void setValeur(String valeur) {
		Valeur = valeur;
	}	
	
}
