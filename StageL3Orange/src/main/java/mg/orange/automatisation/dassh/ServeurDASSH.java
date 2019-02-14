package mg.orange.automatisation.dassh;

import org.springframework.stereotype.Service;

import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.PServeur;
//import mg.orange.automatisation.entities.PServeur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;

@Service
public class ServeurDASSH {

	public ServeurDASSH() {
	}	
	
	public void testerConnexion(Utilisateur user) throws serveurException {
		try {
				SshConnection.CreerConnection(new SshConfig(user));				
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	

	public String[] listeSauvegarde( Utilisateur user, PServeur serv) throws serveurException
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
	


	public void testeDisponibiliteIP(Utilisateur user,IP ip) throws serveurException
	{
		try {
				//connnexion
				SshConnection sshCon = SshConnection.CreerConnection(new SshConfig(user));		
				
				//teste si ping Ok
				if(sshCon.pinger(ip.toString())==0)
					throw new serveurException("L'adresse IP " + ip.toString()+ " est occupé");
				
	} catch (sshException e) {
			throw new serveurException(e.getMessage());
	}
	}
	public void testerDocker(Utilisateur user) throws serveurException
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
	public void testSshConnexion(Utilisateur user) throws serveurException
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
	public void testMysqlConnexion(Utilisateur user) throws serveurException
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
