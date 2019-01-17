package mg.orange.automatisation.entities;

public class Sauvegarde {
	private Serveur serv;
	private String[] sauvegarde;
		
	public Sauvegarde() {
	}

	public Serveur getServ() {
		return serv;
	}

	public void setServ(Serveur serv) {
		this.serv = serv;
	}

	public String[] getSauvegarde() {
		return sauvegarde;
	}

	public void setSauvegarde(String[] sauvegarde) {
		this.sauvegarde = sauvegarde;
	}
	
	
}
