package mg.orange.automatisation.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import mg.orange.automatisation.metier.ReseauMetier;

@Controller
public class ReseauController {
	
	@Autowired
	private ReseauMetier reseau;
	
	@GetMapping(value="/reseauList")
	public String ListReseau(HttpSession session,Model model)
	{
		
		if(session.getAttribute("user")==null) return "redirect:/";		
		if(session.getAttribute("Erreur")!=null) model.addAttribute("Erreur", (String) session.getAttribute("Erreur"));
		session.removeAttribute("Erreur");
		model.addAttribute("reseau", reseau.listeReseau());
		
		return "PageReseau";
	}
}
