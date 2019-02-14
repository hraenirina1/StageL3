package mg.orange.automatisation.metier;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.ConfigDAO;
import mg.orange.automatisation.dao.DockerServeurDAO;
import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.dassh.DockerDASSH;
import mg.orange.automatisation.entities.BDServeur;
import mg.orange.automatisation.entities.Config;
import mg.orange.automatisation.entities.DockerServeur;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.Stat;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.ConfigException;
import mg.orange.automatisation.exception.serveurException;

@Service
public class DockerMetier {
	//dassh
	@Autowired
	private DockerDASSH dockerservdassh; 
	
	//dao 
	@Autowired
	private DockerServeurDAO dockerserv;
	@Autowired
	private BdServeurDAO bdserv;
	@Autowired
	private IPDAO ipdao;
	@Autowired
	private ConfigDAO configdao; 
	
	public DockerMetier() {
		
	}
	public BDServeur recupererBDServeur(Long id)
	{
		return bdserv.getOne(id);
	}
	public DockerServeur recuperer(Long id)
	{
		return dockerserv.getOne(id);
	}
	public List<DockerServeur> recupererDocker(Long id)
	{
		return dockerserv.findBybdserveur(recupererBDServeur(id));
	}
	public void enregistrer(DockerServeur dockerserv)
	{
		this.dockerserv.save(dockerserv);
	}
	
	public List<IP> recupererIpDisponible(Utilisateur user, Reseau reseau,int nb) throws serveurException
	{
		return dockerservdassh.recupererIPDisponible(user,reseau,nb,ipdao.findAll());
	}
	
	public Stat statistique(DockerServeur dserv, Utilisateur user) throws serveurException
	{
		Stat stat;
				
		stat = dockerservdassh.statistique(dserv, user);
		return new Stat(Superviseur.testMysql(dserv.getIp().toString()),stat.getRAM(),stat.getDisque(),stat.getCPU());				
	}
	
	public List<DockerServeur> ConfigurerDocker(List<IP> adresseIp,String ram, String cpu, BDServeur bd) throws ConfigException
	{
			List<DockerServeur> dockers = new ArrayList<>();
			
					
					List<IP> adresseIpInterne = new ArrayList<>();
					
					for (int i = 1; i <= adresseIp.size(); i++) {
						IP ipp = new IP(bd.getReseau_interne().getIp().getPart1(), bd.getReseau_interne().getIp().getPart2(),bd.getReseau_interne().getIp().getPart3(), bd.getReseau_interne().getIp().getPart4()+5+i);
						adresseIpInterne.add(ipp);						
					}
					
					//creation des dockerserveurs
					int i = 1;
					
					for (IP ip : adresseIp) {
						
						//creation du docker
						DockerServeur docker = new DockerServeur(bd.getNom() + "-galera-" + i,ip,bd);
						
						//ressource docker 
						docker.setRam(ram);
						docker.setCpu(cpu);
						
						//assignement du ip interne
						docker.setIp_interne(adresseIpInterne.get(i-1));						
						
						//deployer IP sur le serveur
						//ServeurDASSH.miseEnPlaceIp(docker.getIp_docker(), "enp0s8", user, serv);
						
						//teste si aucun config
						if(configdao.findAll().size()==0) throw new ConfigException("pas de configuration");
						docker = dockerserv.save(docker);
						
						for (Config conf1 : configdao.findAll()) {
										
										switch (conf1.getMot_cle()) {
										
										case "WSREP_ON":
											docker.addConfig(conf1,"ON");									
											break;
										
										case "WSREP_PROVIDER":
											docker.addConfig(conf1,"/usr/lib64/galera/libgalera_smm.so");	
											break;
										
										case "WSREP_PROVIDER_OPTIONS":
											docker.addConfig(conf1,"");	
											break;
											
										case "WSREP_CLUSTER_ADDRESS":
											String valeur = "'gcomm://";
											valeur += adresseIpInterne.get(0);
											
											for(int j=1;j < adresseIpInterne.size();j++) {
												valeur +="," + adresseIpInterne.get(j);
											};
											
											docker.addConfig(conf1,valeur + "'");	
											break;					
											
										case "WSREP_CLUSTER_NAME":
											docker.addConfig(conf1, "'" + bd.getNom() + "'");	
											break;
											
										case "WSREP_NODE_ADDRESS":
											docker.addConfig(conf1,"'" + adresseIpInterne.get(i-1) + "'");
											break;
											
										case "WSREP_NODE_NAME":
											docker.addConfig(conf1,"'" + docker.getNom() + "'");	
											break;
											
										case "WSREP_SST_METHOD":
											docker.addConfig(conf1,"rsync");	
											break;
											
										case "BINLOG_FORMAT":
											docker.addConfig(conf1,"row");	
											break;
											
										case "DEFAULT_STORAGE_ENGINE":
											docker.addConfig(conf1,"InnoDB");	
											break;
											
										case "INNODB_AUTOINC_LOCK_MODE":
											docker.addConfig(conf1,"2");	
											break;
											
										case "BIND_ADDRESS":
											docker.addConfig(conf1,"0.0.0.0");	
											break;
											
										case "MYSQL_ROOT_PASSWORD":
											docker.addConfig(conf1,bd.getMysqlPasssword());	
											break;
	
										default:
											System.out.println(conf1.getMot_cle());
											break;
										}				
									}									
									
									//enregister docker
									dockers.add(docker);
									
									i++;
								}			
			return dockers;
		}
	}
