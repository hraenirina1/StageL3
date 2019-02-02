//package mg.orange.automatisation.controller;
//
//import java.util.Date;
//import java.util.List;
//
//import javax.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import mg.orange.automatisation.dao.IPDAO;
//import mg.orange.automatisation.dao.LogDAO;
//import mg.orange.automatisation.dao.ServeurDAO;
//import mg.orange.automatisation.dassh.ServeurDASSH;
//import mg.orange.automatisation.entities.IP;
//import mg.orange.automatisation.entities.Log;
//import mg.orange.automatisation.entities.PServeur;
//import mg.orange.automatisation.entities.Utilisateur;
//import mg.orange.automatisation.exception.serveurException;
//
//@Controller
//public class ServeurController {
//	@Autowired
//	private ServeurDAO serveur;
//	@Autowired
//	private IPDAO ip;
//	@Autowired
//	private LogDAO log;
//	
//	@GetMapping("/serveurAjout")
//	public String ajoutGet(HttpSession session)
//	{
//		return "redirect:/";				
//	}
//	@RequestMapping(value="/serveurAjout",method=RequestMethod.POST)
//	public String ajoutPost(HttpSession session,
//			@RequestParam("nomserveur")String nom,
//			@RequestParam("ipserveur1")String ip1,
//			@RequestParam("ipserveur2")String ip2,
//			@RequestParam("ipserveur3")String ip3,
//			@RequestParam("ipserveur4")String ip4,
//			Model model) {
//		
//		if(session.getAttribute("user")==null) return "redirect:/";			
//		Utilisateur user = (Utilisateur) session.getAttribute("user");
//		
//		if(!nom.isEmpty() && !ip1.isEmpty() && !ip2.isEmpty() && !ip3.isEmpty() && !ip4.isEmpty())
//		{
//			if((Integer.parseInt(ip1)>=0 && Integer.parseInt(ip1)<=255)
//				&&(Integer.parseInt(ip2)>=0 && Integer.parseInt(ip2)<=255)
//				&&(Integer.parseInt(ip3)>=0 && Integer.parseInt(ip3)<=255)
//				&&(Integer.parseInt(ip4)>=0 && Integer.parseInt(ip4)<=255)){
//				
//				IP ip_serveur = new IP(Integer.parseInt(ip1),Integer.parseInt(ip2), Integer.parseInt(ip3), Integer.parseInt(ip4));
//				try {
//						ServeurDASSH.testerDocker(user);
//						ServeurDASSH.testSshConnexion(user);
//						ServeurDASSH.testMysqlConnexion(user);
//						
//						ip.save(ip_serveur);
//						serveur.save(new PServeur(nom, ip_serveur));							
//						log.save(new Log(user.getUser(),"info","Ajout d'un nouveau serveur" + user.getAdresse(),false,new Date()));
//						
//						return "redirect:serveurList";					
//				} catch (serveurException e) {
//					session.removeAttribute("user");
//					session.setAttribute("Erreur", e.getMessage());
//				}
//			}
//		}
//		
//		return "redirect:/";
//	}
//	
//	@GetMapping("/serveurList")
//	public String list(HttpSession session,Model model) {
//		
//		if(session.getAttribute("user")==null) return "redirect:/";
//		
//		List<PServeur> listserveur = serveur.findAll();
//		model.addAttribute("listServeur", listserveur);	
//		model.addAttribute("page", "listServeur");
//		return "listServeur";
//	}
//	
//}
