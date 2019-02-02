package mg.orange.automatisation.entities;

public class Sauvegarde {
	private PServeur serv;
	private String[] sauvegarde;
		
	public Sauvegarde() {
	}

	public PServeur getServ() {
		return serv;
	}

	public void setServ(PServeur serv) {
		this.serv = serv;
	}

	public String[] getSauvegarde() {
		return sauvegarde;
	}

	public void setSauvegarde(String[] sauvegarde) {
		this.sauvegarde = sauvegarde;
	}
	
	
}
