package mg.orange.automatisation.metier;

import java.util.ArrayList;
import java.util.List;

import mg.orange.automatisation.dassh.SshConnection;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Fichier;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;
import mg.orange.automatisation.exception.sshException;

public class Deployeur {
	private SshConnection sshConnex;
	private List<String> commandes;	
	
	public Deployeur(SshConnection sshConnex) {
		super();
		this.sshConnex = sshConnex;
		commandes = new ArrayList<>();
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

		System.out.print("sauv");
		return true;
	}
	
	/********************************** Teste Reseau 
	 * @throws sshException *********************************************************/
	
	
	
}
