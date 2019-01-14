package mg.orange.automatisation.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.dao.LogDAO;
import mg.orange.automatisation.dassh.ServeurDASSH;
import mg.orange.automatisation.dassh.SshConnection;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Log;
import mg.orange.automatisation.entities.SshConfig;
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
	private LogDAO log;	
	
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
	@GetMapping("/test")	
	public String test(HttpSession session,
				@RequestParam("ip")String ip
				)
		{   
			return "redirect:/";
		}
	
	//supervision
	@GetMapping("/supervision")	
	public String supervision(HttpSession session,
				Model model) throws sshException
		{   			
			List<BdServeur> bdserveur = bdservdao.findByStatus("deploy");	
			for (BdServeur bdServeur2 : bdserveur) {
				
				SshConnection connectionssh = SshConnection.CreerConnection(new SshConfig("192.168.237.3","root","123456",22));
								
				bdServeur2.setStat(new Stat(Superviseur.testMysql(bdServeur2.getIp_externe().toString()),"-","-","-"));
					
				for (dockerserveur dserv : bdServeur2.getDserveur()) {
					Stat stat = Superviseur.statistique(dserv.getIp_docker().toString(), connectionssh);
					dserv.setStat(new Stat(Superviseur.testMysql(dserv.getIp_docker().toString()),stat.getRAM(),stat.getDisque(),stat.getCPU()));
				}
			}
			
			model.addAttribute("bdserv",bdserveur);
			return "supervision";
		}

}
