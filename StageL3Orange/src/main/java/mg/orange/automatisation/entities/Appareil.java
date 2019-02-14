package mg.orange.automatisation.entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;


@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Appareil implements Serializable{

	@Id
	@GeneratedValue
	@SequenceGenerator(name="appareil_sequence")
	private Long id_appareil = (long) 0 ;
	@Column(unique=true,length=30)
	private String nom;
	@OneToOne(cascade=CascadeType.ALL)
	private IP ip;
	
	public Appareil() {
	}	
	public Appareil(String nom) {
		super();
		this.nom = nom;
	}
	public Appareil(String nom, IP ip) {
		super();
		this.nom = nom;
		this.ip = ip;
	}
	
	public long getId_appareil() {
		return id_appareil;
	}
	public void setId_appareil(long id_appareil) {
		this.id_appareil = id_appareil;
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
}
