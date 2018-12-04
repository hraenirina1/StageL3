package mg.orange.automatisation.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.dao.ServeurDAO;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Serveur;

@Controller
public class BdServeurController {
	
	@Autowired
	private BdServeurDAO bdserv;
	@Autowired 
	private ServeurDAO serv;
	@Autowired
	private IPDAO ip;
	
	
	@GetMapping("/bdServeurList")
	public String bdserveurList(HttpSession session,
			@RequestParam(value="serv",required=false)String id_serveur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		if(id_serveur!=null && !id_serveur.isEmpty())
		{
			List<BdServeur> bdserveur = bdserv.findByServeur(serv.getOne(Long.valueOf(id_serveur)));		
			model.addAttribute("BdServeurList", bdserveur);
		}
		else
		{
			List<BdServeur> bdserveur = bdserv.findAll();		
			model.addAttribute("BdServeurList", bdserveur);
		}
		
		
		return "bdServeurList";
	}
	@GetMapping("/bdServeurAjout")
	public String bdserveurAjout(HttpSession session,Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		model.addAttribute("serveur", serv.findAll());
		System.out.println(serv.count());
		return "AjoutBdServeur";
	}
	@RequestMapping(value="/BdServeurAjout",method=RequestMethod.POST)
	public String bdserveurAjoutPost(HttpSession session,
			@RequestParam("nombdserveur")String nom,
			@RequestParam("ipbdserveur1")String ip1,
			@RequestParam("ipbdserveur2")String ip2,
			@RequestParam("ipbdserveur3")String ip3,
			@RequestParam("ipbdserveur4")String ip4,
			@RequestParam("idserveur")String serveur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		IP ipdbserv = new IP(Integer.parseInt(ip1),Integer.parseInt(ip2),Integer.parseInt(ip3),Integer.parseInt(ip4));
		ip.save(ipdbserv);
		
		Serveur pserveur = serv.findById(Long.valueOf(serveur)).get();
		bdserv.save(new BdServeur(nom,ipdbserv,pserveur));	
		
		return "bdserveurlist";
	}
	@GetMapping("/bdServeurConfig")
	public String bdserveurConfig(HttpSession session,
	@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		model.addAttribute("bdserv", id_bdserveur);
		return "dockerList";
	}
}
