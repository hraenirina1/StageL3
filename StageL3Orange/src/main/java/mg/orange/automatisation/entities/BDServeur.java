package mg.orange.automatisation.entities;

import java.util.List;

import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
public class BDServeur extends Serveur {
	
	//status
	private String status;//travail // deploy // up // down
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	//serveur
	@ManyToOne
	@JoinColumn
	private PServeur pserveur;
	public PServeur getServeur() {
		return pserveur;
	}
	public void setPServeur(PServeur pserveur) {
		this.pserveur = pserveur;
	}
	
	
	//Docker serveur
	//@OneToMany(cascade = CascadeType.ALL)
	//@JoinColumn(name="id_bd_serveur",nullable=false,insertable=false,updatable=false)
	//private List<DockerServeur> dserveur;
	//public List<DockerServeur> getDserveur() {
	//	return dserveur;
	//}
	//public void setDserveur(List<DockerServeur> dserveur) {
	//	this.dserveur = dserveur;
	//}
	
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
	
	//Reseau Interne
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(nullable=false)
	private Reseau reseau_interne;
	public Reseau getReseau_interne() {
		return reseau;
	}
	public void setReseau_interne(Reseau reseau) {
		this.reseau_interne = reseau;
	}
	
	//stat
	@Transient
	private Stat stat;	
	public Stat getStat() {
		return stat;
	}
	public void setStat(Stat stat) {
		this.stat = stat;
	}
	
	//contructeur
	public BDServeur() {		
	}
	public BDServeur(String nom,IP ip, String status,String mysqlPasssword, Reseau reseau, Reseau Interne) {
		super(nom,ip);
		this.status = status;
		this.mysqlPasssword = mysqlPasssword;
		this.reseau = reseau;
		this.reseau_interne = Interne;
	}	
}
