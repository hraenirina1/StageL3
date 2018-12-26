package mg.orange.automatisation.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.ConfigDao;
import mg.orange.automatisation.dao.DockerServeurDAO;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;
import mg.orange.automatisation.metier.Configurateur;
import mg.orange.automatisation.metier.Deployeur;
import mg.orange.automatisation.metier.SshConnection;

@Controller
public class DockerServeurController {
	@Autowired
	private BdServeurDAO bdserv;
	@Autowired 
	private DockerServeurDAO dockerserv; 
	@Autowired
	private ConfigDao config;
	
	@RequestMapping("/dockerServeurAjout")
	public String dockerServeurAjout(HttpSession session,
	@RequestParam("bdServ") String id_bdserveur,
	@RequestParam("nbServ") String nb_DockerServeur,
			Model model) throws NumberFormatException, Exception
	{
		//if(session.getAttribute("user")==null) return "redirect:/";		
			
		//recuperer la base de donnees
		BdServeur bd = bdserv.findById(Long.valueOf(id_bdserveur)).get();
		
		//configurer les serveurs dockers
		Configurateur configurateur = new Configurateur(new SshConfig("127.0.0.1","root","123456",2022));
		List<dockerserveur> dockers = configurateur.configurer(bd, Integer.parseInt(nb_DockerServeur), config.findByType("Base"));
		
		for (dockerserveur dockerserveur : dockers) {
			dockerserv.save(dockerserveur);
		}
		
		return "redirect:/bdServeurConfig?bdserv=" + id_bdserveur;
	}
	
	@RequestMapping("/dockerServeurConfig")
	public String dockerServeurConfig(HttpSession session,
	@RequestParam("dockerServ") String id_dockerServeur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		dockerserveur dockerserveurs = dockerserv.findById(Long.valueOf(id_dockerServeur)).get();
		List<dockerConfig> conf = dockerserveurs.getDockerConfig();
	
		model.addAttribute("listconfig",conf);
		return "ListConfig";	
	}
	
}