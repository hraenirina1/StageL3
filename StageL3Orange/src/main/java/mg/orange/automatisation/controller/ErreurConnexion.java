package mg.orange.automatisation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErreurConnexion {
	
	@RequestMapping(value="errors")
	public String Erreur() {
		return "errorPage"; 
	}

}
