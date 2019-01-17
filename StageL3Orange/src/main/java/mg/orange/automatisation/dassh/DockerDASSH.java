package mg.orange.automatisation.dassh;

import java.util.ArrayList;
import java.util.List;

import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Config;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Stat;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;
import mg.orange.automatisation.exception.ConfigException;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;

public class DockerDASSH {

	public DockerDASSH() {
	
	}
	
	public static List<dockerserveur> configurer(Utilisateur user, Serveur serv, BdServeur bd,int nb_docker,String ram, String cpu, List<Config> configBase,List<IP> ip_dao) throws serveurException, ConfigException
	{
		List<dockerserveur> dockers = new ArrayList<>();
		
		try {
				//teste de connexion				
				SshConnection.CreerConnection(new SshConfig(user));	
				
				//Ip à scanner
				List<IP> adresseIp;
				List<IP> adresseIpInterne;
				
				//scanne IP
				adresseIpInterne = ReseauDASSH.scannerIp(bd.getReseau().getIp_reseau(),bd.getReseau().getMasque_reseau(), nb_docker,ip_dao,user,serv);
				adresseIp = ReseauDASSH.scannerIp(bd.getAdresseReseau(),bd.getMasque(),nb_docker,ip_dao,user,serv);
				
				//creation des dockerserveurs
				int i = 1;
				
				for (IP ip : adresseIp) {
					
					//creation du docker
					dockerserveur docker = new dockerserveur(bd.getNomBdServeur() + "-galera-" + i,ip,bd);
					
					//ressource docker 
					docker.setRam(ram);
					docker.setCpu(cpu);
					
					//assignement du ip interne
					docker.setIp_interne(adresseIpInterne.get(i-1));
					
					//deployer IP sur le serveur
					ServeurDASSH.miseEnPlaceIp(docker.getIp_docker(), "enp0s8", user, serv);
					
					//configuration de docker
					List<dockerConfig> dconf = new ArrayList<>();
					
					//teste si aucun config
					if(configBase.size()==0) throw new ConfigException("pas de configuration");
								
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
								
								//assigner conf a docker
								docker.setDockerConfig(dconf);
								
								//enregister docker
								dockers.add(docker);
								
								i++;
							}
									
			} catch (sshException e) {
					throw new serveurException(e.getMessage());
			} catch (serveurException e) {				
				throw new serveurException(e.getMessage());
			}
		
		return dockers;
	}
	public static void configurerDocker(dockerserveur doc, String pass, Utilisateur user) throws serveurException {
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
				
				commandes.add("docker cp script.sh "+ doc.getNom_docker_serveur() +":/");
				commandes.add("docker cp mdp "+ doc.getNom_docker_serveur() +":/ ");
				commandes.add("rm mdp");
				commandes.add("rm script.sh");
				
				//lancement
				commandes.add("docker exec "+ doc.getNom_docker_serveur() +" bash script.sh");
				sshConnex.ExecuterCommande(commandes);
			
	} catch (sshException e) {
			throw new serveurException(e.getMessage());
	}
}
	public static Stat statistique(String ip, Utilisateur user) throws serveurException
	{
		try {
				SshConnection connectionssh = SshConnection.CreerConnection(new SshConfig(user));
				
				Stat stat = new Stat();
				//stat	
					String[] ligne = 
							connectionssh.ExecuterCommandeRecupOutStat("nc "+ip+" 1234").split("\n");
					System.out.println(ligne.toString());
					/*
					 * 1 - ram total
					 * 2 - ram libre
					 * 3 - ram utiliser
					 * 
					 * 4 - cpu sys
					 * 5 - cpu ni
					 * 6 - cpu libre
					 * 
					 * 0verlay - 
					 * */
					//int i = Integer.parseInt(ligne[1]) + Integer.parseInt(ligne[2]);
					stat.setRAM(""+ ligne[1] +"");	
					
					Double j = Double.valueOf(ligne[3]) + Double.valueOf(ligne[4]);
					//Double k = j + Double.valueOf(ligne[5]);
					
					stat.setCPU(""+ j +"");
					stat.setDisque(ligne[7]);
						
					connectionssh.ExecuterCommandeRecupOut("pkill -fx 'nc "+ip+" 1234'");
					
					return stat;
			} catch (sshException e) {
					throw new serveurException(e.getMessage());
			}
	}
}
