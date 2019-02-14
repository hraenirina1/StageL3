package mg.orange.automatisation.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalIdCache;

@SuppressWarnings("serial")
@Entity
@NaturalIdCache
@Cache(
    usage = CacheConcurrencyStrategy.READ_WRITE
)
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

	@OneToMany(mappedBy = "docker",cascade = CascadeType.ALL)
	private List<Configuration> config = new ArrayList<Configuration>();	
	
	public List<Configuration> getConfig() {
		return config;
	}
	public void addConfig(Config config,String Valeur) {
		Configuration configuration = new Configuration(config,this,Valeur);
		this.config.add(configuration);
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="bdserveur",nullable=false)
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
