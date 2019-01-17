package mg.orange.automatisation.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.ConfigDAO;
import mg.orange.automatisation.dao.DockerConfigDAO;
import mg.orange.automatisation.dao.DockerServeurDAO;
import mg.orange.automatisation.dao.LogDAO;
import mg.orange.automatisation.dao.ReseauDAO;
import mg.orange.automatisation.dao.ServeurDAO;
import mg.orange.automatisation.dassh.BdServeurDASSH;
import mg.orange.automatisation.dassh.ReseauDASSH;
import mg.orange.automatisation.dassh.ServeurDASSH;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Config;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Log;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;
import mg.orange.automatisation.exception.reseauException;
import mg.orange.automatisation.exception.serveurException;

@Controller
public class BdServeurController {
	
	@Autowired
	private BdServeurDAO bdserv;
	@Autowired 
	private ServeurDAO serv;
	@Autowired
	private ReseauDAO reseau;
	@Autowired
	private ConfigDAO config;
	@Autowired 
	private DockerServeurDAO dockerserv;
	@Autowired 
	private DockerConfigDAO dconf;
	@Autowired
	private LogDAO log;
	
	@GetMapping("/bdServeurList")
	public String bdserveurList(HttpSession session,
			@RequestParam(value="serv",required=false)String id_serveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		if(session.getAttribute("Erreur")!=null) model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
		
		session.removeAttribute("Erreur");
		
		Utilisateur user = (Utilisateur) session.getAttribute("user");		
		log.save(new Log(user.getUser(),"info","list",false,new Date()));
		
		//liste pour un serveur
		if(id_serveur!=null && !id_serveur.isEmpty())
		{			
			List<BdServeur> bdserveur = bdserv.findByServeur(serv.getOne(Long.valueOf(id_serveur)));		
			model.addAttribute("BdServeurList", bdserveur);
		}
		else
		{
			List<BdServeur> bdserveur = bdserv.findAll();		
			model.addAttribute("BdServeurList", bdserveur);
		}
		model.addAttribute("page", "listBdServeur");
		return "bdServeurList";
	}
	@GetMapping("/bdServeurDeployer")
	public String bdserveurListDeploy(HttpSession session,
			@RequestParam(value="serv",required=false)String id_serveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		if(session.getAttribute("Erreur")!=null) model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
		
		session.removeAttribute("Erreur");
		
		if(id_serveur!=null && !id_serveur.isEmpty())
		{
			List<BdServeur> bdserveur = bdserv.findByServeurAndStatus(serv.getOne(Long.valueOf(id_serveur)),"deploy");		
			model.addAttribute("BdServeurList", bdserveur);
		}
		else
		{
			List<BdServeur> bdserveur = bdserv.findByStatus("deploy");		
			model.addAttribute("BdServeurList", bdserveur);
		}
		
		
		return "bdServeurList";
	}
	@GetMapping("/bdServeurEncours")
	public String bdserveurListTravail(HttpSession session,
			@RequestParam(value="serv",required=false)String id_serveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		if(session.getAttribute("Erreur")!=null) model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
		
		session.removeAttribute("Erreur");
		
		if(id_serveur!=null && !id_serveur.isEmpty())
		{
			List<BdServeur> bdserveur = bdserv.findByServeurAndStatus(serv.getOne(Long.valueOf(id_serveur)),"travail");		
			model.addAttribute("BdServeurList", bdserveur);
		}
		else
		{
			List<BdServeur> bdserveur = bdserv.findByStatus("travail");		
			model.addAttribute("BdServeurList", bdserveur);
		}
				
		return "bdServeurList";
	}
		
	@GetMapping("/bdServeurAjout")
	public String bdserveurAjoutGet(HttpSession session,Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
			model.addAttribute("reseau", reseau.findAll());
			model.addAttribute("serveur", serv.findAll());
		
		return "AjoutBdServeur";
	}
	@RequestMapping(value="/BdServeurAjout",method=RequestMethod.POST)
	public String bdserveurAjoutPost(HttpSession session,
			@RequestParam("nombdserveur")String nom,
			@RequestParam("ipbdserveur1")String ip1,
			@RequestParam("ipbdserveur2")String ip2,
			@RequestParam("ipbdserveur3")String ip3,
			@RequestParam("ipbdserveur4")String ip4,
			@RequestParam("adServ1")String ad1,
			@RequestParam("adServ2")String ad2,
			@RequestParam("adServ3")String ad3,
			@RequestParam("adServ4")String ad4,
			@RequestParam("masque")String masque,
			@RequestParam("idserveur")String serveur,
			@RequestParam("reseauType")String reseauType,			
			@RequestParam("mysqlPassword")String mysqlpassword,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		try {
			
			//conversion en adresse ip
			//ip virtuel & plage d'adresse
			IP ipdbserv = new IP(Integer.parseInt(ip1),Integer.parseInt(ip2),Integer.parseInt(ip3),Integer.parseInt(ip4));
			IP addbserv = new IP(Integer.parseInt(ad1),Integer.parseInt(ad2),Integer.parseInt(ad3),Integer.parseInt(ad4));		
				
			//serveur		
			Serveur pserveur = serv.findById(Long.valueOf(serveur)).get();
			
			//tester connexion
			ServeurDASSH.testerServeur(user);
			
			//liste reseau
			List<Reseau> rex = reseau.findAll();
			
			Reseau res = new Reseau();
			
			//si premier reseau
			if(rex.size()==0)
			{
				res = new Reseau(nom,new IP(10,11,10,0),24,pserveur,reseauType);				
			}
			else 
			{
				res = new Reseau(nom,new IP(10,10,rex.get(rex.size()-1).getIp_reseau().getPart3()+1,0),24,pserveur,reseauType);
			}
			
			//deployer reseau
			ReseauDASSH.DeployerReseau(res, user, pserveur);
			log.save(new Log(user.getUser(),"info","Creation du reseau" + res.getNom_reseau() +" "+ res.getIp_reseau().toString() + " sur le serveur "+ user.getAdresse() ,false,new Date()));
			
			//enregistrer reseau dans la base de donnees
			bdserv.save(new BdServeur(nom,Integer.parseInt(masque),ipdbserv,addbserv,"travail",pserveur,mysqlpassword,res));	
			
		}
		catch(serveurException e)
		{
			System.out.println("Le serveur est inaccessible");
			session.setAttribute("Erreur", "Impossible d'établir la connexion avec le serveur " + user.getAdresse() + ":" + user.getPort() + " !" + e.getMessage());
		
		} catch (reseauException e) {
			
			System.out.println(e);
			session.setAttribute("Erreur", e.getMessage());
		}
		 catch (Exception e) {
				
				System.out.println(e.getMessage());
				session.setAttribute("Erreur", "BDD error" + e.getMessage());
				
		}
		
		return "redirect:/bdServeurEncours";
	}
	@GetMapping("/bdServeurConfig")
	public String bdserveurConfig(HttpSession session,
	@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		model.addAttribute("bdserv", id_bdserveur);
		model.addAttribute("ListDockerServeur", dockerserv.findBybdserveur(bdserv.getOne(Long.valueOf(id_bdserveur))));
		
		return "dockerList";
	}
	
	@GetMapping("/bdServeurDeploy")
	public String bdServeurDeploy(HttpSession session,
			@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";		
		Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		try {
				if(id_bdserveur!=null && !id_bdserveur.isEmpty())
				{					
					BdServeur bdserveur = bdserv.findById(Long.valueOf(id_bdserveur)).get();					
					
					//commande de deploiement 
					BdServeurDASSH.ReelDeployer(bdserveur,user,bdserveur.getServeur());
					
					//changement de status
					bdserveur.setStatus("deploy");
					bdserv.save(bdserveur);
				
				}
		} catch (serveurException e) {
			session.setAttribute("Erreur", e.getMessage());
		}
		
		return "redirect:/bdServeurList";
	}
	@GetMapping("/bdServeurDelete")
	public String bdServeurDelete(HttpSession session,
			@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		try
		{
			
			// teste de la presence de bdserveur
			if(id_bdserveur!=null && !id_bdserveur.isEmpty())
			{
				BdServeur bdserveur = bdserv.findById(Long.valueOf(id_bdserveur)).get();
				
				//chercher les contaigners affilier
				//si vide supprimer
				if(dockerserv.findBybdserveur(bdserveur).size()==0) {
					bdserv.delete(bdserveur);
				}
				else
				{
					for (dockerserveur dserv_element : dockerserv.findBybdserveur(bdserveur)) {
						dockerserv.delete(dserv_element);
							for (dockerConfig conf : dserv_element.getDockerConfig()) {
								dconf.delete(conf);
							}   
						ServeurDASSH.RetirerEnPlaceIp(dserv_element.getIp_docker(),"enp0s8", user);
					}
						bdserv.delete(bdserveur);
				}									
			}
		}
		catch (serveurException e) {
			
			session.setAttribute("Erreur", e.getMessage());
		}
		
		return "redirect:/bdServeurList";
	}
	@GetMapping("/conf")
	public String conf(HttpSession session)
	{
		config.save(new Config("Base", "WSREP_ON"));
		config.save(new Config("Base", "WSREP_PROVIDER"));
		config.save(new Config("Base", "WSREP_PROVIDER_OPTIONS"));
		config.save(new Config("Base", "WSREP_CLUSTER_ADDRESS"));
		config.save(new Config("Base", "WSREP_CLUSTER_NAME"));
		config.save(new Config("Base", "WSREP_NODE_ADDRESS"));
		config.save(new Config("Base", "WSREP_NODE_NAME"));
		config.save(new Config("Base", "BINLOG_FORMAT"));
		config.save(new Config("Base", "DEFAULT_STORAGE_ENGINE"));
		config.save(new Config("Base", "INNODB_AUTOINC_LOCK_MODE"));
		config.save(new Config("Base", "BIND_ADDRESS"));
		config.save(new Config("Base", "MYSQL_ROOT_PASSWORD"));
		
		return "redirect:/bdServeurList";
	}

}
