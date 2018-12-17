package mg.orange.automatisation.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class BdServeur {
	@Id
	@GeneratedValue
	private Long id_bdServeur;
	@Column(unique=true)
	private String nomBdServeur;
	private int masque;
	
	@OneToOne
	@JoinColumn(name="id_ip_externe")
	private IP ip_externe;
	private String nomReseau;
	@OneToOne
	@JoinColumn(name="id_ip_adresse_reseau")
	private IP adresseReseau;
	private String Status;//travail // deploy // up // down
	
	@ManyToOne
	@JoinColumn(name="id_serveur")
	private Serveur serveur;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="id_bd_serveur",nullable=false,insertable=false,updatable=false)
	private List<dockerserveur> dserveur;
	private String mysqlPasssword;
	public IP getAdresseReseau() {
		return adresseReseau;
	}
	public void setAdresseReseau(IP adresseReseau) {
		this.adresseReseau = adresseReseau;
	}
	
	public String getMysqlPasssword() {
		return mysqlPasssword;
	}
	public void setMysqlPasssword(String mysqlPasssword) {
		this.mysqlPasssword = mysqlPasssword;
	}
	public int getMasque() {
		return masque;
	}
	public void setMasque(int masque) {
		this.masque = masque;
	}
	
	public String getStatus() {
		return Status;
	}
	
	public void setStatus(String status) {
		Status = status;
	}
	public BdServeur() {
		
	}
	public BdServeur(String nomBdServeur, IP ip_externe) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.ip_externe = ip_externe;
		this.Status = "Travail";
	}
	
	public BdServeur(String nomBdServeur, IP ip_externe, Serveur serveur) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.ip_externe = ip_externe;
		this.serveur = serveur;
	}
	
	public BdServeur(String nomBdServeur, int masque, IP ip_externe, IP adresseReseau, Serveur serveur) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.masque = masque;
		this.ip_externe = ip_externe;
		this.adresseReseau = adresseReseau;
		this.serveur = serveur;
	}
		
	public BdServeur(String nomBdServeur, int masque, IP ip_externe, String nomReseau, IP adresseReseau,
			Serveur serveur, String mysqlPasssword) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.masque = masque;
		this.ip_externe = ip_externe;
		this.nomReseau = nomReseau;
		this.adresseReseau = adresseReseau;
		this.serveur = serveur;
		this.mysqlPasssword = mysqlPasssword;
	}
	public List<dockerserveur> getDserveur() {
		return dserveur;
	}
	public void setDserveur(List<dockerserveur> dserveur) {
		this.dserveur = dserveur;
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
	
	public String getNomReseau() {
		return nomReseau;
	}
	public void setNomReseau(String nomReseau) {
		this.nomReseau = nomReseau;
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
