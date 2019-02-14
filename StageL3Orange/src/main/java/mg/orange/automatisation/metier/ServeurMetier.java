package mg.orange.automatisation.metier;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.dao.ServeurDAO;
import mg.orange.automatisation.dassh.DockerDASSH;
import mg.orange.automatisation.dassh.ServeurDASSH;
import mg.orange.automatisation.entities.BDServeur;
import mg.orange.automatisation.entities.DockerServeur;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.PServeur;
import mg.orange.automatisation.entities.Sauvegarde;
import mg.orange.automatisation.entities.Stat;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.serveurException;

@Service
public class ServeurMetier {
	
	//DASSH
	@Autowired
	private ServeurDASSH serveurdassh;
	@Autowired
	private DockerDASSH dockerservdassh;
	
	//DAO
	@Autowired
	private IPDAO ipdao;
	@Autowired
	private ServeurDAO servdao;
	@Autowired
	private BdServeurDAO bdservdao;
	
	
	public ServeurMetier() {
	}
	
	public void testerConnection(Utilisateur utilisateur) throws serveurException {
		serveurdassh.testerConnexion(utilisateur);
	}
	public boolean testerIPSiExiste(IP ip)
	{
		return ipdao.findByPart1AndPart2AndPart3AndPart4AllIgnoreCase(ip.getPart1(), ip.getPart2(), ip.getPart3(), ip.getPart4()).size()==0;
	}
	
	public void AjouterPserveur(PServeur pserveur, Utilisateur user) throws serveurException 
	{
		serveurdassh.testerDocker(user);
		serveurdassh.testSshConnexion(user);
		serveurdassh.testMysqlConnexion(user);
		
		try
		{
			servdao.save(pserveur);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new serveurException("Impossible d'ajouter Serveur physique");
		}
		
	}
	public void SupprimerPServeur(Long id) throws serveurException
	{
		try
		{
			PServeur pserveur = servdao.findById(id).get();
			
			//si non vide
			if(bdservdao.findByPserveur(pserveur).size()!=0)
			throw new serveurException("Serveur physque non vide");	
				
			servdao.delete(pserveur);	
		}
		catch(Exception e)
		{
			throw new serveurException("Serveur Physique introuvable");
		}
		 
		
			
	}
	public List<Sauvegarde> listeSauvegarde(Utilisateur user) throws serveurException
	{
		List<PServeur> serveur = servdao.findAll();				
		List<Sauvegarde> listSave = new ArrayList<>();
		
		for (PServeur Serveur2 : serveur) {							
						Sauvegarde save = new Sauvegarde();
						save.setServ(Serveur2);
						
						String[] list = serveurdassh.listeSauvegarde(user, Serveur2);
						save.setSauvegarde(list);
						
						listSave.add(save);							
		}
		
		return listSave;
	}
	
	public List<BDServeur> supervision(Utilisateur user) throws serveurException
	{
		List<BDServeur> bdserveur = bdservdao.findByStatus("deploy");
		
		for (BDServeur bdServeur2 : bdserveur) {
							
			bdServeur2.setStat(new Stat(Superviseur.testMysql(bdServeur2.getIp().toString()),"-","-","-"));
				
			for (DockerServeur dserv : bdServeur2.getDserveur()) {
				
				Stat stat;
					
					stat = dockerservdassh.statistique(dserv, user);
					dserv.setStat(new Stat(Superviseur.testMysql(dserv.getIp().toString()),stat.getRAM(),stat.getDisque(),stat.getCPU()));				
			}
		}
		
		return bdserveur;
	}
	public List<PServeur> recupererPServeurList()
	{
		return servdao.findAll();
	}
	
	}
