package mg.orange.automatisation.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class BdServeur {
	
	
	// identifiant
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id_bdServeur;
	public Long getId_bdServeur() {
		return id_bdServeur;
	}
	public void setId_bdServeur(Long id_bdServeur) {
		this.id_bdServeur = id_bdServeur;
	}
	
	
	//nom bdserveur	
	@Column(unique=true)
	private String nomBdServeur;
	public String getNomBdServeur() {
		return nomBdServeur;
	}
	public void setNomBdServeur(String nomBdServeur) {
		this.nomBdServeur = nomBdServeur;
	}
	
	
	//masque
	private int masque;
	public int getMasque() {
		return masque;
	}
	public void setMasque(int masque) {
		this.masque = masque;
	}
	
	
	//IpExterne
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="id_ip_externe")
	private IP ip_externe;
	public IP getIp_externe() {
		return ip_externe;
	}
	public void setIp_externe(IP ip_externe) {
		this.ip_externe = ip_externe;
	}

	
	//adresse reseau
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="id_ip_adresse_reseau")
	private IP adresseReseau;
	public IP getAdresseReseau() {
		return adresseReseau;
	}
	public void setAdresseReseau(IP adresseReseau) {
		this.adresseReseau = adresseReseau;
	}
	
	
	//status
	private String Status;//travail // deploy // up // down
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	
	
	//serveur
	@ManyToOne
	@JoinColumn(name="id_serveur")
	private Serveur serveur;
	public Serveur getServeur() {
		return serveur;
	}
	public void setServeur(Serveur serveur) {
		this.serveur = serveur;
	}
	
	
	//Docker serveur
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="id_bd_serveur",nullable=false,insertable=false,updatable=false)
	private List<dockerserveur> dserveur;
	public List<dockerserveur> getDserveur() {
		return dserveur;
	}
	public void setDserveur(List<dockerserveur> dserveur) {
		this.dserveur = dserveur;
	}
	
	
	// mysql password
	private String mysqlPasssword;
	public String getMysqlPasssword() {
		return mysqlPasssword;
	}
	public void setMysqlPasssword(String mysqlPasssword) {
		this.mysqlPasssword = mysqlPasssword;
	}
	
	
	//Reseau
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(nullable=false)
	private Reseau reseau;
	public Reseau getReseau() {
		return reseau;
	}
	public void setReseau(Reseau reseau) {
		this.reseau = reseau;
	}
	
	
	//contructeur
	public BdServeur() {
		
	}
	public BdServeur(String nomBdServeur, int masque, IP ip_externe, IP adresseReseau,
			Serveur serveur, String mysqlPasssword) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.masque = masque;
		this.ip_externe = ip_externe;
		this.adresseReseau = adresseReseau;
		this.serveur = serveur;
		this.mysqlPasssword = mysqlPasssword;
	}
	public BdServeur(String nomBdServeur, int masque, IP ip_externe, IP adresseReseau, String status,
			Serveur serveur, String mysqlPasssword, Reseau reseau) {
		super();
		this.nomBdServeur = nomBdServeur;
		this.masque = masque;
		this.ip_externe = ip_externe;
		this.adresseReseau = adresseReseau;
		Status = status;
		this.serveur = serveur;
		this.mysqlPasssword = mysqlPasssword;
		this.reseau = reseau;
	}
	
}
