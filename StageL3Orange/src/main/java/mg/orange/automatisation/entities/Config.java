package mg.orange.automatisation.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Config {
	@Id
	@GeneratedValue
	private Long id_config;
	private String type; //base / ajout
	@Column(unique=true)
	private String mot_cle;

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
