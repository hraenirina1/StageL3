package mg.orange.automatisation.entities;

//import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@SuppressWarnings("serial")
@Entity
@PrimaryKeyJoinColumn(name = "id_appareil")
public class PServeur extends Serveur{
	
//	//reseau
//	@OneToMany(cascade=CascadeType.ALL,mappedBy="serveur")
//	private List<Reseau> reseau;
//	public List<Reseau> getReseau() {
//		return reseau;
//	}
//	public void setReseau(List<Reseau> reseau) {
//		this.reseau = reseau;
//	}
	
	//token
	private String token;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	//constructeur
	public PServeur() {
		super();
	}

}
