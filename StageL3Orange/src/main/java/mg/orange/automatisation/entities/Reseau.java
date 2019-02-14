package mg.orange.automatisation.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@SuppressWarnings("serial")
@Entity
@PrimaryKeyJoinColumn(name = "id_appareil")
public class Reseau extends Appareil {
		
	@Column(length=3,nullable=false) 
	private int masque_reseau;	
	public int getMasque_reseau() {
		return masque_reseau;
	}
	public void setMasque_reseau(int masque_reseau) {
		this.masque_reseau = masque_reseau;
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
	public Reseau(String nom,IP ip,int masque_reseau,String Type) {
		super(nom,ip);
		this.masque_reseau = masque_reseau;
		this.Type = Type;
	}
	
}
