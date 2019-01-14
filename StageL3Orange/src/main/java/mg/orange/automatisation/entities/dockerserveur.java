package mg.orange.automatisation.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class dockerserveur {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id_docker_serveur;
	@Column(unique=true)
	private String nom_docker_serveur;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_ip_docker")
	private IP ip_docker;
	
	@OneToOne(cascade = CascadeType.ALL)
	private IP ip_interne;	
	public IP getIp_interne() {
		return ip_interne;
	}
	public void setIp_interne(IP ip_interne) {
		this.ip_interne = ip_interne;
	}

	@OneToMany(cascade=CascadeType.ALL) 
	@JoinColumn
	private List<dockerConfig> dockerConfig;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_bd_serveur",nullable=false)
	private BdServeur bdserveur;
	
	//stat
	@Transient
	private Stat stat;	
	public Stat getStat() {
		return stat;
	}
	public void setStat(Stat stat) {
		this.stat = stat;
	}
		
		
	public dockerserveur() {
		// TODO Auto-generated constructor stub
	}

	public dockerserveur(String nom_docker_serveur, IP ip_docker) {
		super();
		this.nom_docker_serveur = nom_docker_serveur;
		this.ip_docker = ip_docker;
	}
	
	public dockerserveur(String nom_docker_serveur, IP ip_docker, BdServeur bdserveur) {
		super();
		this.nom_docker_serveur = nom_docker_serveur;
		this.ip_docker = ip_docker;
		this.bdserveur = bdserveur;
	}	
	
	public List<dockerConfig> getDockerConfig() {
		return dockerConfig;
	}

	public void setDockerConfig(List<dockerConfig> dockerConfig) {
		this.dockerConfig = dockerConfig;
	}

	public BdServeur getBdserveur() {
		return bdserveur;
	}
	
	public Long getId_docker_serveur() {
		return id_docker_serveur;
	}

	public void setId_docker_serveur(Long id_docker_serveur) {
		this.id_docker_serveur = id_docker_serveur;
	}

	public void setBdserveur(BdServeur bdserveur) {
		this.bdserveur = bdserveur;
	}

	public String getNom_docker_serveur() {
		return nom_docker_serveur;
	}

	public void setNom_docker_serveur(String nom_docker_serveur) {
		this.nom_docker_serveur = nom_docker_serveur;
	}

	public IP getIp_docker() {
		return ip_docker;
	}

	public void setIp_docker(IP ip_docker) {
		this.ip_docker = ip_docker;
	}
	
	
}
