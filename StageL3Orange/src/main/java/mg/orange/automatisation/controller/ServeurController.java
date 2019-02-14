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

import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.PServeur;
//import mg.orange.automatisation.entities.Log;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.metier.ServeurMetier;
import mg.orange.automatisation.exception.serveurException;

@Controller
public class ServeurController {
	@Autowired
	private ServeurMetier serv;

//	private LogDAO log;

	@GetMapping("/serveurAjout")
	public String ajoutGet(HttpSession session)
	{
		return "redirect:/";				
	}
	@RequestMapping(value="/serveurAjout",method=RequestMethod.POST)
	public String ajoutPost(HttpSession session,
			@RequestParam("nomserveur")String nom,
			@RequestParam("ipserveur1")String ip1,
			@RequestParam("ipserveur2")String ip2,
			@RequestParam("ipserveur3")String ip3,
			@RequestParam("ipserveur4")String ip4,
			Model model) {
		
		if(session.getAttribute("user")==null) return "redirect:/";			
		Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		if(!nom.isEmpty() && !ip1.isEmpty() && !ip2.isEmpty() && !ip3.isEmpty() && !ip4.isEmpty())
		{
			if((Integer.parseInt(ip1)>=0 && Integer.parseInt(ip1)<=255)
				&&(Integer.parseInt(ip2)>=0 && Integer.parseInt(ip2)<=255)
				&&(Integer.parseInt(ip3)>=0 && Integer.parseInt(ip3)<=255)
				&&(Integer.parseInt(ip4)>=0 && Integer.parseInt(ip4)<=255)){
				
				IP ip_serveur = new IP(Integer.parseInt(ip1),Integer.parseInt(ip2), Integer.parseInt(ip3), Integer.parseInt(ip4));
				try {
						serv.AjouterPserveur(new PServeur(nom,ip_serveur), user);
//						log.save(new Log(user.getUser(),"info","Ajout d'un nouveau serveur" + user.getAdresse(),false,new Date()));
						
						return "redirect:serveurList";					
				} catch (serveurException e) {
					session.removeAttribute("user");
					session.setAttribute("Erreur", e.getMessage());
				}
			}
		}		
		return "redirect:/";
	}
	
	@GetMapping("/serveurList")
	public String list(HttpSession session,Model model) {
		
		if(session.getAttribute("user")==null) return "redirect:/";
		
		List<PServeur> listserveur = serv.recupererPServeurList();
		model.addAttribute("listServeur", listserveur);	
		model.addAttribute("page", "listServeur");
		
		if(session.getAttribute("Erreur")!=null) 
		{
			model.addAttribute("Erreur", session.getAttribute("Erreur"));
			session.removeAttribute("Erreur");
		}
		
		return "PageServeur";
	}
	
	@GetMapping("/ServeurDelete")
	public String supprimer(HttpSession session,
			@RequestParam("serv")String id,
			Model model) {
		
		if(session.getAttribute("user")==null) return "redirect:/";
		
		try {			
			serv.SupprimerPServeur(Long.valueOf(id));	
			
		} 
		catch(NumberFormatException e)
		{
			session.setAttribute("Erreur","Mauvais format d'Identifiant");
		}
		catch (serveurException e) {			
			session.setAttribute("Erreur", e.getMessage());
		}		
		
		return "redirect:serveurList";
	}	
}
