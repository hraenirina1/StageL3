package mg.orange.automatisation.dassh;

import java.util.List;

import org.springframework.stereotype.Service;

import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Stat;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import mg.orange.automatisation.entities.DockerServeur;


@Service
public class DockerDASSH {

	public DockerDASSH() {
	
	}
	
	public List<IP> recupererIPDisponible(Utilisateur user, Reseau reseau, int nb, List<IP> ipOccupe) throws serveurException
	{
		return scannerIp(reseau.getIp(), reseau.getMasque_reseau(), nb, ipOccupe, user);
	}
	
	//trouver IP libre
	public List<IP> scannerIp(IP reseau, int Masque, int nb_adresse, List<IP> Ip_occuper, Utilisateur user) throws serveurException
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
						if(ping(ad.getHostAddress(),user)==false && !Ip_occuper.contains(ip_total.get(k))) {
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
	private List<IP> recupIp(IP reseau,int Masque)
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
	private int parse(String nbr) throws Exception {

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
	private boolean ping(String adresse, Utilisateur user) throws serveurException
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
	
	//configurer Actif docker
	public void configurerDocker(DockerServeur doc, String pass, Utilisateur user) throws serveurException {
		try {
			
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));
				List<String> commandes = new ArrayList<>();
				
				//gerer ssh
				commandes.add("echo /sbin/sshd-keygen > script.sh");
				commandes.add("echo /sbin/sshd >> script.sh");
				
				//envoy stat
				commandes.add("echo /sbin/crond >> script.sh ");
				commandes.add("echo \"(crontab -l; echo \\\"* * * * * bash /stat.sh\\\") | crontab - \" >> script.sh ");
				
				//changement mot de passe
				commandes.add("echo \""+ pass + "\" >> mdp");
				commandes.add("echo \""+ pass + "\" >> mdp");
				commandes.add("echo \"passwd < mdp \" >> script.sh");
				commandes.add("echo \"rm mdp\" >> script.sh");
				
				commandes.add("docker cp script.sh "+ doc.getNom() +":/");
				commandes.add("docker cp mdp "+ doc.getNom() +":/ ");
				commandes.add("rm mdp");
				commandes.add("rm script.sh");
				
				//lancement
				commandes.add("docker exec "+ doc.getNom() +" bash script.sh");
				sshConnex.ExecuterCommande(commandes);
			
	} catch (sshException e) {
			throw new serveurException(e.getMessage());
	}
}
	
	public Stat statistique(DockerServeur doc , Utilisateur user) throws serveurException
	{
		try {
				SshConnection connectionssh = SshConnection.CreerConnection(new SshConfig(user));
				
				Stat stat = new Stat();
				
					String[] cpuram = connectionssh.ExecuterCommandeRecupOut("docker stats --no-stream --format {{.Name}}\\\t{{.CPUPerc}}\\\t{{.MemPerc}} " + doc.getNom()).split("\t");
					String[] disque = connectionssh.ExecuterCommandeRecupOut("docker ps -s --format \"{{.Size}}\\\t{{.Names}}\" | grep " + doc.getNom()).split(" ");
					
					if(cpuram.length>=3 && disque.length>=2)
					{
						stat.setRAM(cpuram[2].toString().replaceAll("%",""));							
						stat.setCPU(cpuram[1].toString().replaceAll("%",""));	
						stat.setDisque(disque[0].toString());
					}
					
					return stat;
			} catch (Exception e) {
					throw new serveurException(e.getMessage());
			}
	}
}
