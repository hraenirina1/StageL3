package mg.orange.automatisation.dassh;

import java.util.ArrayList;
import java.util.List;

import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Fichier;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;

public class BdServeurDASSH {

	public BdServeurDASSH() {
	}
	
	public static void testerServeur(Utilisateur user) throws serveurException {
		try {
				SshConnection.CreerConnection(new SshConfig(user));	
				
				
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	//****** creer dossier et fichier
	private static void creerDossier(String nomDossier, Utilisateur user) throws serveurException
	{
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				List<String> commandes = new ArrayList<>();
			
				commandes.add("mkdir \""+ nomDossier +"\";");
				sshConnex.ExecuterCommande(commandes);
			
			} catch (sshException e) {
					throw new serveurException(e.getMessage());
			}
	}
	private static void creerFichier(Fichier fich, Utilisateur user) throws serveurException
	{ 
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				List<String> commandes = new ArrayList<>();
		
				commandes.add("echo \""+ fich.toString() +"\" > " + fich.getNomFichier() + ";");
				sshConnex.ExecuterCommande(commandes);
		
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	//supprimer conf
	private static void supprimerConf(Utilisateur user) throws serveurException
	{ 
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				List<String> commandes = new ArrayList<>();
		
				commandes.add("rm -Rvf MariaConf");
				sshConnex.ExecuterCommande(commandes);
		
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	//******* deploy commande
	private static void deploy(Utilisateur user) throws serveurException {
		try {
				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
				sshConnex.ExecuterCommandeVerifRetour("cd MariaConf && bash deploy.sh");
				
		} catch (sshException e) {
				throw new serveurException(e.getMessage());
		}
	}
	
	//***** deployer ficher config
	private static void creerInit(String destination,String mdp, Utilisateur user) throws serveurException
	{
		Fichier init = new Fichier(destination);
		init.ajouterLigne("CREATE USER 'su_root'@'%' IDENTIFIED BY '"+ mdp +"';\r\n" + 
				"GRANT ALL ON *.* TO 'su_root'@'%';\r\n");
		
		creerFichier(init,user);
	}

	//creation des fichiers d'environnemt
	private static void creerEnv(String destination, dockerserveur dserv, Utilisateur user ) throws serveurException {			
			Fichier env = new Fichier(destination);
			
			for (dockerConfig dconf : dserv.getDockerConfig()) {
				env.ajouterLigne(dconf.getConfigDocker().getMot_cle() + "=" + dconf.getValeur());
			}		
				
			creerFichier(env,user);
		}
	
	////creation du fichier de deploiement
	private static void CreerDeployer(String destination, BdServeur bdserv, Utilisateur user) throws serveurException
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
		
		depl.append("-p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":22:22 -p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":1234:1234 -p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":3306:3306 -p 4444 -p 4567 -p 4568 hraenirina1/mariadb_galera_stage -vif initialize.sql mysqld \n");
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
			deplo.append("-p "+bdserv.getDserveur().get(j).getIp_docker().toString()+":22:22 -p "+bdserv.getDserveur().get(j).getIp_docker().toString()+ ":1234:1234 -p "+bdserv.getDserveur().get(j).getIp_docker().toString()+":3306:3306 -p 4444 -p 4567 -p 4568 hraenirina1/mariadb_galera_stage -vj mysqld");
			
			deploy.ajouterLigne(deplo.toString());
		}	
		
		
		deploy.ajouterLigne("exit 0;");		
		creerFichier(deploy,user);
	}
	
	public static void ReelDeployer(BdServeur bdServeur,Utilisateur user, Serveur serv) throws serveurException
	{
		creerDossier("MariaConf", user);
		creerDossier("MariaConf/env", user);
		creerDossier("MariaConf/init", user);
		
		creerInit("MariaConf/init/initialize.sql", bdServeur.getMysqlPasssword(), user);
		
		for (dockerserveur dserv : bdServeur.getDserveur()) {
			creerEnv("MariaConf/env/node"+ dserv.getNom_docker_serveur() +".env",dserv,user);			
		}
		
		CreerDeployer("MariaConf/deploy.sh",bdServeur, user);
		deploy(user);
		
		//demarage ssh,stat,sauvegarde
		for(dockerserveur doc: bdServeur.getDserveur())
		{
			DockerDASSH.configurerDocker(doc, bdServeur.getMysqlPasssword(), user);			
		}	
		
		//CreerSauvegarde("script-mysql.sh", bdServeur);
	}

	
}
