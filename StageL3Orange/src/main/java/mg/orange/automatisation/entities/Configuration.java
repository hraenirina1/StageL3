//package mg.orange.automatisation.entities;
//
//import java.io.Serializable;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.MapsId;
//
//
//@SuppressWarnings("serial")
//@Entity
//public class Configuration implements Serializable {
//	@Id
//	@GeneratedValue(strategy=GenerationType.AUTO)
//	private DockerConfigurationPK id_configdocker;
//	
//	@ManyToOne(cascade = CascadeType.PERSIST)
//	@MapsId("id_docker_serveur")
//	@JoinColumn
//	private Config config;
//	
//	@ManyToOne
//    @MapsId("id_config")
//	@JoinColumn
//	private DockerServeur docker;
//	
//	@Column(columnDefinition="TEXT")
//	private String Valeur;
//
//	public Configuration(Config config, DockerServeur docker, String valeur) {
//		super();
//		this.config = config;
//		this.docker = docker;
//		Valeur = valeur;
//	}
//
//	
//	public DockerConfigurationPK getId_configdocker() {
//		return id_configdocker;
//	}
//
//	public void setId_configdocker(DockerConfigurationPK id_configdocker) {
//		this.id_configdocker = id_configdocker;
//	}
//
//	public Config getConfig() {
//		return config;
//	}
//
//	public void setConfig(Config config) {
//		this.config = config;
//	}
//
//	public DockerServeur getDocker() {
//		return docker;
//	}
//
//	public void setDocker(DockerServeur docker) {
//		this.docker = docker;
//	}
//
//	public String getValeur() {
//		return Valeur;
//	}
//
//	public void setValeur(String valeur) {
//		Valeur = valeur;
//	}	
//	
//}
