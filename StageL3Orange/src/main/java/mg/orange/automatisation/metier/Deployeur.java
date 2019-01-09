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
		
		if(exit_status)
		{
			//demarage ssh,stat,sauvegarde
			for(dockerserveur doc: bdServeur.getDserveur())
			{
				commandes.add("echo /sbin/sshd-keygen > script.sh");
				commandes.add("echo /sbin/sshd >> script.sh");
				
				commandes.add("echo /sbin/crond >> script.sh ");
				commandes.add("echo \"(crontab -l; echo \\\"* * * * * bash /stat.sh\\\") | crontab - \" >> script.sh ");
				
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
		}	
		
		commandes.add("rm -Rvf MariaConf");
		sshConnex.ExecuterCommande(commandes);
		commandes.clear();
		
		//CreerSauvegarde("script-mysql.sh", bdServeur);
		
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
		
		creerFichier(deploy);
		return true;
	}
	
	//creer sauvegarde
	private Boolean CreerSauvegarde(String destination, BdServeur bdserv)
	{
		Fichier sauv = new Fichier(destination);
		sauv.ajouterLigne("#!/bin/bash\r\n" + 
				"\r\n" + 
				"# Sauvegarde de mariadb\r\n" + 
				"#\r\n" + 
				"# Author: Hariniaina Raenirina <rhaenirina08@gmail.com>\r\n" + 
				"# Date: 01/01/2010\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"#####################################################################\r\n" + 
				"# PARAMETRAGE BASH\r\n" + 
				"#####################################################################\r\n" + 
				"\r\n" + 
				"# Arrête le script dès qu'il y a une erreur non interceptée\r\n" + 
				"set -e\r\n" + 
				"# Intercepte les variables non initialisées\r\n" + 
				"set -u\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"#####################################################################\r\n" + 
				"# CONFIGURATION\r\n" + 
				"#####################################################################\r\n" + 
				"\r\n" + 
				"# Répertoire destination (mappé en NFS)\r\n" + 
				"BACKUP_DIR=/srv/backup/mysql\r\n" + 
				"# Fichier de configuration MySQL avec login/pw d'un compte avec les\r\n" + 
				"# droits suffisants pour lancer mysqldump\r\n" + 
				"MY_CONF=/root/.my.cnf\r\n" + 
				"# Durée de rétention des backups\r\n" + 
				"RETENTION=7\r\n" + 
				"# Bases à exclure séparées par des espaces\r\n" + 
				"DB_EXCLUDE=\"information_schema| mysql \"\r\n" + 
				"# Crée ou pas un sous-répertoire par serveur\r\n" + 
				"FOLDER_PER_HOST=1\r\n" + 
				"# Crée ou pas un sous-répertoire par base\r\n" + 
				"FOLDER_PER_BASE=1\r\n" + 
				"# Options mysqldump\r\n" + 
				"MY_OPTS=\"--opt --complete-insert --routines --single-transaction --max_allowed_packet=32M\"\r\n" + 
				"\r\n" + 
				"# Variables qu'il n'est pas nécessaire de modifier\r\n" + 
				"# ------------------------------------------------\r\n" + 
				"\r\n" + 
				"# Date du jour (format YYYYMMDD-HHMM) précision à la minute\r\n" + 
				"DATE=`date +%Y%m%d-%H%M`\r\n" + 
				"# Préfixe des backups\r\n" + 
				"PREFIX=${HOSTNAME%%.*}_mysql_backup\r\n" + 
				"\r\n" + 
				"PROGNAME=mysql-backup\r\n" + 
				"LOCKFILE=/var/tmp/${PROGNAME}.lock\r\n" + 
				"\r\n" + 
				"# Commandes mysql (il ne sera généralement pas nécessaire de les modifier)\r\n" + 
				"# Chemin du client mysql\r\n" + 
				"MYSQL=\"mysql\"\r\n" + 
				"# Chemin de l'outil de dump mysql\r\n" + 
				"MYSQLDUMP=\"mysqldump\"\r\n" + 
				"\r\n" + 
				"# Commande d'écriture dans syslog\r\n" + 
				"# Paramètres d'entrée :\r\n" + 
				"#  - Niveau de syslog (info, warning, error...)\r\n" + 
				"#  - Message à logger\r\n" + 
				"function log() {\r\n" + 
				"  loglevel=$1\r\n" + 
				"  shift\r\n" + 
				"  msg=\"$*\"\r\n" + 
				"\r\n" + 
				"  logger -p user.${loglevel} -t \"${PROGNAME}[$$]\" ${msg}\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"#################################\r\n" + 
				"# Clean old backups\r\n" + 
				"# Arguments :\r\n" + 
				"# - Folder to clean\r\n" + 
				"# - File prefix\r\n" + 
				"# - Retention (days)\r\n" + 
				"#################################\r\n" + 
				"function clean_backups() {\r\n" + 
				"  local dir=$1 prefix=$2 retention=$3\r\n" + 
				"\r\n" + 
				"  # Set backup file's date according to the date in the filename\r\n" + 
				"  find \"${dir}\" -name .snapshot -prune -o -type f -name \"${prefix}*\" -print0 | while read -d $'\\0' file ; do\r\n" + 
				"    # Extract file date as YYYYMMDDhhmm\r\n" + 
				"    file_date=$(echo ${file} | sed -e 's/^.*\\.\\([0-9]\\{8\\}\\)-\\([0-9]\\{4\\}\\).*$/\\1\\2/')\r\n" + 
				"    # and set\r\n" + 
				"    touch -t ${file_date} ${file}\r\n" + 
				"  done\r\n" + 
				"  ## Suppression des dumps plus vieux que la durée de rétention définie dans la configuration\r\n" + 
				"  find \"${dir}\" -type f -name \"${prefix}*\" -mtime +${retention} -exec rm -f \"{}\" \\; 2>/dev/null\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"#####################################################################\r\n" + 
				"# Fonction principale\r\n" + 
				"#####################################################################\r\n" + 
				"\r\n" + 
				"function main() {\r\n" + 
				"  log info \"Début de la sauvegarde MySQL\"\r\n" + 
				"\r\n" + 
				"  ## Interrogation de mysql pour obtenir le nom de chaque base\r\n" + 
				"  databases=`${MYSQL} --defaults-extra-file=${MY_CONF} -Bse \"show databases\"`\r\n" + 
				"  if [ $? -ne 0 ]; then\r\n" + 
				"    # Une erreur s'est produite\r\n" + 
				"    log error \"ERREUR d'accès au serveur MySQL. La requête 'show databases' a échoué.\"\r\n" + 
				"    exit 1\r\n" + 
				"  fi\r\n" + 
				"  \r\n" + 
				"  # Exclut les bases de la liste\r\n" + 
				"  if [ -n \"${DB_EXCLUDE}\" ]; then\r\n" + 
				"      # On construit une expression régulière au format \"sed\" de la liste\r\n" + 
				"      # d'exclusion.\r\n" + 
				"      reg=$(echo ${DB_EXCLUDE} | sed -e 's/ /\\\\\\|/')\r\n" + 
				"      reg=\"\\(${reg}\\)\"\r\n" + 
				"      # puis on nettoie\r\n" + 
				"      databases=$(echo ${databases} | sed -e \"s/${reg}//g\")\r\n" + 
				"  fi\r\n" + 
				"\r\n" + 
				"  ## Pour chacune des bases\r\n" + 
				"  for base in ${databases}\r\n" + 
				"  do\r\n" + 
				"    # Choix du répertoire à utiliser\r\n" + 
				"    DUMP_DIR=${BACKUP_DIR}\r\n" + 
				"    if [ ${FOLDER_PER_HOST} -eq 1 ]; then\r\n" + 
				"        DUMP_DIR=${DUMP_DIR}/${HOSTNAME%%.*}\r\n" + 
				"    fi\r\n" + 
				"    if [ ${FOLDER_PER_BASE} -eq 1 ]; then\r\n" + 
				"        DUMP_DIR=${DUMP_DIR}/${base}\r\n" + 
				"    fi\r\n" + 
				"    mkdir -p ${DUMP_DIR}\r\n" + 
				"    ## On la dumpe\r\n" + 
				"    log info \"Dump de la base ${base}...\"\r\n" + 
				"    ${MYSQLDUMP} --defaults-extra-file=${MY_CONF} ${MY_OPTS} ${base} \\\r\n" + 
				"      | bzip2 -9 > ${DUMP_DIR}/${PREFIX}_${base}.${DATE}.sql.bz2\r\n" + 
				"    if [ $? -ne 0 ]; then\r\n" + 
				"      log error \"ERREUR lors du dump de la base ${base}.\"\r\n" + 
				"    fi\r\n" + 
				"  done\r\n" + 
				"  \r\n" + 
				"  # Clean old backups for this server\r\n" + 
				"  clean_backups ${BACKUP_DIR} ${PREFIX} ${RETENTION}\r\n" + 
				"\r\n" + 
				"  log info \"Fin de la sauvegarde MySQL\"\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"#####################################################################\r\n" + 
				"# Point d'entrée du programme et gestion de verrou pour éviter le lancement multiple\r\n" + 
				"#####################################################################\r\n" + 
				"# C'est ici qu'on fait tout le nettoyage (fichiers temporaires par exemple).\r\n" + 
				"if ( set -o noclobber; echo \"$$\" > \"$LOCKFILE\") 2> /dev/null; \r\n" + 
				"then\r\n" + 
				"   trap 'rm -f \"$LOCKFILE\"; exit $?' INT TERM EXIT\r\n" + 
				"\r\n" + 
				"   # Appelle la fonction principale\r\n" + 
				"   main\r\n" + 
				"   \r\n" + 
				"   rm -f \"$LOCKFILE\"\r\n" + 
				"   trap - INT TERM EXIT\r\n" + 
				"   exit 0\r\n" + 
				"else\r\n" + 
				"   (\r\n" + 
				"     echo \"Failed to acquire lockfile: $LOCKFILE.\" \r\n" + 
				"     echo \"Held by following process:\"\r\n" + 
				"     ps -p $(cat $LOCKFILE) uww\r\n" + 
				"   ) | log error\r\n" + 
				"   exit 1\r\n" + 
				"fi\r\n" + 
				"\r\n" + 
				"");
		creerFichier(sauv);
		System.out.print("sauv");
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
