package mg.orange.automatisation.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Serveur {
	@Id
	@GeneratedValue
	private Long id_serveur;
	private String nom;
	
	@OneToOne
	@JoinColumn(name="id_ip")
	private IP ip;
	
	public Serveur() {
		
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public IP getIp() {
		return ip;
	}

	public void setIp(IP ip) {
		this.ip = ip;
	}
	

	public Long getId_serveur() {
		return id_serveur;
	}

	public void setId_serveur(Long id_serveur) {
		this.id_serveur = id_serveur;
	}

	public Serveur(String nom, IP ip) {
		super();
		this.nom = nom;
		this.ip = ip;
	}
}
