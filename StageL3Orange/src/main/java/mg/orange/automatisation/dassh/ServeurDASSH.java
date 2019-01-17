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
	
	public static String[] listeSauvegarde( Utilisateur user, Serveur serv) throws serveurException
	{
		try {
				String[] list;
				
				//connnexion
				SshConnection sshCon = SshConnection.CreerConnection(new SshConfig(user));		
				
				//list sauvegarde
				String save = sshCon.ExecuterCommandeRecupOut("cd /srv/backup/mariadb/ && ls");				
				list = save.split("\n");
				return list;
					
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
	
	public static void testerDocker(Utilisateur user) throws serveurException
	{ 
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				if(sshConnex.ExecuterCommandeVerifRetour("docker")!=0)
				{
					throw new serveurException("Le serveur n'a pas de docker");
				}
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	public static void testSshConnexion(Utilisateur user) throws serveurException
	{ 
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				if(sshConnex.ExecuterCommandeVerifRetour("netstat -paunt | grep LISTEN | grep 0.0.0.0:22")!=1)
				{
					throw new serveurException("Le serveur possede une multi - ecoute ssh");
				}
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	public static void testMysqlConnexion(Utilisateur user) throws serveurException
	{ 
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				if(sshConnex.ExecuterCommandeVerifRetour("netstat -paunt | grep LISTEN | grep 0.0.0.0:3306")!=1)
				{
					throw new serveurException("Le serveur possede une multi - ecoute Mariadb");
				}
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
}
