package mg.orange.automatisation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IPController {
	
	@GetMapping("/IP")
	public String Index(Model model) {
        return "prerequis";
    }
}
