package mg.orange.automatisation.metier;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.ReseauDAO;
import mg.orange.automatisation.dao.ServeurDAO;
import mg.orange.automatisation.dassh.BdServeurDASSH;
import mg.orange.automatisation.dassh.DockerDASSH;
import mg.orange.automatisation.dassh.ReseauDASSH;
import mg.orange.automatisation.dassh.ServeurDASSH;
import mg.orange.automatisation.entities.BDServeur;
import mg.orange.automatisation.entities.DockerServeur;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.PServeur;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.reseauException;
import mg.orange.automatisation.exception.serveurException;



@Service 
public class BDServeurMetier {
	static String SAUVEGARDE_LOCATION = "/srv/mariadb/backup/";
	public static String INTERFACE = "enp0s8";
	
	//dao
	@Autowired
	private ServeurDAO servdao;
	@Autowired
	private ReseauDAO resdao;
	@Autowired
	private BdServeurDAO bdservdao;
	
	//dassh
	@Autowired
	private BdServeurDASSH bdservdassh; 
	@Autowired
	private ServeurDASSH servdassh;
	@Autowired
	private ReseauDASSH reseaudassh;
 	@Autowired
 	private DockerDASSH dockerdassh;
 	
 	public BDServeurMetier() {
	}
	
	public IP GenererIPReseau_interne()
	{
		List<Reseau> rex = resdao.findAll();		
		//si premier reseau
		if(rex.size()==0)
		{
			return new IP(10,10,10,0);		
		}
		else 
		{
			return new IP(10,10,10 + rex.size()/2,0);
		}
	}
	public BDServeur VerifierBdserveur(Utilisateur user,String nom, IP ip, IP ip_reseau, int masquereseau,IP ip_reseau_interne, int masquereseau_interne, Long id_pserveur, String MysqlMDP, String ReseauType) throws serveurException
	{
		servdassh.testerConnexion(user);
		
		PServeur pserveur = servdao.findById(Long.valueOf(id_pserveur)).get();
		servdassh.testeDisponibiliteIP(user, ip);
		
		return new BDServeur(nom,ip,"travail",MysqlMDP,new Reseau(nom+"_reseau", ip_reseau, masquereseau,ReseauType),new Reseau(nom + "_reseau_interne", ip_reseau_interne, masquereseau_interne,ReseauType),pserveur);
	}
	public void deployBDServeur(BDServeur bdserveur,Utilisateur user) throws reseauException, serveurException
	{
		//deployer reseau
		reseaudassh.DeployerReseau(bdserveur.getReseau_interne(), user);
		System.out.println("--- fin reseau -----");
		
		//deployer serveur de base de donnees
		bdservdassh.deployerBDServeur(bdserveur, user);
		System.out.println("--- fin deploy -----");
		
		//configurer les contaigners
		for (DockerServeur doc : bdserveur.getDserveur()){
			dockerdassh.configurerDocker(doc, bdserveur.getMysqlPasssword(), user);
			System.out.println("--- fin "+ doc.getNom() + " -----");
		}
		
		//configurer Haproxy
		bdservdassh.ConfigurerHaproxy(bdserveur, user);
		System.out.println("--- fin ha -----");
		
		//configurer sauvegarde
		bdservdassh.sauvegarde(bdserveur, SAUVEGARDE_LOCATION , bdserveur.getNom()+".sh", user);
		System.out.println("--- fin save -----");
		
		//enregistrement
		bdserveur.setStatus("deploy");
		bdservdao.save(bdserveur);
		
	}
	public void replierBDServeur(Long id,Utilisateur user) throws serveurException, reseauException
	{
		BDServeur bd = RecupererBDServeur(id);
		bdservdassh.retirerBDServeur(bd, user);
		bdservdassh.retirerHaproxy(bd, user);
		bdservdassh.retirerSauvegarde(bd, user);
		
		reseaudassh.SupprimerReseau(bd.getReseau_interne(), user);
		
		bd.setStatus("travail");
		bdservdao.save(bd);
	}
	

	public PServeur recupererPServeur(Long id)
	{
		return servdao.getOne(Long.valueOf(id));
	}
	public List<PServeur> recupererPServeur()
	{
		return servdao.findAll();
	}	
	public List <BDServeur> recupererBDServeurTravail()
	{
		return bdservdao.findByStatus("travail");
	}
	public List <BDServeur> recupererBDServeurDeploy()
	{
		return bdservdao.findByStatus("deploy");
	}
	public List <BDServeur> recupererBDServeur()
	{
		return bdservdao.findAll();
	}
	public BDServeur RecupererBDServeur(Long id)
	{
		return bdservdao.findById(id).get();
	}
	public List <BDServeur> recupererBDServeurTravailServeur(PServeur serv)
	{
		return bdservdao.findByPserveurAndStatus(serv, "travail");
	}
	public List <BDServeur> recupererBDServeurDeployServeur(PServeur serv)
	{
		return bdservdao.findByPserveurAndStatus(serv,"deploy");
	}
	public List <BDServeur> recupererBDServeurServeur(PServeur serv)
	{
		return bdservdao.findByPserveur(serv);
	}
	public void  EnregistrerBDServeur(BDServeur bdserveur)
	{
		bdservdao.save(bdserveur);
	}
	public void  EnregistrerReseau(Reseau reseau)
	{
		resdao.save(reseau);
	}
	public void supprimerBDserveur(BDServeur bdserveur)
	{
		bdservdao.delete(bdserveur);
	}
	
	
}
