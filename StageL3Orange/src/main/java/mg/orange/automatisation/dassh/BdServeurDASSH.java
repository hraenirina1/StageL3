//package mg.orange.automatisation.dassh;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import mg.orange.automatisation.entities.BDServeur;
//import mg.orange.automatisation.entities.Fichier;
//import mg.orange.automatisation.entities.PServeur;
//import mg.orange.automatisation.entities.SshConfig;
//import mg.orange.automatisation.entities.Utilisateur;
//import mg.orange.automatisation.entities.DockerCon;
//import mg.orange.automatisation.entities.DockerServeur;
//import mg.orange.automatisation.exception.serveurException;
//import mg.orange.automatisation.exception.sshException;
//
//public class BdServeurDASSH {
//
//	public BdServeurDASSH() {
//	}
//	
//	public static void testerServeur(Utilisateur user) throws serveurException {
//		try {
//				SshConnection.CreerConnection(new SshConfig(user));	
//				
//		} catch (sshException e) {
//				throw new serveurException(e.getMessage());
//		}
//	}
//	
//	//****** creer dossier et fichier
//	private static void creerDossier(String nomDossier, Utilisateur user) throws serveurException
//	{
//		try {
//				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
//				List<String> commandes = new ArrayList<>();
//			
//				commandes.add("mkdir \""+ nomDossier +"\";");
//				sshConnex.ExecuterCommande(commandes);
//			
//			} catch (sshException e) {
//					throw new serveurException(e.getMessage());
//			}
//	}
//	private static void creerFichier(Fichier fich, Utilisateur user) throws serveurException
//	{ 
//		try {
//				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
//				List<String> commandes = new ArrayList<>();
//		
//				commandes.add("echo \""+ fich.toString() +"\" > " + fich.getNomFichier() + ";");
//				sshConnex.ExecuterCommande(commandes);
//		} catch (sshException e) {
//				throw new serveurException(e.getMessage());
//		}
//	}
//	
//	private static String CommandeCreerFichierDockerHaproxy(BDServeur bd)
//	{ 
//		Fichier haproxy = new Fichier("ha.cfg");
//		haproxy.ajouterLigne("global\r\n" + 
//				"        log /dev/log local0\r\n" + 
//				"        log /dev/log local1 notice\r\n" + 
//				"\r\n" + 
//				"        user haproxy\r\n" + 
//				"        group haproxy\r\n" + 
//				"        daemon\r\n" + 
//				"\r\n" + 
//				"defaults\r\n" + 
//				"        log     global\r\n" + 
//				"        mode    http\r\n" + 
//				"        option  dontlognull\r\n" + 
//				"        contimeout 5000\r\n" + 
//				"        clitimeout 50000\r\n" + 
//				"        srvtimeout 50000\r\n" + 
//				"        errorfile 400 /etc/haproxy/errors/400.http\r\n" + 
//				"        errorfile 403 /etc/haproxy/errors/403.http\r\n" + 
//				"        errorfile 408 /etc/haproxy/errors/408.http\r\n" + 
//				"        errorfile 500 /etc/haproxy/errors/500.http\r\n" + 
//				"        errorfile 502 /etc/haproxy/errors/502.http\r\n" + 
//				"        errorfile 503 /etc/haproxy/errors/503.http\r\n" + 
//				"        errorfile 504 /etc/haproxy/errors/504.http\r\n" + 
//				"");
//		
//		haproxy.ajouterLigne("listen cluster_db \r\n" + 
//				"                bind 0.0.0.0:3306\r\n" + 
//				"");
//		haproxy.ajouterLigne("mode tcp");
//		haproxy.ajouterLigne("option mysql-check user haproxy");
//		haproxy.ajouterLigne("balance roundrobin");
//		
//		for (DockerServeur doc: bd.getDserveur()) {
//			haproxy.ajouterLigne("server "+ doc.getNom_docker_serveur() +" "+doc.getIp_docker()+":3306 check");
//		}
//		
//		haproxy.ajouterLigne("listen stats \r\n" + 
//				"                bind 0.0.0.0:80\r\n" + 
//				"        stats enable\r\n" + 
//				"        stats hide-version\r\n" + 
//				"        stats refresh 30s\r\n" + 
//				"        stats show-node\r\n" + 
//				"        stats auth castiel:h0809s12192n1\r\n" + 
//				"        stats uri /stats");		
//		
//		return "echo \""+ haproxy.toString() +"\" > " + haproxy.getNomFichier();
//	}
//	
//	//supprimer conf
//	private static void supprimerConf(Utilisateur user) throws serveurException
//	{ 
//		try {
//				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
//				List<String> commandes = new ArrayList<>();
//		
//				commandes.add("rm -Rvf MariaConf");
//				sshConnex.ExecuterCommande(commandes);
//		
//		} catch (sshException e) {
//				throw new serveurException(e.getMessage());
//		}
//	}
//	
//	//******* deploy commande
//	private static void deploy(Utilisateur user) throws serveurException {
//		try {
//				SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
//				sshConnex.ExecuterCommandeVerifRetour("cd MariaConf && bash deploy.sh");
//				
//		} catch (sshException e) {
//				throw new serveurException(e.getMessage());
//		}
//	}
//	
//	//***** deployer ficher config
//	private static void creerInit(String destination,String mdp, Utilisateur user) throws serveurException
//	{
//		Fichier init = new Fichier(destination);
//		init.ajouterLigne("CREATE USER 'su_root'@'%' IDENTIFIED BY '"+ mdp +"';\r\n" + 
//				"GRANT ALL ON *.* TO 'su_root'@'%';\r\n");
//		init.ajouterLigne("CREATE USER 'haproxy'@'%'");
//		
//		creerFichier(init,user);
//	}
//
//	//creation des fichiers d'environnemt
//	private static void creerEnv(String destination, DockerServeur dserv, Utilisateur user ) throws serveurException {			
//			Fichier env = new Fichier(destination);
//			
//			for (DockerCon dconf : dserv.getDockerConfig()) {
//				env.ajouterLigne(dconf.getConfigDocker().getMot_cle() + "=" + dconf.getValeur());
//			}		
//				
//			creerFichier(env,user);
//		}
//	
//	////creation du fichier de deploiement
//	private static void CreerDeployer(String destination, BDServeur bdserv, Utilisateur user) throws serveurException
//	{
//		
//		Fichier deploy = new Fichier(destination);
//		deploy.ajouterLigne("#!/usr/bin/env bash\n" + 
//				"#HARINIAINA RAMIANDRISOA\n");
//		
//		deploy.ajouterLigne("REPO_DIR=\\$(pwd)\n");
//		deploy.ajouterLigne("GALERANET=\\$(docker network inspect "+ bdserv.getNomBdServeur() + ")\n");
//		deploy.ajouterLigne("if [[ \"\\${GALERANET}\" != '[]' ]]; then \n");
//		
//		StringBuilder depl = new StringBuilder();
//		depl.append("docker run -d --memory="+bdserv.getDserveur().get(0).getRam()+"m --cpus=" + bdserv.getDserveur().get(0).getCpu() + " --name "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" -h "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" ");
//		depl.append("-v \\${REPO_DIR}/init:/init ");
//		depl.append("--env-file=env/node"+ bdserv.getDserveur().get(0).getNom_docker_serveur() +".env ");
//		depl.append("--net " + bdserv.getNomBdServeur() + " ");
//		depl.append("--ip "+ bdserv.getDserveur().get(0).getIp_interne().toString()+" ");
//							
//					for(int i=1 ;i<bdserv.getDserveur().size();i++)
//					{
//						depl.append("--add-host "+bdserv.getDserveur().get(i).getNom_docker_serveur()+":"+bdserv.getDserveur().get(i).getIp_interne().toString()+ " ");
//					}
//		
//		depl.append("-p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":22:22 -p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":1234:1234 -p "+bdserv.getDserveur().get(0).getIp_docker().toString()+":3306:3306 -p 4444 -p 4567 -p 4568 hraenirina1/mariadb_galera_stage -vif initialize.sql mysqld \n");
//		deploy.ajouterLigne(depl.toString());
//		
//		for(int j=1 ;j<bdserv.getDserveur().size();j++)
//		{
//			StringBuilder deplo = new StringBuilder();
//			deplo.append("docker run -d --memory="+ bdserv.getDserveur().get(j).getRam()+"m --cpus=" + bdserv.getDserveur().get(j).getCpu() + " --name " + bdserv.getDserveur().get(j).getNom_docker_serveur() +" -h "+ bdserv.getDserveur().get(j).getNom_docker_serveur() +" ");
//			deplo.append("--env-file=env/node"+bdserv.getDserveur().get(j).getNom_docker_serveur() +".env ");
//			deplo.append("--net " +  bdserv.getNomBdServeur() + " ");
//			deplo.append(" --ip "+  bdserv.getDserveur().get(j).getIp_interne().toString() +" ");
//		
//						for(int i=0 ;i<bdserv.getDserveur().size();i++)
//						{
//							if(i!=j)		
//								{deplo.append("--add-host "+ bdserv.getDserveur().get(i).getNom_docker_serveur() +":"+bdserv.getDserveur().get(i).getIp_interne().toString() +" ");}
//						}		
//			deplo.append("-p "+bdserv.getDserveur().get(j).getIp_docker().toString()+":22:22 -p "+bdserv.getDserveur().get(j).getIp_docker().toString()+ ":1234:1234 -p "+bdserv.getDserveur().get(j).getIp_docker().toString()+":3306:3306 -p 4444 -p 4567 -p 4568 hraenirina1/mariadb_galera_stage -vj mysqld");
//			
//			deploy.ajouterLigne(deplo.toString());
//		}	
//		
//		deploy.ajouterLigne("docker run -d -it --name haproxy"+ bdserv.getNomBdServeur()+ " -p "+ bdserv.getIp_externe()+ ":3306:3306 -p "+ bdserv.getIp_externe()+ ":8888:80 hraenirina1/haproxy /bin/bash");
//		
//		deploy.ajouterLigne("exit 0;");
//		deploy.ajouterLigne("fi");
//		deploy.ajouterLigne("exit 1;");		
//		creerFichier(deploy,user);
//	}
//	
//	////sauvegarde
//	public static void sauvegarde(BDServeur bdServeur, String Script_destination,String script,Utilisateur user) throws serveurException
//	{
//		
//		String[] doc = Script_destination.split("/");
//		if(doc.length>=2)
//		{
//			String dos = "";
//			for (String string : doc) {
//				dos += string + "/";
//				creerDossier(dos, user);
//			}
//			
//		}	
//		
//		Fichier save = new Fichier(Script_destination+script);
//		save.ajouterLigne("set -e");
//		save.ajouterLigne("set -u");
//		save.ajouterLigne("IPSERV=\\$1");
//		save.ajouterLigne("USER_MYSQL=\\$2");
//		save.ajouterLigne("PASS_MYSQL=\\$3");
//		save.ajouterLigne("BACKUP_DIR=/srv/backup/mariadb");
//		save.ajouterLigne("RETENTION=7");
//		save.ajouterLigne("DB_EXCLUDE=\"information_schema mysql performance_schema\"");
//		save.ajouterLigne("FOLDER_PER_HOST=0");
//		save.ajouterLigne("FOLDER_PER_BASE=0");
//		save.ajouterLigne("MY_OPTS=\"--opt --complete-insert --routines --single-transaction --max_allowed_packet=32M\"");
//		save.ajouterLigne("DATE=\\`date +%Y%m%d-%H%M\\`");
//		save.ajouterLigne("PREFIX=\\${IPSERV}_mysql_backup");
//		save.ajouterLigne("PROGNAME=mysql-backup");
//		save.ajouterLigne("LOCKFILE=/var/tmp/\\${PROGNAME}"+bdServeur.getNomBdServeur()+".lock");
//		save.ajouterLigne("MYSQL=\"mysql -h \\${IPSERV} -u \\${USER_MYSQL} -p\\${PASS_MYSQL}\"");
//		save.ajouterLigne("MYSQLDUMP=\"mysqldump -h \\${IPSERV} -u \\${USER_MYSQL} -p\\${PASS_MYSQL}\"");
//		save.ajouterLigne("function log() {");
//		save.ajouterLigne("loglevel=\\$1");
//		save.ajouterLigne("shift");
//		save.ajouterLigne("msg=\"\\$*\"");
//		save.ajouterLigne("logger -p user.\\${loglevel} -t \"\\${PROGNAME}[\\$\\$]\" \\${msg}");
//		save.ajouterLigne("}");
//		save.ajouterLigne("function clean_backups() {");
//		save.ajouterLigne("local dir=\\$1 prefix=\\$2 retention=\\$3");
//		save.ajouterLigne("find \"\\${dir}\" -name .snapshot -prune -o -type f -name \"\\${prefix}*\" -print0 | while read -d \\$'\\0' file ; do");
//		save.ajouterLigne("file_date=\\$(echo \\${file} | sed -e 's/^.*\\.\\([0-9]\\{8\\}\\)-\\([0-9]\\{4\\}\\).*\\$/\\1\\2/')");
//		save.ajouterLigne("touch -t \\${file_date} \\${file}");
//		save.ajouterLigne("done");
//		save.ajouterLigne("find \"\\${dir}\" -type f -name \"\\${prefix}*\" -mtime +\\${retention} -exec rm -f \"{}\" \\; 2>/dev/null");
//		save.ajouterLigne("}");
//		save.ajouterLigne("function main() {");
//		save.ajouterLigne("log info \"Début de la sauvegarde MySQL\"");
//		save.ajouterLigne("databases=\\`\\${MYSQL} -Bse \"show databases\"\\`");
//		save.ajouterLigne("if [ \\$? -ne 0 ]; then");
//		save.ajouterLigne("log error \"ERREUR d'accès au serveur MySQL. La requête 'show databases' a échoué.\"");
//		save.ajouterLigne("exit 1");
//		save.ajouterLigne("fi");
//		save.ajouterLigne("if [ -n \"\\${DB_EXCLUDE}\" ]; then");
//		save.ajouterLigne("reg=\\$(echo \\${DB_EXCLUDE} | sed -e 's/ /\\\\\\\\|/')");
//		save.ajouterLigne("reg=\"\\(\\${reg}\\)\"");
//		save.ajouterLigne("databases=\\$(echo \\${databases} | sed -e \"s/\\${reg}//g\")");
//		save.ajouterLigne("fi");
//		save.ajouterLigne("for base in \\${databases}");
//		save.ajouterLigne("do");
//		save.ajouterLigne("DUMP_DIR=\\${BACKUP_DIR}");
//		save.ajouterLigne("if [ \\${FOLDER_PER_HOST} -eq 1 ]; then");
//		save.ajouterLigne("DUMP_DIR=\\${DUMP_DIR}/\\${HOSTNAME%%.*}");
//		save.ajouterLigne("fi");
//		save.ajouterLigne("if [ \\${FOLDER_PER_BASE} -eq 1 ]; then");
//		save.ajouterLigne("DUMP_DIR=\\${DUMP_DIR}/\\${base}");
//		save.ajouterLigne("fi");
//		save.ajouterLigne("mkdir -p \\${DUMP_DIR}");
//		save.ajouterLigne("log info \"Dump de la base \\${base}...\"");
//		save.ajouterLigne("\\${MYSQLDUMP} \\${MY_OPTS} \\${base} \\");
//		save.ajouterLigne("| bzip2 -9 > \\${DUMP_DIR}/\\${PREFIX}_\\${base}.\\${DATE}.sql.bz2");
//		save.ajouterLigne("if [ \\$? -ne 0 ]; then");
//		save.ajouterLigne("log error \"ERREUR lors du dump de la base \\${base}.\"");
//		save.ajouterLigne("fi");
//		save.ajouterLigne("done");
//		save.ajouterLigne("clean_backups \\${BACKUP_DIR} \\${PREFIX} \\${RETENTION}");
//		save.ajouterLigne("log info \"Fin de la sauvegarde MySQL\"");
//		save.ajouterLigne("}");
//		save.ajouterLigne("if ( set -o noclobber; echo \"\\$\\$\" > \"\\$LOCKFILE\") 2> /dev/null;");
//		save.ajouterLigne("then");
//		save.ajouterLigne("trap 'rm -f \"\\$LOCKFILE\"; exit \\$?' INT TERM EXIT");
//		save.ajouterLigne("main");
//		save.ajouterLigne("rm -f \"\\$LOCKFILE\"");
//		save.ajouterLigne("trap - INT TERM EXIT");
//		save.ajouterLigne("exit 0");
//		save.ajouterLigne("else");
//		save.ajouterLigne("(");
//		save.ajouterLigne("echo \"Failed to acquire lockfile: \\$LOCKFILE.\"");
//		save.ajouterLigne("echo \"Held by following process:\"");
//		save.ajouterLigne("ps -p \\$(cat \\$LOCKFILE) uww");
//		save.ajouterLigne(") | log error");
//		save.ajouterLigne("exit 1");
//		save.ajouterLigne("fi");
//		
//		creerFichier(save,user);
//		
//		sauvegarderdocker(bdServeur, Script_destination,script, user);
//	}
//	
//	////sauvegarde docker 
//	private static void sauvegarderdocker(BDServeur bdServeur, String Script_destination, String script,Utilisateur user) throws serveurException
//	{
//		try {
//			SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));	
//					sshConnex.ExecuterCommandeVerifRetour("(crontab -l; echo \"* * * * * bash "+ Script_destination+script +" "+ bdServeur.getIp_externe() +" su_root "+ bdServeur.getMysqlPasssword() + " \") | crontab -");		
//		} catch (sshException e) {
//				throw new serveurException(e.getMessage());
//		}
//	}
//	
//	public static void ReelDeployer(BDServeur bdServeur,Utilisateur user, PServeur serv) throws serveurException
//	{
//		creerDossier("MariaConf", user);
//		creerDossier("MariaConf/env", user);
//		creerDossier("MariaConf/init", user);
//		
//		creerInit("MariaConf/init/initialize.sql", bdServeur.getMysqlPasssword(), user);
//		
//		for (DockerServeur dserv : bdServeur.getDserveur()) {
//			creerEnv("MariaConf/env/node"+ dserv.getNom_docker_serveur() +".env",dserv,user);			
//		}
//		
//		CreerDeployer("MariaConf/deploy.sh",bdServeur, user);
//		deploy(user);
//		
//		//demarage ssh,stat,sauvegarde
//		for(DockerServeur doc: bdServeur.getDserveur())
//		{
//			DockerDASSH.configurerDocker(doc, bdServeur.getMysqlPasssword(), user);			
//		}
//			
//		configurerBd(bdServeur, user);
//		System.out.println("----");
//		supprimerConf(user);
//		System.out.println("----");
//		sauvegarde(bdServeur,"/srv/mariadb/backup/",bdServeur.getNomBdServeur()+".sh", user);		
//	}
//
//public static void configurerBd(BDServeur bd, Utilisateur user) throws serveurException {
//	try {
//		
//			SshConnection sshConnex = SshConnection.CreerConnection(new SshConfig(user));
//			List<String> commandes = new ArrayList<>();
//			
//			commandes.add(CommandeCreerFichierDockerHaproxy(bd));
//			commandes.add("docker cp ha.cfg haproxy"+ bd.getNomBdServeur()+ ":/etc/haproxy/haproxy.cfg" );
//			
//			//lancement
//			commandes.add("docker exec haproxy"+ bd.getNomBdServeur()+ " service haproxy restart");
//			sshConnex.ExecuterCommande(commandes);
//		
//} catch (sshException e) {
//		throw new serveurException(e.getMessage());
//}
//}
//}
