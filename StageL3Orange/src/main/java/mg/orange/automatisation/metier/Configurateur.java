package mg.orange.automatisation.metier;

import java.util.List;

public class Configurateur {
	private SshConnection sshConnex;
	private List<String> commandes;
	
	public Configurateur(SshConnection connex) {
		sshConnex = connex;
	}

	public SshConnection getSshConnex() {
		return sshConnex;
	}

	public void setSshConnex(SshConnection sshConnex) {
		this.sshConnex = sshConnex;
	}
	
	public boolean creerDossier(String nomDossier)
	{
		commandes.add("mkdir \""+ nomDossier +"\";");
		sshConnex.ExecuterCommande(commandes);
		commandes.clear();
		return true;
	}
	
	public boolean creerFicher(String destination, String Contenu_Fichier)
	{
		commandes.add("echo \""+ Contenu_Fichier +"\" > " + destination);
		sshConnex.ExecuterCommande(commandes);
		commandes.clear();
		return true;
	}
}
