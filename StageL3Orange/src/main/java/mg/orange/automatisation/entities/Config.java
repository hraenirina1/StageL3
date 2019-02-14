package mg.orange.automatisation.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@SuppressWarnings("serial")
@Entity
public class Config implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id_config;
	private String type; //base / ajout
	@Column(unique=true)
	private String mot_cle;

	@OneToMany(mappedBy = "config")
	private Set<Configuration> docker = new HashSet<Configuration>();
	
	public Config() {
		// TODO Auto-generated constructor stub
	}

	public Long getId_config() {
		return id_config;
	}

	public void setId_config(Long id_config) {
		this.id_config = id_config;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMot_cle() {
		return mot_cle;
	}

	public void setMot_cle(String mot_cle) {
		this.mot_cle = mot_cle;
	}

	public Config(String type, String mot_cle) {
		super();
		this.type = type;
		this.mot_cle = mot_cle;
	}

	
}
