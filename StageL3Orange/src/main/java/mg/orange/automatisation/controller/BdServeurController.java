package mg.orange.automatisation.controller;

import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.metier.BDServeurMetier;
import mg.orange.automatisation.entities.BDServeur;

import mg.orange.automatisation.dao.ConfigDAO;

import mg.orange.automatisation.entities.Config;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.reseauException;
import mg.orange.automatisation.exception.serveurException;

@Controller
public class BdServeurController {
	@Autowired
	private BDServeurMetier bdserv;
	@Autowired
	private ConfigDAO config;
//	private LogDAO log;
	
	@GetMapping("/bdServeurList")

	public String bdserveurList(HttpSession session,
			@RequestParam(value="serv",required=false)String id_serveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";		
		//	Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		//log.save(new Log(user.getUser(),"info","list",false,new Date()));
		
		//liste pour un serveur
		if(id_serveur!=null && !id_serveur.isEmpty())
		{			
			List<BDServeur> bdserveur = bdserv.recupererBDServeurServeur(bdserv.recupererPServeur(Long.valueOf(id_serveur)));		
			model.addAttribute("BdServeurList", bdserveur);
		}
		else
		{
			List<BDServeur> bdserveur = bdserv.recupererBDServeur();		
			model.addAttribute("BdServeurList", bdserveur);
		}
			model.addAttribute("page", "listBdServeur");
		
			if(session.getAttribute("Erreur")!=null)
			{
				model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
				session.removeAttribute("Erreur");
			}		
		return "PageBDServeur";
	}
	@GetMapping("/bdServeurDeployer")
	public String bdserveurListDeploy(HttpSession session,
			@RequestParam(value="serv",required=false)String id_serveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";

		if(id_serveur!=null && !id_serveur.isEmpty())
		{
			List<BDServeur> bdserveur = bdserv.recupererBDServeurDeployServeur(bdserv.recupererPServeur(Long.valueOf(id_serveur)));		
			model.addAttribute("BdServeurList", bdserveur);
		}
		else
		{
			List<BDServeur> bdserveur = bdserv.recupererBDServeurDeploy();		
			model.addAttribute("BdServeurList", bdserveur);
		}
		
			if(session.getAttribute("Erreur")!=null)
			{
				model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
				session.removeAttribute("Erreur");
			}
		
		return "PageBDServeur";
	}
	@GetMapping("/bdServeurEncours")
	public String bdserveurListTravail(HttpSession session,
			@RequestParam(value="serv",required=false)String id_serveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		if(id_serveur!=null && !id_serveur.isEmpty())
		{
			List<BDServeur> bdserveur = bdserv.recupererBDServeurTravailServeur(bdserv.recupererPServeur(Long.valueOf(id_serveur)));		
			model.addAttribute("BdServeurList", bdserveur);
		}
		else
		{
			List<BDServeur> bdserveur = bdserv.recupererBDServeurTravail();		
			model.addAttribute("BdServeurList", bdserveur);
		}
		
		if(session.getAttribute("Erreur")!=null) 
		{	
				model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));		
				session.removeAttribute("Erreur");
		}
		return "PageBDServeur";
	}
		
	@GetMapping("/bdServeurAjout")
	public String AjouterBDServeurGet(HttpSession session,Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		model.addAttribute("serveur", bdserv.recupererPServeur());
		
		if(session.getAttribute("Erreur")!=null) 
		{
			model.addAttribute("Erreur", session.getAttribute("Erreur"));
			session.removeAttribute("Erreur");
		}
		
		return "PageAjoutBDServeur";
	}
	@RequestMapping(value="/BdServeurAjout",method=RequestMethod.POST)
	public String AjouterBDServeurPost(HttpSession session,
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
			//ip virtuel & plage d'adresse & plage d'addresse interne
			IP ipdbserv = new IP(Integer.parseInt(ip1),Integer.parseInt(ip2),Integer.parseInt(ip3),Integer.parseInt(ip4));
			IP addbserv = new IP(Integer.parseInt(ad1),Integer.parseInt(ad2),Integer.parseInt(ad3),Integer.parseInt(ad4));		
			IP addbserv_interne = bdserv.GenererIPReseau_interne();
			
			
			//creation bdserveur
			BDServeur bdserveur = bdserv.VerifierBdserveur(user,nom,ipdbserv,addbserv,Integer.parseInt(masque),addbserv_interne,24,Long.valueOf(serveur),mysqlpassword,reseauType);

			//enregistrer reseau dans la base de donnees
			bdserv.EnregistrerBDServeur(bdserveur);
			
			//log.save(new Log(user.getUser(),"info","Creation du reseau" + res.getNom_reseau() +" "+ res.getIp_reseau().toString() + " sur le serveur "+ user.getAdresse() ,false,new Date()));
			return "redirect:/bdServeurEncours";
		}
		catch(serveurException e)
		{
			session.setAttribute("Erreur", e.getMessage());
			return "redirect:/bdServeurAjout";
		}
		 catch (Exception e) {
			session.setAttribute("Erreur", "BDD error" + e.getMessage());
			e.printStackTrace();
			return "redirect:/bdServeurAjout";
		}
		
		
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
					BDServeur bdserveur = bdserv.RecupererBDServeur(Long.valueOf(id_bdserveur));					
					
					//commande de deploiement 
					bdserv.deployBDServeur(bdserveur, user);
				}
			} catch (serveurException | reseauException e) {
				session.setAttribute("Erreur", e.getMessage());

				try {
					bdserv.replierBDServeur(Long.valueOf(id_bdserveur), user);
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (serveurException e1) {
					e1.printStackTrace();
				} catch (reseauException e1) {
					e1.printStackTrace();
				}
			}
		
		return "redirect:/bdServeurList";
	}
	
	@GetMapping("/bdServeurReplie")
	public String ReplierBDServeu(HttpSession session,
			@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";		
		Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		try {
				if(id_bdserveur!=null && !id_bdserveur.isEmpty())
				{									
					//commande de deploiement 
					bdserv.replierBDServeur(Long.valueOf(id_bdserveur),user);
				}
			} catch (serveurException | reseauException e) {
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
			
			// teste de la presence de bdserveur
			if(id_bdserveur!=null && !id_bdserveur.isEmpty())
			{
				BDServeur bdserveur = bdserv.RecupererBDServeur(Long.valueOf(id_bdserveur));
				
				bdserv.supprimerBDserveur(bdserveur);
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
