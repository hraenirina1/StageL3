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
import mg.orange.automatisation.metier.Superviseur;

@Controller
public class MainController {
	@SuppressWarnings("unused")
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
			//connectionssh = SshConnection.CreerConnection(new SshConfig(adr,user,pass,Integer.parseInt(port)));
			
			//recuperer l'adresse ip
			IP ip = IP.IPfromString(adr);
			
			List<IP> listip = ipdao.findByPart1AndPart2AndPart3AndPart4AllIgnoreCase(ip.getPart1(), ip.getPart2(), ip.getPart3(), ip.getPart4());
			
			//reponse au connexion ssh
			session.setAttribute("user", new Utilisateur(user,Integer.parseInt(port), adr,pass));
			
			if(listip.size()==0)
			{
				model.addAttribute("ip", ip);
				return "Ajoutserveur";
			}
			else
			{
				return "home";
			}
				
		}
		catch(Exception e) {//sshException e){
			model.addAttribute("Erreur", " Impossible d'établir la connexion avec le serveur " + adr + ":" + port + " !" + e.getMessage());
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

	//test
	@GetMapping("/test")	
	public String test(HttpSession session,
				@RequestParam("ip")String ip
				)
		{   
			
			
//			if(Superviseur.testMysql(ip))
//			{
//				System.out.println("velona");
//			}
//			else
//				System.out.println("maty");
			
			try {
				connectionssh = SshConnection.CreerConnection(new SshConfig("192.168.237.3","root","123456",22));

			//stat	
				String[] ligne = 
						connectionssh.ExecuterCommandeRecupOutStat("nc "+ip+" 1234").split("\n");
				System.out.println(ligne.length);
				
				/*
				 * 1 - ram total
				 * 2 - ram libre
				 * 3 - ram utiliser
				 * 
				 * 4 - cpu sys
				 * 5 - cpu ni
				 * 6 - cpu libre
				 * 
				 * 0verlay - 
				 * */
				int i = Integer.parseInt(ligne[1]) + Integer.parseInt(ligne[2]);
					System.out.println("RAM : "+ ligne[1] + "/" + i);
					
				Double j = Double.valueOf(ligne[3]) + Double.valueOf(ligne[4]);
				Double k = j + Double.valueOf(ligne[5]);
				
					System.out.println("CPU : "+ j + "/" + k);
					
					System.out.println("Disque : " + ligne[7]);
					
			//	connectionssh.ExecuterCommandeRecupOut("pkill -fx 'nc "+ip+" 1234'");
				
				//sauvegarder
				
			} catch (sshException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return "redirect:/";
		}
	
	@GetMapping("/supervision")	
	public String supervision(HttpSession session,
				Model model)
		{   		
		
			
			return "supervision";
		}

}
