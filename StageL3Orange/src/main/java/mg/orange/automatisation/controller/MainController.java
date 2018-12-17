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
	@GetMapping("/")
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
		
		//connectionssh = SshConnection.CreerConnection(new SshConfig(adr,user,pass,Integer.parseInt(port)));
		
		session.setAttribute("user", new Utilisateur(user,Integer.parseInt(port), adr));
		return "home";
		
//		if(connectionssh != null) 
//		{
//			session.setAttribute("user", new Utilisateur(user,Integer.parseInt(port), adr));
//			return "home";
//		}
//		else
//		{
//			model.addAttribute("Erreur", "Impossible de se connecter !!");
//			return "index";
//		}	
		
	}
	@GetMapping("/connexion")
	public String connexionGet(HttpSession session){
		return "home";
//		if(session.getAttribute("user")==null)
//			return "index";
//		else
//			return "home";
	}
	
	//deconnexion
	@GetMapping("/deconnexion")	
	public String Deconnection(HttpSession session)
	{
		session.invalidate();
		return "index";
	}
	
	/* --------------------------------------------------------*/
	
	//page Principale
	@GetMapping("/home")	
	public String Principale(HttpSession session)
	{
		if(session.getAttribute("user")==null)
			return "index";
		else
			return "home";
	}
	
	
	
	
}
