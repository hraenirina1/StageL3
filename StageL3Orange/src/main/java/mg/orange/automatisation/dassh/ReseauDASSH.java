package mg.orange.automatisation.dassh;

import org.springframework.stereotype.Service;

//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.util.ArrayList;
//import java.util.List;

import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.reseauException;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;
@Service
public class ReseauDASSH {

	public ReseauDASSH() {
		
	}
	
	//creation d'un reseau sur le serveur
	public void DeployerReseau(Reseau reseau, Utilisateur user) throws serveurException, reseauException
	{
		try {
				//connexion
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));

				//teste deploy reseau
				if(reseau.getType().equals("local")) {
					
					if(sshConnex.ExecuterCommandeVerifRetour("docker network create " + reseau.getNom() + " --subnet=" + reseau.getIp().toString() + "/" + reseau.getMasque_reseau())!=0) 
							throw new reseauException("Impossible de creer le reseau " + reseau.getNom() + " de IP " + reseau.getIp().toString());
				
				}				
				else if(reseau.getType().equals("overlay"))
				{
					if(sshConnex.ExecuterCommandeVerifRetour("docker network create --driver=overlay  --attachable " + reseau.getNom() + " --subnet=" + reseau.getIp().toString() + "/" + reseau.getMasque_reseau())!=0)
						throw new reseauException("Impossible de creer le reseau " + reseau.getNom() + " de IP " + reseau.getIp().toString());
				
				}
				else throw new reseauException("Deffaillance generale");
				
			} catch (sshException e) {
					throw new serveurException(e.getMessage());
			}
		
	}

	
//	//tester si un reseau existe
//	public static void testerReseau(String net, Utilisateur user) throws reseauException, serveurException {
//		try {
//				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));		
//				
//				if(sshConnex.ExecuterCommandeVerifRetour("docker network inspect " + net)!=0)
//				{
//					throw new reseauException("Reseau Introuvable");
//				}
//				
//		} catch (sshException e) {
//				throw new serveurException(e.getMessage());
//		}
//		
//	}
//	
	//suppression d'un reseau
	public void SupprimerReseau(Reseau res, Utilisateur user) throws reseauException, serveurException
	{
		try {
			SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));		
			
			if(sshConnex.ExecuterCommandeVerifRetour("docker network rm " + res.getNom())!=0)
			{
				throw new reseauException("Impossible de supprimer le reseau " + res.getNom());
			}
			
	} catch (sshException e) {
			throw new serveurException(e.getMessage());
	}
	}

}
