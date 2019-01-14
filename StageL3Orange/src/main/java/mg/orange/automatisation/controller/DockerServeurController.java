package mg.orange.automatisation.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.ConfigDAO;
import mg.orange.automatisation.dao.DockerServeurDAO;
import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.dassh.DockerDASSH;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;
import mg.orange.automatisation.exception.ConfigException;
import mg.orange.automatisation.exception.serveurException;

@Controller
public class DockerServeurController {
	@Autowired
	private BdServeurDAO bdserv;
	@Autowired 
	private DockerServeurDAO dockerserv; 
	@Autowired
	private ConfigDAO config;
	@Autowired
	private IPDAO ip;
	
	@RequestMapping("/dockerServeurAjout")
	public String dockerServeurAjout(HttpSession session,
	@RequestParam("bdServ") String id_bdserveur,
	@RequestParam("nbServ") String nb_DockerServeur,
			Model model) 
	{
		if(session.getAttribute("user")==null) return "redirect:/";		
			Utilisateur user = (Utilisateur) session.getAttribute("user");
		
		//recuperer la base de donnees
		BdServeur bd = bdserv.findById(Long.valueOf(id_bdserveur)).get();
		
		//configurer les serveurs dockers
		
		List<dockerserveur> dockers;
		
		try {
				//configuration docker
				dockers = DockerDASSH.configurer(user, bd.getServeur(), bd, Integer.parseInt(nb_DockerServeur), config.findByType("Base"),ip.findAll());
				
				//enregistrement docker
				for (dockerserveur dockerserveur : dockers) {
					dockerserv.save(dockerserveur);
				}
			
		} catch (NumberFormatException | serveurException | ConfigException e) {			
			model.addAttribute("Erreur", e.getMessage());
		}		
		
		return "redirect:/bdServeurConfig?bdserv=" + id_bdserveur;
	}
	@RequestMapping("/dockerServeurConfig")
	public String dockerServeurConfig(HttpSession session,
	@RequestParam("dockerServ") String id_dockerServeur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		dockerserveur dockerserveurs = dockerserv.findById(Long.valueOf(id_dockerServeur)).get();
		List<dockerConfig> conf = dockerserveurs.getDockerConfig();
	
		model.addAttribute("listconfig",conf);
		return "ListConfig";	
	}
	
}