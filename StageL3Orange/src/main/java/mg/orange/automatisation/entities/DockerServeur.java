package mg.orange.automatisation.entities;

//import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@PrimaryKeyJoinColumn(name = "id_appareil")
public class DockerServeur extends Serveur {
	
	//ip_interne
	@OneToOne(cascade = CascadeType.ALL)
	private IP ip_interne;	
	public IP getIp_interne() {
		return ip_interne;
	}
	public void setIp_interne(IP ip_interne) {
		this.ip_interne = ip_interne;
	}

//	@OneToMany(cascade=CascadeType.ALL) 
//	@JoinColumn
//	private List<dockerConfig> dockerConfig;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_bd_serveur",nullable=false)
	private BDServeur bdserveur;
	
	private String ram;
	private String cpu;
	private String espace;
	
	//stat
	@Transient
	private Stat stat;	
	public Stat getStat() {
		return stat;
	}
	public void setStat(Stat stat) {
		this.stat = stat;
	}
	
	public String getRam() {
		return ram;
	}
	public void setRam(String ram) {
		this.ram = ram;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getEspace() {
		return espace;
	}
	public void setEspace(String espace) {
		this.espace = espace;
	}
	
	public DockerServeur() {
		// TODO Auto-generated constructor stub
	}
	public DockerServeur(String nom, IP ip, BDServeur bdserveur) {
		super(nom,ip);
		this.bdserveur = bdserveur;
	}	
	
	public BDServeur getBdserveur() {
		return bdserveur;
	}
	public void setBdserveur(BDServeur bdserveur) {
		this.bdserveur = bdserveur;
	}
}
