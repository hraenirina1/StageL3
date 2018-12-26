package mg.orange.automatisation.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.exception.sshException;
import mg.orange.automatisation.metier.SshConnection;

@Controller
public class MainController {
	private SshConnection connectionssh; 
	@Autowired
	private IPDAO ipdao; 
	
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
		try {
			
			//essai de connexion ssh
			connectionssh = SshConnection.CreerConnection(new SshConfig(adr,user,pass,Integer.parseInt(port)));
			
		}
		catch(sshException e){
			model.addAttribute("Erreur", " Impossible d'établir la connexion avec le serveur " + adr + ":" + port + " !" + e.getMessage());
			return "index";
		}
		
		//recuperer l'adresse ip
		IP ip = IP.IPfromString(adr);
		System.out.println(ip.getPart1().toString() + ip.getPart2().toString() + ip.getPart3().toString() + ip.getPart4().toString());
		
		//List<IP> listip = ipdao.findByPart1AndPart2AndPart3AndPart4AllIgnoreCase(ip.getPart1(), ip.getPart2(), ip.getPart3(), ip.getPart4());
		
		//reponse au connexion ssh
		session.setAttribute("user", new Utilisateur(user,Integer.parseInt(port), adr));
		
//		if(listip.size()==0)
//		{
//			model.addAttribute("ip", ip);
//			return "Ajoutserveur";
//		}
//		else
//		{
			return "home";
//		}
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
