//package mg.orange.automatisation.controller;
//
//import javax.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import mg.orange.automatisation.dao.BdServeurDAO;
//import mg.orange.automatisation.dao.ReseauDAO;
//import mg.orange.automatisation.dassh.ReseauDASSH;
//import mg.orange.automatisation.entities.Reseau;
//
//import mg.orange.automatisation.entities.Utilisateur;
//import mg.orange.automatisation.exception.reseauException;
//import mg.orange.automatisation.exception.serveurException;
//
//@Controller
//public class ReseauController {
//	
//	@Autowired
//	private ReseauDAO reseau;
//	@Autowired
//	private BdServeurDAO bdserv;
//	
//	@GetMapping(value="/reseauList")
//	public String ListReseau(HttpSession session,Model model)
//	{
//		
//		if(session.getAttribute("user")==null) return "redirect:/";		
//		if(session.getAttribute("Erreur")!=null) model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
//		
//		session.removeAttribute("Erreur");
//		model.addAttribute("reseau", reseau.findAll());
//		
//		return "listReseau";
//	}
//	@GetMapping("/reseauDelete")
//	public String reseauDelete(HttpSession session,
//			@RequestParam(value="reseau")String id_reseau,
//			Model model)
//	{
//		if(session.getAttribute("user")==null) return "redirect:/";
//		Utilisateur user = (Utilisateur) session.getAttribute("user");
//		
//		//recuperer le reseau 
//		Reseau res = reseau.findById(Long.valueOf(id_reseau)).get();
//		
//		try {						
//			
//				if(bdserv.findByReseau(res).size()==0)
//				{
//					//effacer du serveur
//					ReseauDASSH.SupprimerReseau(res,user);
//					
//					//effacer de la bd
//					reseau.delete(res);		
//				}
//				else
//				{
//					session.setAttribute("Erreur","reseau non vide");
//				}
//
//		} catch (serveurException | reseauException e) {
//			session.setAttribute("Erreur", e.getMessage());
//		}			
//		
//		return "redirect:/reseauList";
//	}
//}
