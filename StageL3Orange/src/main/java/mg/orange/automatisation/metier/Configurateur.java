package mg.orange.automatisation.metier;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.validation.executable.ExecutableValidator;

import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Config;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;

public class Configurateur {

	private SshConfig ssh;	

	public Configurateur(SshConfig ssh) {
		super();
		this.ssh = ssh;
	}
	
	/////// hyper important 
	public List<IP> scannerIp(int nb_adresse,IP reseau,int Masque) throws Exception
	{
		List<IP> Adresse_ip = new ArrayList<>();	
		List<IP> ip_total = recupIp(reseau,Masque);
		int k = 2;
		
		for (int i = 1; i <= nb_adresse; i++) {
			
			while(true) {					
					InetAddress ad;
					
					try {						
						ad = InetAddress.getByName(ip_total.get(k).toString());

						if(ping(ad.getHostAddress())==false) {
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
	public List<dockerserveur> configurer(BdServeur bd,int nb_docker,List<Config> configBase) throws Exception
	{
		List<dockerserveur> dockers = new ArrayList<>(); 
		
		//scanner Ip
		List<IP> adresseIp;
		List<IP> adresseIpInterne;
		try {
			adresseIpInterne = scannerIp(nb_docker,bd.getReseau().getIp_reseau(),bd.getReseau().getMasque_reseau());
			adresseIp = scannerIp(nb_docker,bd.getAdresseReseau(),bd.getMasque());
			
			//creation des dockerserveurs
			int i = 1;
			for (IP ip : adresseIp) {
				
				dockerserveur docker = new dockerserveur(bd.getNomBdServeur() + "-galera-" + i,ip,bd);
				docker.setIp_interne(adresseIpInterne.get(i-1));
				SshConnection sshCon = SshConnection.CreerConnection(ssh);
				if(sshCon!=null)
				{
					if(sshCon.ExecuterCommandeVerifRetour("ifconfig enp0s8 add " + docker.getIp_docker().toString())==0)
					{
							List<dockerConfig> dconf = new ArrayList<>();
							if(configBase.size()==0) throw new Exception("pas de configuration");
							for (Config conf1 : configBase) {
								
								switch (conf1.getMot_cle()) {
								
								case "WSREP_ON":
									dconf.add(new dockerConfig(conf1, "ON"));									
									break;
								
								case "WSREP_PROVIDER":
									dconf.add(new dockerConfig(conf1, "/usr/lib64/galera/libgalera_smm.so"));	
									break;
								
								case "WSREP_PROVIDER_OPTIONS":
									dconf.add(new dockerConfig(conf1, ""));	
									break;
									
								case "WSREP_CLUSTER_ADDRESS":
									String valeur = "'gcomm://";
									valeur += adresseIpInterne.get(0);
									
									for(int j=1;j < adresseIpInterne.size();j++) {
										valeur +="," + adresseIpInterne.get(j);
									};
									
									dconf.add(new dockerConfig(conf1, valeur + "'"));	
									break;					
									
								case "WSREP_CLUSTER_NAME":
									dconf.add(new dockerConfig(conf1, "'" + bd.getNomBdServeur() + "'"));	
									break;
									
								case "WSREP_NODE_ADDRESS":
									dconf.add(new dockerConfig(conf1, "'" + adresseIpInterne.get(i-1) + "'"));
									break;
									
								case "WSREP_NODE_NAME":
									dconf.add(new dockerConfig(conf1, "'" + docker.getNom_docker_serveur() + "'"));	
									break;
									
								case "WSREP_SST_METHOD":
									dconf.add(new dockerConfig(conf1, "rsync"));	
									break;
									
								case "BINLOG_FORMAT":
									dconf.add(new dockerConfig(conf1, "row"));	
									break;
									
								case "DEFAULT_STORAGE_ENGINE":
									dconf.add(new dockerConfig(conf1, "InnoDB"));	
									break;
									
								case "INNODB_AUTOINC_LOCK_MODE":
									dconf.add(new dockerConfig(conf1, "2"));	
									break;
									
								case "BIND_ADDRESS":
									dconf.add(new dockerConfig(conf1, "0.0.0.0"));	
									break;
									
								case "MYSQL_ROOT_PASSWORD":
									dconf.add(new dockerConfig(conf1, bd.getMysqlPasssword()));	
									break;

								default:
									System.out.println(conf1.getMot_cle());
									break;
								}				
							}
							
							docker.setDockerConfig(dconf);
							dockers.add(docker);
							i++;
						}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return dockers;

	}
	
	//ping
	private boolean ping(String adresse)
	{
			try {
				SshConnection sshConnex = SshConnection.CreerConnection(ssh);
				if(sshConnex!=null)
				{
					if(sshConnex.pinger(adresse)==0)
						return true;
					else
						return false;
				}
				else
					return false;
			} catch (Exception e) {				
				e.printStackTrace();
				return false;
			}
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
	//recuperer ip
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
			// TODO Auto-generated catch block
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
}
  