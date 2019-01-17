package mg.orange.automatisation.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Reseau {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(length=6)
	private Long id_reseau;
	public Long getId_reseau() {
		return id_reseau;
	}
	public void setId_reseau(Long id_reseau) {
		this.id_reseau = id_reseau;
	}
	
	
	@Column(length=30,nullable=false)
	private String nom_reseau; 
	public String getNom_reseau() {
		return nom_reseau;
	}
	public void setNom_reseau(String nom_reseau) {
		this.nom_reseau = nom_reseau;
	}
		
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(nullable=false)
	private IP ip_reseau;
	public IP getIp_reseau() {
		return ip_reseau;
	}
	public void setIp_reseau(IP ip_reseau) {
		this.ip_reseau = ip_reseau;
	}
	
	
	@Column(length=3,nullable=false) 
	private int masque_reseau;	
	public int getMasque_reseau() {
		return masque_reseau;
	}
	public void setMasque_reseau(int masque_reseau) {
		this.masque_reseau = masque_reseau;
	}
	
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Serveur serveur;
	public Serveur getServeur() {
		return serveur;
	}
	public void setServeur(Serveur serveur) {
		this.serveur = serveur;
	}
	
	private String Type;	
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	
	
	//constructeur
	public Reseau() {
	}
	public Reseau(String nom_reseau, IP ip_reseau, int masque_reseau) {
		super();
		this.nom_reseau = nom_reseau;
		this.ip_reseau = ip_reseau;
		this.masque_reseau = masque_reseau;
	}
	public Reseau(String nom_reseau, IP ip_reseau, int masque_reseau, Serveur serveur, String type) {
		super();
		this.nom_reseau = nom_reseau;
		this.ip_reseau = ip_reseau;
		this.masque_reseau = masque_reseau;
		this.serveur = serveur;
		Type = type;
	}
	
	
}
