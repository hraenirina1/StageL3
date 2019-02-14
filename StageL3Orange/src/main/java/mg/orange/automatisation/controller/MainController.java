package mg.orange.automatisation.controller;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.orange.automatisation.metier.DockerMetier;
import mg.orange.automatisation.metier.ServeurMetier;
import mg.orange.automatisation.entities.BDServeur;
import mg.orange.automatisation.entities.DockerServeur;
import mg.orange.automatisation.entities.IP;

import mg.orange.automatisation.entities.Sauvegarde;
import mg.orange.automatisation.entities.Stat;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.serveurException;
import mg.orange.automatisation.exception.sshException;

@Controller
public class MainController {

	@Autowired
	ServeurMetier serv;
	@Autowired
	DockerMetier dock;
	
	//page de connexion
	@GetMapping({"/","/index","/connexion"})
	public String Index(HttpSession session,Model model)
	{
		if(session.getAttribute("Erreur")!=null) model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
		if(session.getAttribute("user")==null)
			return "PageConnexion";
		else {
				model.addAttribute("page", "main");
			return "PagePrincipale";
		}
			
	}
	
	//page de connexion
		@PostMapping({"/test"})
		public String test(HttpSession session,
				@RequestParam MultiValueMap<String, String> parametre_multiple,
				Model model)
		{
			
			List<String> ip_part1 = parametre_multiple.get("ip1");
			List<String> ip_part2 = parametre_multiple.get("ip2");
			List<String> ip_part3 = parametre_multiple.get("ip3");
			List<String> ip_part4 = parametre_multiple.get("ip4");
			
			int i = 0;
			
			List<IP> ip = new ArrayList<>();
			
			for (String string: ip_part1) {
				ip.add(new IP(Integer.parseInt(string),Integer.parseInt(ip_part2.get(i)),Integer.parseInt(ip_part3.get(i)),Integer.parseInt(ip_part4.get(i))));
				i++;
			}
			
			for (IP ip2 : ip) {
				System.out.println(ip2);
			}
			
			return "PageConnexion";
				
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
			
				Utilisateur utilisateur = new Utilisateur(user,Integer.parseInt(port), adr,pass);
				serv.testerConnection(utilisateur);
			
				//recuperer l'adresse ip
				IP ip = IP.IPfromString(adr);
			
			
				//reponse au connexion ssh
				session.setAttribute("user",utilisateur);
			
				if(serv.testerIPSiExiste(ip))
				{
					//log.save(new Log(user, "Info", "connexion au nouveau serveur "+ adr, false, new Date()));
					model.addAttribute("ip", ip);
					return "Ajoutserveur";
				}
				else
				{
					//log.save(new Log(user, "Info", "connexion au serveur "+ adr, false, new Date()));
					return "PagePrincipale";
				}

		}
		catch(serveurException e){
			model.addAttribute("Erreur", "Impossible d'établir la connexion avec le serveur " + adr + ":" + port + " !" + e.getMessage());
			return "PageConnexion";
		}		
	}
	
	//deconnexion
	@GetMapping("/deconnexion")	
	public String Deconnection(HttpSession session)
	{	
		//log.save(new Log(user.getUser(), "Info", "connexion au nouveau serveur "+ user.getAdresse(), false, new Date()));
		session.invalidate();
		return "redirect:/";
	}
//
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
			
			DockerServeur doc = dock.recuperer(Long.valueOf(adr));			
			
			Stat stat;
				
				stat = dock.statistique(doc, user);

				rep.add(Double.valueOf(stat.getCPU()));
				rep.add(Double.valueOf(100) - Double.valueOf(stat.getCPU()));
				
				
				rep.add(Double.valueOf(doc.getRam()) * Double.valueOf(stat.getRAM()) / 100);
				rep.add(Double.valueOf(doc.getRam()));				
				

				rep.add(Double.valueOf(stat.getDisque().replaceAll("kB", "")));
				rep.add(Double.valueOf(100) * Double.valueOf(stat.getDisque().replaceAll("kB", "")));
				
				if(stat.getMysql())
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
			
			List<BDServeur> bdserveur;
			
			try {
				bdserveur = serv.supervision(user);
				model.addAttribute("bdserv",bdserveur);
			} catch (serveurException e) {				
				e.printStackTrace();
			}
			
			
			return "supervision";
		}
	@GetMapping("/detail")	
	public String supervisionUn(HttpSession session,
			@RequestParam("serv")String adr,			
			Model model) throws sshException
	{
		
		if(session.getAttribute("user")==null) return "redirect:/";				
		
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
				
				List<Sauvegarde> listSave;
				
				try {
					listSave = serv.listeSauvegarde(user);
					model.addAttribute("save", listSave);
				} catch (serveurException e) {
					e.printStackTrace();
				}
				
				return "PageSauvegarde";
			}

}
