package mg.orange.automatisation.dassh;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.reseauException;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;

public class ReseauDASSH {

	public ReseauDASSH() {
	}
	
	//creation d'un reseau sur le serveur
	public static void DeployerReseau(Reseau reseau, Utilisateur user,Serveur serv) throws serveurException, reseauException
	{
		try {
				//connexion
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));
				
				//teste deploy reseau
				if(reseau.getType().equals("local")) {
					
					if(sshConnex.ExecuterCommandeVerifRetour("docker network create " + reseau.getNom_reseau() + " --subnet=" + reseau.getIp_reseau().toString() + "/" + reseau.getMasque_reseau())!=0) 
							throw new reseauException("Impossible de creer le reseau " + reseau.getNom_reseau() + " de IP " + reseau.getIp_reseau().toString());
				
				}				
				else if(reseau.getType().equals("overlay"))
				{
					if(sshConnex.ExecuterCommandeVerifRetour("docker network create --driver=overlay  --attachable " + reseau.getNom_reseau() + " --subnet=" + reseau.getIp_reseau().toString() + "/" + reseau.getMasque_reseau())!=0)
						throw new reseauException("Impossible de creer le reseau " + reseau.getNom_reseau() + " de IP " + reseau.getIp_reseau().toString());
				
				}
				else throw new reseauException("Deffaillance generale");
				
			} catch (sshException e) {
					throw new serveurException(e.getMessage());
			}
		
	}
	
	//trouver IP libre
	public static List<IP> scannerIp(IP reseau, int Masque, int nb_adresse, List<IP> Ip_occuper, Utilisateur user, Serveur serv) throws sshException, serveurException
	{
		//Ip à scanner
		List<IP> Adresse_ip = new ArrayList<>();	
		
		//Ip sur le reseau
		List<IP> ip_total = recupIp(reseau,Masque);
		
		//Ignorer adresse .1 et .2
		int k = 2;
		
		//boucle scanne 
		for (int i = 1; i <= nb_adresse; i++) {
			
			while(true) {					
					InetAddress ad;
					
					try {						
						ad = InetAddress.getByName(ip_total.get(k).toString());		
						
						//teste si adresse ip libre
						if(ping(ad.getHostAddress(),user,serv)==false && !Ip_occuper.contains(ip_total.get(k))) {
							{
								Adresse_ip.add(ip_total.get(k));	
								k++;
								break;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					k++;
				}
			}
		return Adresse_ip;
	}
	
	//recuperer ip possible sur un reseau
	private static List<IP> recupIp(IP reseau,int Masque)
		{
			List<IP> ip = new ArrayList<>();
			
			int n_adresse_total = 32 - Masque;
			String Bin = "";
			
			for (int i = 0; i<n_adresse_total; i++) {
				Bin = Bin + "1";
			}
			
			try {
				n_adresse_total = parse(Bin) - 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			int k = 0;
			int l = 0;
			int m = 0;
			int n = 0;
			
			for (int i = 0; i < n_adresse_total; i++) {
				k++;
				if(k==255)
					{k=1;l++;}
				if(l==255)
					{l=0;m++;}
				if(m==255)
					{m=0;n++;}
					
				ip.add(new IP(reseau.getPart1()+n,reseau.getPart2()+m,reseau.getPart3()+l,reseau.getPart4()+k));				
			}
			
			return ip;
		}
	
	//parse
	private static int parse(String nbr) throws Exception {

        int puissance = 1;
        int resultat = 0;
        
        for (int i = nbr.length() - 1; i >= 0; i--) {

            if (nbr.charAt(i) == '1') {
                resultat = resultat + puissance;
            }else if(nbr.charAt(i) != '0'){
                throw new Exception("Nombre binaire incorrecte!");
            }
            puissance = puissance * 2;
        }

        return resultat;

    }

	////ping
	private static boolean ping(String adresse, Utilisateur user, Serveur serv) throws serveurException
	{		
		try {
				//creation de sshConnexion
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				
				//teste si ping Ok
				if(sshConnex.pinger(adresse)==0)
					return true;
				else
					return false;
			
			} catch (sshException e) {
					throw new serveurException(e.getMessage());
			}
	}
	
	//tester si un reseau existe
	public static void testerReseau(String net, Utilisateur user) throws reseauException, serveurException {
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));		
				
				if(sshConnex.ExecuterCommandeVerifRetour("docker network inspect " + net)!=0)
				{
					throw new reseauException("Reseau Introuvable");
				}
				
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
		
	}
	
	//suppression d'un reseau
	public static void SupprimerReseau(Reseau res, Utilisateur user) throws reseauException, serveurException
	{
		try {
			SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));		
			
			if(sshConnex.ExecuterCommandeVerifRetour("docker network rm " + res.getNom_reseau())!=0)
			{
				throw new reseauException("Impossible de supprimer le reseau " + res.getNom_reseau());
			}
			
	} catch (sshException e) {
			throw new serveurException(e.getMessage());
	}
	}

}
