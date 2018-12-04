package mg.orange.automatisation.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class BdServeur {
	@Id
	@GeneratedValue
	private Long id_bdServeur;
	private String nomBdServeur;
	
	@OneToOne
	@JoinColumn(name="id_ip")
	private IP ip_externe;
	
	@ManyToOne
	@JoinColumn(name="id_serveur")
	private Serveur serveur;
	
	
	public BdServeur() {
		
	}
	public BdServeur(String nomBdServeur, IP ip_externe) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.ip_externe = ip_externe;
	}
	
	public BdServeur(String nomBdServeur, IP ip_externe, Serveur serveur) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.ip_externe = ip_externe;
		this.serveur = serveur;
	}
	public Long getId_bdServeur() {
		return id_bdServeur;
	}

	public void setId_bdServeur(Long id_bdServeur) {
		this.id_bdServeur = id_bdServeur;
	}

	public String getNomBdServeur() {
		return nomBdServeur;
	}

	public void setNomBdServeur(String nomBdServeur) {
		this.nomBdServeur = nomBdServeur;
	}

	public IP getIp_externe() {
		return ip_externe;
	}
	
	


	public Serveur getServeur() {
		return serveur;
	}
	public void setServeur(Serveur serveur) {
		this.serveur = serveur;
	}
	public void setIp_externe(IP ip_externe) {
		this.ip_externe = ip_externe;
	}
}