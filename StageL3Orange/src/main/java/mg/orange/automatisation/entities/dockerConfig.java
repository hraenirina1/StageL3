package mg.orange.automatisation.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class dockerConfig {
	@Id
	@GeneratedValue
	private Long id_configdocker;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private Config configDocker;
	
	@Column(columnDefinition="TEXT")
	private String Valeur;
	
	public dockerConfig() {
		// TODO Auto-generated constructor stub
	}

	public Long getId_configdocker() {
		return id_configdocker;
	}

	public void setId_configdocker(Long id_configdocker) {
		this.id_configdocker = id_configdocker;
	}

	public Config getConfigDocker() {
		return configDocker;
	}

	public void setConfigDocker(Config configDocker) {
		this.configDocker = configDocker;
	}

	public String getValeur() {
		return Valeur;
	}

	public void setValeur(String valeur) {
		Valeur = valeur;
	}

	public dockerConfig(Config configDocker, String valeur) {
		super();
		this.configDocker = configDocker;
		Valeur = valeur;
	}

}
