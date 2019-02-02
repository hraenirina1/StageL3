package mg.orange.automatisation.entities;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;


@SuppressWarnings("serial")
@Entity
@PrimaryKeyJoinColumn(name = "id_appareil")
public class Serveur extends Appareil {	
	public Serveur() {
		super();		
	}

	public Serveur(String nom, IP ip) {
		super(nom, ip);
	}
}
