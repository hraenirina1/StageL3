package mg.orange.automatisation.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Serveur {
	
	@Id
	@GeneratedValue
	@Column(length=6)
	private Long id_serveur;
	public Long getId_serveur() {
		return id_serveur;
	}
	public void setId_serveur(Long id_serveur) {
		this.id_serveur = id_serveur;
	}
	
	@Column(length=30)
	private String nom;
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@OneToOne(cascade=CascadeType.ALL)
	private IP ip;
	public IP getIp() {
		return ip;
	}
	public void setIp(IP ip) {
		this.ip = ip;
	}
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="serveur")
	private List<Reseau> reseau;
	public List<Reseau> getReseau() {
		return reseau;
	}
	public void setReseau(List<Reseau> reseau) {
		this.reseau = reseau;
	}
	
	private String token;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
	//constructeur
	public Serveur() {
		
	}
	public Serveur(String nom, IP ip) {
		super();
		this.nom = nom;
		this.ip = ip;
	}

}
