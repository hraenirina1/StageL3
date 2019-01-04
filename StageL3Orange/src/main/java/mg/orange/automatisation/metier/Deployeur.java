package mg.orange.automatisation.metier;

import java.util.ArrayList;
import java.util.List;

import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;

public class Deployeur {
	private SshConnection sshConnex;
	private List<String> commandes;	
	
	public Deployeur(SshConnection sshConnex) {
		super();
		this.sshConnex = sshConnex;
		commandes = new ArrayList<>();
	}
	public SshConnection getSshConnex() {
		return sshConnex;
	}
	public void setSshConnex(SshConnection sshConnex) {
		this.sshConnex = sshConnex;
	}
	public List<String> getCommandes() {
		return commandes;
	}
	public void setCommandes(List<String> commandes) {
		this.commandes = commandes;
	}
	
	/************************************  DEPLOY COMMANDE *****************************************************/
	
	public Boolean ReelDeployer(BdServeur bdServeur)
	{
		creerDossier("MariaConf");
		creerDossier("MariaConf/env");
		creerDossier("MariaConf/init");
		
		creerInit("MariaConf/init/initialize.sql", bdServeur.getMysqlPasssword());
		
		for (dockerserveur dserv : bdServeur.getDserveur()) {
			creerEnv("MariaConf/env/node"+ dserv.getNom_docker_serveur() +".env",dserv);			
		}
		
		CreerDeployer("MariaConf/deploy.sh",bdServeur);
		
		Boolean exit_status = sshConnex.ExecuterCommandeVerifRetour("cd MariaConf && bash deploy.sh")==0?true:false;
		
		//demarage ssh
		for(dockerserveur doc: bdServeur.getDserveur())
		{
			commandes.add("echo /sbin/sshd-keygen > script.sh");
			commandes.add("echo /sbin/sshd >> script.sh");
			
			commandes.add("echo \""+bdServeur.getMysqlPasssword() + "\" >> mdp");
			commandes.add("echo \""+bdServeur.getMysqlPasssword() + "\" >> mdp");
			commandes.add("echo \"passwd < mdp \" >> script.sh");
			commandes.add("echo \"rm mdp\" >> script.sh");
			
			commandes.add("docker cp script.sh "+ doc.getNom_docker_serveur() +":/");
			commandes.add("docker cp mdp "+ doc.getNom_docker_serveur() +":/ ");
			commandes.add("rm mdp");
			commandes.add("rm script.sh");
			commandes.add("docker exec "+ doc.getNom_docker_serveur() +" bash script.sh");
		}
		
		commandes.add("rm -Rvf MariaConf");
		sshConnex.ExecuterCommande(commandes);
		commandes.clear();
		
		return exit_status;
	}

	//****** creer dossier et fichier
	private boolean creerDossier(String nomDossier)
	{
		commandes.add("mkdir \""+ nomDossier +"\";");
		sshConnex.ExecuterCommande(commandes);
		commandes.clear();
		return true;
	}
	private boolean creerFichier(Fichier fich)
	{ 
		commandes.add("echo \""+ fich.toString() +"\" > " + fich.getNomFichier() + ";");
		sshConnex.ExecuterCommande(commandes);
		commandes.clear();
		return true;
	}

	//***** deployer ficher config
	private Boolean creerInit(String destination,String mdp)
	{
		Fichier init = new Fichier(destination);
		init.ajouterLigne("CREATE USER 'su_root'@'%' IDENTIFIED BY '"+ mdp +"';\r\n" + 
				"GRANT ALL ON *.* TO 'su_root'@'%';\r\n");
		creerFichier(init);
		return true;
	}
	
	//creation des fichiers d'environnemt
	private Boolean creerEnv(String destination, dockerserveur dserv) {
		Fichier env = new Fichier(destination);
		
		for (dockerConfig dconf : dserv.getDockerConfig()) {
			env.ajouterLigne(dconf.getConfigDocker().getMot_cle() + "=" + dconf.getValeur());
		}		
		
		creerFichier(env);
		
		return true;		
	}
	
	//creation du fichier de deploiement
	private Boolean CreerDeployer(String destination, BdServeur bdserv)
	{
		String ram = "96m";
		String cpus = "0.2";
		
		Fichier deploy = new Fichier(destination);
		deploy.ajouterLigne("#!/usr/bin/env bash\n" + 
				"#HARINIAINA RAMIANDRISOA\n");
		
		deploy.ajouterLigne("REPO_DIR=\\$(pwd)\n");
		deploy.ajouterLigne("GALERANET=\\$(docker network inspect "+ bdserv.getNomBdServeur() + ")\n");
		deploy.ajouterLigne("if [[ \"\\${GALERANET}\" = '[]' ]]; then\n" + 
							"    docker network create -d overlay --attachable --subnet="+bdserv.getAdresseReseau().toString() +"/" + String.valueOf(bdserv.getMasque()) + " "+ bdserv.getNomBdServeur() +"\n" + 
							"fi\n");
		
		StringBuilder depl = new StringBuilder();
		depl.append("docker run -d --memory="+ram+" --cpus=" + cpus + " --name "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" -h "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" ");
		depl.append("-v \\${REPO_DIR}/init:/init ");
		depl.append("--env-file=env/node"+ bdserv.getDserveur().get(0).getNom_docker_serveur() +".env ");
		depl.append("--net " + bdserv.getNomBdServeur() + " ");
		depl.append("--ip "+ bdserv.getDserveur().get(0).getIp_interne().toString()+" ");
							
					for(int i=1 ;i<bdserv.getDserveur().size();i++)
					{
						depl.append("--add-host "+bdserv.getDserveur().get(i).getNom_docker_serveur()+":"+bdserv.getDserveur().get(i).getIp_interne().toString()+ " ");
					}
		
		depl.append("-p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":22:22 -p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":3306:3306 -p 4444 -p 4567 -p 4568 hraenirina1/test_stage -vif initialize.sql mysqld \n");
		deploy.ajouterLigne(depl.toString());
		
		for(int j=1 ;j<bdserv.getDserveur().size();j++)
		{
			StringBuilder deplo = new StringBuilder();
			deplo.append("docker run -d --memory="+ram+" --cpus=" + cpus + " --name " + bdserv.getDserveur().get(j).getNom_docker_serveur() +" -h "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" ");
			deplo.append("--env-file=env/node"+bdserv.getDserveur().get(j).getNom_docker_serveur() +".env ");
			deplo.append("--net " +  bdserv.getNomBdServeur() + " ");
			deplo.append(" --ip "+  bdserv.getDserveur().get(j).getIp_interne().toString() +" ");
		
						for(int i=0 ;i<bdserv.getDserveur().size();i++)
						{
							if(i!=j)		
								{deplo.append("--add-host "+ bdserv.getDserveur().get(i).getNom_docker_serveur() +":"+bdserv.getDserveur().get(i).getIp_interne().toString() +" ");}
						}		
			deplo.append("-p "+bdserv.getDserveur().get(j).getIp_docker().toString()+":22:22 -p "+bdserv.getDserveur().get(j).getIp_docker().toString()+":3306:3306 -p 4444 -p 4567 -p 4568 hraenirina1/test_stage -vj mysqld");
			
			deploy.ajouterLigne(deplo.toString());
		}	
		
		
		deploy.ajouterLigne("exit 0;");
		
		creerFichier(deploy);
		return true;
	}
	
	/********************************** Teste Reseau *********************************************************/
	
	public int testerReseau(String net) {
		return sshConnex.ExecuterCommandeVerifRetour("docker network inspect " + net);
	}
	public int DeployerReseau(Reseau res)
	{
		if(res.getType().equals("local")) {System.out.println("docker network create " + res.getNom_reseau() + " --subnet=" + res.getIp_reseau().toString() + "/" + res.getMasque_reseau());
			return sshConnex.ExecuterCommandeVerifRetour("docker network create " + res.getNom_reseau() + " --subnet=" + res.getIp_reseau().toString() + "/" + res.getMasque_reseau());}
		else if(res.getType().equals("overlay"))
		{
			return sshConnex.ExecuterCommandeVerifRetour("docker network create --driver=overlay  --attachable " + res.getNom_reseau() + " --subnet=" + res.getIp_reseau().toString() + "/" + res.getMasque_reseau());
		}
		else return -1;	
	}
	public int SupprimerReseau(Reseau res)
	{
		return sshConnex.ExecuterCommandeVerifRetour("docker network rm " + res.getNom_reseau());
	}
	
}
