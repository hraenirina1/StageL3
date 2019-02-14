package mg.orange.automatisation.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.metier.DockerMetier;

import mg.orange.automatisation.entities.BDServeur;
import mg.orange.automatisation.entities.Configuration;
import mg.orange.automatisation.entities.Utilisateur;
import mg.orange.automatisation.entities.DockerServeur;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.exception.ConfigException;
import mg.orange.automatisation.exception.serveurException;

@Controller
public class DockerServeurController {
	@Autowired
	private DockerMetier dockerserv;

	@GetMapping("/bdServeurConfig")
	public String AjoutDockerGet(HttpSession session,
	@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		model.addAttribute("bdserv", id_bdserveur);
		model.addAttribute("ListDockerServeur", dockerserv.recupererDocker(Long.valueOf(id_bdserveur)));
		
		if(session.getAttribute("Erreur")!=null) 
		{
			model.addAttribute("Erreur", session.getAttribute("Erreur"));
			session.removeAttribute("Erreur");
		}
		
		return "PageAjoutDocker";
	}
	
	@RequestMapping("/dockerServeurAjout")
	public String AjouterDockerPost(HttpSession session,
	@RequestParam MultiValueMap<String, String> parametre_multiple,
	@RequestParam("bdServ") String id_bdserveur,
	@RequestParam("nbServ") String nb_DockerServeur,
	@RequestParam("ram") String ram,
	@RequestParam("cpu") String cpu,
			Model model) 
	{
		if(session.getAttribute("user")==null) return "redirect:/";		
			Utilisateur user = (Utilisateur) session.getAttribute("user");
		
			//recuperer la base de donnees
			BDServeur bd = dockerserv.recupererBDServeur(Long.valueOf(id_bdserveur));
			
			try {
			
			//essai ip	
			List<String> ip_part1 = parametre_multiple.get("ip1");
			List<String> ip_part2 = parametre_multiple.get("ip2");
			List<String> ip_part3 = parametre_multiple.get("ip3");
			List<String> ip_part4 = parametre_multiple.get("ip4");
				
			int i = 0;
			
			List<IP> ip;
			
			if(ip_part1!=null)
			{
				ip = new ArrayList<>();
				
				for (String string: ip_part1) {
					ip.add(new IP(Integer.parseInt(string),Integer.parseInt(ip_part2.get(i)),Integer.parseInt(ip_part3.get(i)),Integer.parseInt(ip_part4.get(i))));
					i++;
				}
			}
			else
			{				
				
				//recuperer ip disponible
				ip = dockerserv.recupererIpDisponible(user, bd.getReseau(),Integer.parseInt(nb_DockerServeur));
			}
			
			//configurer les serveurs dockers		
			List<DockerServeur> dockers = dockerserv.ConfigurerDocker(ip, ram, cpu, bd);	
			
					//enregistrement docker
					for (DockerServeur dockerserveur : dockers) {
						dockerserv.enregistrer(dockerserveur);
					}
				
			} catch (NumberFormatException | serveurException | ConfigException e) {			
				session.setAttribute("Erreur", e.getMessage());
				System.out.print(e.getMessage());
			}		
		
		return "redirect:/bdServeurConfig?bdserv=" + id_bdserveur;
	}
	@RequestMapping("/dockerServeurConfig")
	public String visionnerDocker(HttpSession session,
	@RequestParam("dockerServ") String id_dockerServeur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		DockerServeur dockerserveurs = dockerserv.recuperer(Long.valueOf(id_dockerServeur));
		List<Configuration> conf = dockerserveurs.getConfig();

		model.addAttribute("listconfig",conf);
	return "ListConfig";	
	}
	
}