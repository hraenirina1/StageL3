package mg.orange.automatisation.dassh;

import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;

public class ServeurDASSH {

	public ServeurDASSH() {
	}	
	
	public static void testerServeur(Utilisateur user) throws serveurException {
		try {
				SshConnection.CreerConnection(new SshConfig(user));				
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	public static void testerServeur(SshConfig conf) throws serveurException {
		try {
				SshConnection.CreerConnection(conf);				
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	public static void miseEnPlaceIp(IP ip, String interface_serveur, Utilisateur user, Serveur serv) throws serveurException
	{
		try {
				//connnexion
				SshConnection sshCon = SshConnection.CreerConnection(new SshConfig(user));		
				
				//mettre en place reseau
				if(sshCon.ExecuterCommandeVerifRetour("ifconfig " + interface_serveur + " add " + ip)!=0)
					throw new serveurException("Impossible d'assigner l'ip");
					
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
		
	}
	
	public static void RetirerEnPlaceIp(IP ip, String interface_serveur, Utilisateur user) throws serveurException
	{
		try {
				//connnexion
				SshConnection sshCon = SshConnection.CreerConnection(new SshConfig(user));		
				
				//mettre en place reseau
				if(sshCon.ExecuterCommandeVerifRetour("ifconfig " + interface_serveur + " del " + ip)!=0)
					throw new serveurException("Impossible supprimer l'ip");
					
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
		
	}
	
}
