package mg.orange.automatisation.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.metier.SshConnection;

@Controller
public class MainController {
	private SshConnection connectionssh; 
	
	//page de connexion
	@GetMapping({"/","/index"})
	public String Index(HttpSession session)
	{
		if(session.getAttribute("user")==null)
			return "index";
		else
			return "home";
	}
	
	//controlleur de connexion
	@PostMapping(value="/connexion")
	public String connexionPost(HttpSession session,
			@RequestParam("serv_adr")String adr,
			@RequestParam("serv_user")String user,
			@RequestParam("serv_pass")String pass,
			@RequestParam("serv_port")String port,Model model) 
					{		
		//essai de connexion ssh
		connectionssh = SshConnection.CreerConnection(new SshConfig(adr,user,pass,Integer.parseInt(port)));
		
		//reponse au connexion ssh
		if(connectionssh != null) {
			session.setAttribute("user", new Utilisateur(user,Integer.parseInt(port), adr));
			return "home";
		}
		else{
			model.addAttribute("Erreur", " Impossible d'établir la connexion avec le serveur " + adr + ":" + port + " !");
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
		session.invalidate();
		return "redirect:/";
	}
}
