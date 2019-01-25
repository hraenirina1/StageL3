package mg.orange.automatisation.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.DockerServeurDAO;
import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.dao.LogDAO;
import mg.orange.automatisation.dao.ServeurDAO;
import mg.orange.automatisation.dassh.DockerDASSH;
import mg.orange.automatisation.dassh.ServeurDASSH;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Log;
import mg.orange.automatisation.entities.Sauvegarde;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.Stat;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.entities.dockerserveur;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;
import mg.orange.automatisation.metier.Superviseur;

@Controller
public class MainController {

	//DAO
	@Autowired
	private IPDAO ipdao; 
	@Autowired
	private BdServeurDAO bdservdao;
	@Autowired
	private ServeurDAO servdao;
	@Autowired
	private DockerServeurDAO dockdao;
	@Autowired
	private LogDAO log;	
	
	//page de connexion
	@GetMapping({"/","/index"})
	public String Index(HttpSession session,Model model)
	{
		if(session.getAttribute("Erreur")!=null) model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
		if(session.getAttribute("user")==null)
			return "index";
		else {
				model.addAttribute("page", "main");
			return "home";
		}
			
	}
	
	//controlleur de connexion
	@PostMapping(value="/connexion")
	public String connexionPost(HttpSession session,
			@RequestParam("serv_adr")String adr,
			@RequestParam("serv_user")String user,
			@RequestParam("serv_pass")String pass,
			@RequestParam("serv_port")String port,Model model) 
					{		
		try {
			
			//essai de connexion ssh
			ServeurDASSH.testerServeur(new Utilisateur(user,Integer.parseInt(port), adr,pass));
			
			//recuperer l'adresse ip
			IP ip = IP.IPfromString(adr);
			
			List<IP> listip = ipdao.findByPart1AndPart2AndPart3AndPart4AllIgnoreCase(ip.getPart1(), ip.getPart2(), ip.getPart3(), ip.getPart4());
			
			//reponse au connexion ssh
			session.setAttribute("user", new Utilisateur(user,Integer.parseInt(port), adr,pass));
			
			if(listip.size()==0)
			{
				log.save(new Log(user, "Info", "connexion au nouveau serveur "+ adr, false, new Date()));
				model.addAttribute("ip", ip);
				return "Ajoutserveur";
			}
			else
			{
				log.save(new Log(user, "Info", "connexion au serveur "+ adr, false, new Date()));
				return "home";
			}
				
		}
		catch(serveurException e){
			model.addAttribute("Erreur", "Impossible d'établir la connexion avec le serveur " + adr + ":" + port + " !" + e.getMessage());
			return "index";
		}		
	}
	@GetMapping("/connexion")
	public String connexionGet(HttpSession session){
			return "redirect:/";
	}
	
	//deconnexion
	@GetMapping("/deconnexion")	
	public String Deconnection(HttpSession session)
	{
		if(session.getAttribute("user")==null) return "redirect:/";				
		Utilisateur user = (Utilisateur) session.getAttribute("user");		
		log.save(new Log(user.getUser(), "Info", "connexion au nouveau serveur "+ user.getAdresse(), false, new Date()));
		
		session.invalidate();
		return "redirect:/";
	}

	//test
	@GetMapping(value = "/test", produces = "application/json")	
	public @ResponseBody List<Double> test(HttpSession session,
		   @RequestParam("serv")String adr			
				) throws serveurException
		{  
			List<Double> rep = new ArrayList<>();
		try {
			
						
			if(session.getAttribute("user")==null) return rep;				
			Utilisateur user = (Utilisateur) session.getAttribute("user");	
			
			dockerserveur doc = dockdao.getOne(Long.valueOf(adr));			
			
			Stat stat;
				
				stat = DockerDASSH.statistique(doc.getIp_docker().toString(), user);

				rep.add(Double.valueOf(stat.getCPU()));
				rep.add(Double.valueOf(100) - Double.valueOf(stat.getCPU()));
				
				System.out.println(stat.getRAM());
				
				rep.add(Double.valueOf(doc.getRam()) * 1024 - Double.valueOf(stat.getRAM()));
				rep.add(Double.valueOf(stat.getRAM()));				
				
				rep.add(Double.valueOf(stat.getDisque().replaceAll("%", "")));
				rep.add(Double.valueOf(100)-Double.valueOf(stat.getDisque().replaceAll("%", "")));
				
				if(Superviseur.testMysql(doc.getIp_docker().toString()))
				{
					rep.add(Double.valueOf("0"));
				}
				else
				{
					rep.add(Double.valueOf("1"));
				}        
				
				
			} catch (serveurException e) {
			}
		catch(Exception e) {			
			e.printStackTrace();
		}
		return rep;
				
			
		}
	
	//supervision
	@GetMapping("/supervision")	
	public String supervision(HttpSession session,
				Model model) throws sshException
		{
			if(session.getAttribute("user")==null) return "redirect:/";				
			Utilisateur user = (Utilisateur) session.getAttribute("user");
			
			List<BdServeur> bdserveur = bdservdao.findByStatus("deploy");	
			for (BdServeur bdServeur2 : bdserveur) {
								
				bdServeur2.setStat(new Stat(Superviseur.testMysql(bdServeur2.getIp_externe().toString()),"-","-","-"));
					
				for (dockerserveur dserv : bdServeur2.getDserveur()) {
					
					Stat stat;
					try {
						
						stat = DockerDASSH.statistique(dserv.getIp_docker().toString(), user);
						dserv.setStat(new Stat(Superviseur.testMysql(dserv.getIp_docker().toString()),stat.getRAM(),stat.getDisque(),stat.getCPU()));
					
					} catch (serveurException e) {
						model.addAttribute("Erreur", e.getMessage());
					}					
				}
			}
			
			model.addAttribute("bdserv",bdserveur);
			return "supervision";
		}
	@GetMapping("/detail")	
	public String supervisionUn(HttpSession session,
			@RequestParam("serv")String adr,			
			Model model) throws sshException
	{
		
		if(session.getAttribute("user")==null) return "redirect:/";				
		Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		model.addAttribute("dock", adr);
		return "supervisionDocker";
	}
	
	//supervision
	@GetMapping("/sauvegarde")	
	public String sauvegarde(HttpSession session,
					Model model) throws sshException
			{
				if(session.getAttribute("user")==null) return "redirect:/";				
				Utilisateur user = (Utilisateur) session.getAttribute("user");
				
				List<Serveur> serveur = servdao.findAll();				
				List<Sauvegarde> listSave = new ArrayList<>();
				
				for (Serveur Serveur2 : serveur) {

						try {							
								Sauvegarde save = new Sauvegarde();
								save.setServ(Serveur2);
								
								String[] list = ServeurDASSH.listeSauvegarde(user, Serveur2);
								save.setSauvegarde(list);
								
								listSave.add(save);					
						} catch (serveurException e) {
							model.addAttribute("Erreur", e.getMessage());
						}			
				}
				model.addAttribute("save", listSave);
				return "sauvegarde";
			}

}
