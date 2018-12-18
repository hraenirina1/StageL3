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
		creerDossier("MariaConf/10.2");
		
		creerDockerfile("MariaConf/10.2/Dockerfile");
		creerMariaRepo("MariaConf/10.2/MariaDB.repo");
		creerDockerEntrypoint("MariaConf/10.2/docker-entrypoint.sh");
		
		creerInit("MariaConf/init/initialize.sql");
		
		for (dockerserveur dserv : bdServeur.getDserveur()) {
			creerEnv("MariaConf/env/node"+ dserv.getNom_docker_serveur() +".env",dserv);			
		}
		
		CreerDeployer("MariaConf/deploy.sh",bdServeur);
		
		commandes.add("cd MariaConf && bash deploy.sh");
		commandes.add("rm -Rvf MariaConf");
		sshConnex.ExecuterCommande(commandes);
		commandes.clear();
		
		return true;
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
	private Boolean creerDockerfile(String destination)
	{
		Fichier DockerFile = new Fichier(destination);	
		
		DockerFile.ajouterLigne("FROM centos:7\r\n" + 
				"MAINTAINER Michael J. Stealey <michael.j.stealey@gmail.com>");
		
		//# Explicitly set user/group IDs for mysql account		
		DockerFile.ajouterLigne("RUN groupadd -r mysql --gid=997 \\\r\n" + 
				"    && useradd -r -g mysql -d /var/lib/mysql --uid=997 mysql");
		
		//language variable d'environnement
		DockerFile.ajouterLigne("ENV LANGUAGE=\"en_US.UTF-8\"\r\n" + 
				"ENV LANG=\"en_US.UTF-8\"\r\n" + 
				"ENV LC_ALL=\"en_US.UTF-8\"");
		
		//# install gosu
		DockerFile.ajouterLigne("ENV GOSU_VERSION 1.10\r\n" + 
				"RUN set -x \\\r\n" + 
				"    && yum -y install epel-release \\\r\n" + 
				"    && yum -y install wget dpkg \\\r\n" + 
				"    && dpkgArch=\"\\$(dpkg --print-architecture | awk -F- '{ print \\$NF }')\" \\\r\n" + 
				"    && wget -O /usr/bin/gosu \"https://github.com/tianon/gosu/releases/download/\\$GOSU_VERSION/gosu-\\$dpkgArch\" \\\r\n" + 
				"    && wget -O /tmp/gosu.asc \"https://github.com/tianon/gosu/releases/download/\\$GOSU_VERSION/gosu-\\$dpkgArch.asc\" \\\r\n" + 
				"    && export GNUPGHOME=\"\\$(mktemp -d)\" \\\r\n" + 
				"    && gpg --keyserver ha.pool.sks-keyservers.net --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \\\r\n" + 
				"    && gpg --batch --verify /tmp/gosu.asc /usr/bin/gosu \\\r\n" + 
				"    && rm -r \"\\$GNUPGHOME\" /tmp/gosu.asc \\\r\n" + 
				"    && chmod +x /usr/bin/gosu \\\r\n" + 
				"    && gosu nobody true");
		
		//# install prerequisites for MariaDB
		DockerFile.ajouterLigne("RUN yum install -y \\\r\n" + 
				"    rsync \\\r\n" + 
				"    nmap \\\r\n" + 
				"    lsof \\\r\n" + 
				"    perl-DBI \\\r\n" + 
				"    nc \\\r\n" + 
				"    boost-program-options \\\r\n" + 
				"    iproute \\\r\n" + 
				"    iptables\\\r\n" + 
				"    libaio \\\r\n" + 
				"    libmnl \\\r\n" + 
				"    libnetfilter_conntrack \\\r\n" + 
				"    libnfnetlink \\\r\n" + 
				"    make \\\r\n" + 
				"    openssl \\\r\n" + 
				"    which");
		
		//install mariadb
		DockerFile.ajouterLigne("ADD ./MariaDB.repo /etc/yum.repos.d/MariaDB.repo\r\n" + 
				"RUN yum install -y \\\r\n" + 
				"    MariaDB-server \\\r\n" + 
				"    MariaDB-client \\\r\n" + 
				"    MariaDB-compat \\\r\n" + 
				"    galera \\\r\n" + 
				"    socat \\\r\n" + 
				"    jemalloc\r\n" + 
				"\r\n" + 
				"ENV WSREP_ON=ON\r\n" + 
				"ENV WSREP_PROVIDER=/usr/lib64/galera/libgalera_smm.so\r\n" + 
				"ENV WSREP_PROVIDER_OPTIONS=''\r\n" + 
				"ENV WSREP_CLUSTER_ADDRESS='gcomm://'\r\n" + 
				"ENV WSREP_CLUSTER_NAME='galera'\r\n" + 
				"ENV WSREP_NODE_ADDRESS='localhost'\r\n" + 
				"ENV WSREP_NODE_NAME='galera1'\r\n" + 
				"ENV WSREP_SST_METHOD=rsync\r\n" + 
				"ENV BINLOG_FORMAT=row\r\n" + 
				"ENV DEFAULT_STORAGE_ENGINE=InnoDB\r\n" + 
				"ENV INNODB_AUTOINC_LOCK_MODE=2\r\n" + 
				"ENV BIND_ADDRESS=0.0.0.0\r\n" + 
				"ENV MYSQL_ROOT_PASSWORD=temppassword");
		
		//# add docker-entrypoint script
		DockerFile.ajouterLigne("ADD ./docker-entrypoint.sh /docker-entrypoint.sh");
		
		
		DockerFile.ajouterLigne("VOLUME [\"var/lib/mysql\"]");
		
		DockerFile.ajouterLigne("EXPOSE 3306 4444 4567 4568\r\n" + 
				"ENTRYPOINT [\"/docker-entrypoint.sh\"]");
		
		DockerFile.ajouterLigne("CMD [\"-i\", \"mysqld\"]");
		
		creerFichier(DockerFile);
		
		return true;
	}
	private Boolean creerMariaRepo(String destination)
	{
		Fichier mariaRepo = new Fichier(destination);
		mariaRepo.ajouterLigne("[mariadb]\r\n" + 
				"name = MariaDB\r\n" + 
				"baseurl = http://yum.mariadb.org/10.2/centos7-amd64\r\n" + 
				"gpgkey=https://yum.mariadb.org/RPM-GPG-KEY-MariaDB\r\n" + 
				"gpgcheck=1\r\n" + 
				"");
		
		creerFichier(mariaRepo);
		return true;
	}
	private Boolean creerDockerEntrypoint(String destination)
	{
		Fichier dockerEntrypoint = new Fichier(destination);
		
		dockerEntrypoint.ajouterLigne("#!/usr/bin/env bash\r\n" + 
				"set -e\r\n" + 
				"");
		
		dockerEntrypoint.ajouterLigne("package='docker-entrypoint'\r\n" + 
				"init=false\r\n" + 
				"join=false\r\n" + 
				"usage=false\r\n" + 
				"verbose=false\r\n" + 
				"sqlfile=''\r\n" + 
				"mysqld=false");
		
		dockerEntrypoint.ajouterLigne("_server_cnf() {\r\n" + 
				"    echo \"set /etc/my.cnf.d/server.cnf\"\r\n" + 
				"    > /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"[galera]\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"# Mandatory settings\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"wsrep_on=\\${WSREP_ON}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"wsrep_provider=\\${WSREP_PROVIDER}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    if [[ ! -z \"\\${WSREP_PROVIDER_OPTIONS// }\" ]]; then\r\n" + 
				"        echo \"wsrep_provider_options\"=\\${WSREP_PROVIDER_OPTIONS} >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    fi\r\n" + 
				"    echo \"wsrep_cluster_address=\\${WSREP_CLUSTER_ADDRESS}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"wsrep_cluster_name=\\${WSREP_CLUSTER_NAME}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"wsrep_node_address=\\${WSREP_NODE_ADDRESS}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"wsrep_node_name=\\${WSREP_NODE_NAME}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"wsrep_sst_method=\\${WSREP_SST_METHOD}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"binlog_format=\\${BINLOG_FORMAT}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"default_storage_engine=\\${DEFAULT_STORAGE_ENGINE}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"innodb_autoinc_lock_mode=\\${INNODB_AUTOINC_LOCK_MODE}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"    echo \"bind-address=\\${BIND_ADDRESS}\" >> /etc/my.cnf.d/server.cnf\r\n" + 
				"}");
		
		dockerEntrypoint.ajouterLigne("_mysql_secure_installation() {\r\n" + 
				"    echo \"exec mysql_secure_installation\"\r\n" + 
				"    > /.msi_response\r\n" + 
				"    echo \"\" >> /.msi_response\r\n" + 
				"    echo \"y\" >> /.msi_response\r\n" + 
				"    echo \"\\${MYSQL_ROOT_PASSWORD}\" >> /.msi_response\r\n" + 
				"    echo \"\\${MYSQL_ROOT_PASSWORD}\" >> /.msi_response\r\n" + 
				"    echo \"y\" >> /.msi_response\r\n" + 
				"    echo \"y\" >> /.msi_response\r\n" + 
				"    echo \"y\" >> /.msi_response\r\n" + 
				"    echo \"y\" >> /.msi_response\r\n" + 
				"    mysql_secure_installation < /.msi_response\r\n" + 
				"}\r\n" + 
				"");
		
		dockerEntrypoint.ajouterLigne("_usage() {\r\n" + 
				"    echo \"Docker MariaDB Galera Cluster\"\r\n" + 
				"    echo \" \"\r\n" + 
				"    echo \"\\$package [-hijv] [-f filename.sql] [arguments]\"\r\n" + 
				"    echo \" \"\r\n" + 
				"    echo \"options:\"\r\n" + 
				"    echo \"-h                    show brief help\"\r\n" + 
				"    echo \"-i                    initialized galera cluster\"\r\n" + 
				"    echo \"-j                    join existing galera cluster\"\r\n" + 
				"    echo \"-v                    verbose output\"\r\n" + 
				"    echo \"-f filename.sql       provide SQL script to initialize database with from mounted \\$\r\n" + 
				"    exit 0\r\n" + 
				"}\r\n" + 
				"");
		
		
		dockerEntrypoint.ajouterLigne("while getopts 'hijvf:q' opt; do\r\n" + 
				"  case \"\\${opt}\" in\r\n" + 
				"    h) usage=true ;;\r\n" + 
				"    i) init=true ;;\r\n" + 
				"    j) join=true ;;\r\n" + 
				"    f) sqlfile=\"\\${OPTARG}\" ;;\r\n" + 
				"    v) verbose=true ;;\r\n" + 
				"    ?) echo \"Invalid option provided\" && usage=true ;;\r\n" + 
				"  esac\r\n" + 
				"done");
		
		dockerEntrypoint.ajouterLigne("for var in \"\\$@\"\r\n" + 
				"do\r\n" + 
				"    if [[ \"\\${var}\" = 'mysqld' ]]; then\r\n" + 
				"        mysqld=true\r\n" + 
				"    fi\r\n" + 
				"done\r\n" + 
				"");
		dockerEntrypoint.ajouterLigne("\r\n" + 
				"if \\$mysqld; then\r\n" + 
				"    if \\$usage; then\r\n" + 
				"        _usage\r\n" + 
				"    fi\r\n" + 
				"    gosu root /etc/init.d/mysql start\r\n" + 
				"    _mysql_secure_installation\r\n" + 
				"    if [[ -e /init/\\${sqlfile} ]]; then\r\n" + 
				"        gosu root mysql -uroot -p\\${MYSQL_ROOT_PASSWORD} < /init/\\${sqlfile}\r\n" + 
				"        gosu root mysqldump -uroot -p\\${MYSQL_ROOT_PASSWORD} --all-databases > /init/db.sql\r\n" + 
				"    fi\r\n" + 
				"    gosu root /etc/init.d/mysql stop\r\n" + 
				"    _server_cnf\r\n" + 
				"    if \\$verbose; then\r\n" + 
				"        echo \"\\$ cat /etc/my.cnf.d/server.cnf\"\r\n" + 
				"        cat /etc/my.cnf.d/server.cnf\r\n" + 
				"    fi\r\n" + 
				"    if \\$init; then\r\n" + 
				"        gosu root /etc/init.d/mysql start --wsrep-new-cluster\r\n" + 
				"    fi\r\n" + 
				"    if \\$join; then\r\n" + 
				" gosu root /etc/init.d/mysql start\r\n" + 
				"    fi\r\n" + 
				"    if \\$verbose; then\r\n" + 
				"        echo \"[MySQL]> SHOW VARIABLES LIKE 'wsrep%';\"\r\n" + 
				"        gosu root mysql -uroot -p\\${MYSQL_ROOT_PASSWORD} -e \"SHOW VARIABLES LIKE 'wsrep%';\" \\\r\n" + 
				"        | fold -w 80 -s\r\n" + 
				"        echo \"\\$ ss -lntu\"\r\n" + 
				"        gosu root ss -lntu\r\n" + 
				"    fi\r\n" + 
				"    gosu root tail -f /dev/null\r\n" + 
				"else\r\n" + 
				"    exec \"\\$@\"\r\n" + 
				"fi\r\n" + 
				"");
		
		creerFichier(dockerEntrypoint);
		return true;
	}
	private Boolean creerInit(String destination)
	{
		Fichier init = new Fichier(destination);
		init.ajouterLigne("CREATE DATABASE ICAT CHARACTER SET latin1 COLLATE latin1_general_cs;\r\n" + 
				"CREATE USER 'irods'@'localhost' IDENTIFIED BY 'temppassword';\r\n" + 
				"GRANT ALL ON ICAT.* TO 'irods'@'localhost';\r\n" + 
				"SHOW GRANTS FOR 'irods'@'localhost';");
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
		Fichier deploy = new Fichier(destination);
		deploy.ajouterLigne("#!/usr/bin/env bash\n" + 
				"#HARINIAINA RAMIANDRISOA\n");
		
		deploy.ajouterLigne("REPO_DIR=\\$(pwd)\n");
		deploy.ajouterLigne("GALERANET=\\$(docker network inspect "+ bdserv.getNomReseau() + ")\n");
		deploy.ajouterLigne("if [[ \"\\${GALERANET}\" = '[]' ]]; then\n" + 
							"    docker network create -d overlay --attachable --subnet="+bdserv.getAdresseReseau().toString() +"/" + String.valueOf(bdserv.getMasque()) + " "+ bdserv.getNomReseau() +"\n" + 
							"fi\n");
		
		StringBuilder depl = new StringBuilder();
		depl.append("docker run -d --name "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" -h "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" ");
		depl.append("-v \\${REPO_DIR}/init:/init ");
		depl.append("--env-file=env/node"+ bdserv.getDserveur().get(0).getNom_docker_serveur() +".env ");
		depl.append("--net " + bdserv.getNomReseau() + " ");
		depl.append("--ip "+ bdserv.getDserveur().get(0).getIp_docker().toString()+" ");
							
					for(int i=1 ;i<bdserv.getDserveur().size();i++)
					{
						depl.append("--add-host "+bdserv.getDserveur().get(i).getNom_docker_serveur()+":"+bdserv.getDserveur().get(i).getIp_docker().toString()+ " ");
					}
		
		depl.append("-p 3306 -p 4444 -p 4567 -p 4568 mjstealey/mariadb-galera:10.2 -vif initialize.sql mysqld \n");
		deploy.ajouterLigne(depl.toString());
		
		for(int j=1 ;j<bdserv.getDserveur().size();j++)
		{
			StringBuilder deplo = new StringBuilder();
			deplo.append("docker run -d --name " + bdserv.getDserveur().get(j).getNom_docker_serveur() +" -h "+ bdserv.getDserveur().get(0).getNom_docker_serveur() +" ");
			deplo.append("--env-file=env/node"+bdserv.getDserveur().get(j).getNom_docker_serveur() +".env ");
			deplo.append("--net " +  bdserv.getNomReseau() + " ");
			deplo.append(" --ip "+  bdserv.getDserveur().get(j).getIp_docker().toString() +" ");
		
						for(int i=0 ;i<bdserv.getDserveur().size();i++)
						{
							if(i!=j)		
								{deplo.append("--add-host "+ bdserv.getDserveur().get(i).getNom_docker_serveur() +":"+bdserv.getDserveur().get(i).getIp_docker().toString() +" ");}
						}		
			deplo.append("-p 3306 -p 4444 -p 4567 -p 4568 mjstealey/mariadb-galera:10.2 -vj mysqld");
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
		if(res.getType().equals("local"))
			return sshConnex.ExecuterCommandeVerifRetour("docker network create " + res.getNom_reseau() + " --subnet=" + res.getIp_reseau().toString() + "/" + res.getMasque_reseau());
		else if(res.getType().equals("overlay"))
		{
			return sshConnex.ExecuterCommandeVerifRetour("docker network create --driver=overlay  --attachable " + res.getNom_reseau() + " --subnet=" + res.getIp_reseau().toString() + "/" + res.getMasque_reseau());
		}
		else return -1;	
	}

}
