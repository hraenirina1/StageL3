package mg.orange.automatisation.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.metier.Configurateur;
import mg.orange.automatisation.metier.Fichier;
import mg.orange.automatisation.metier.SshConnection;

@Controller
public class DockerServeurController {
	@Autowired
	private BdServeurDAO bdserv;
	
	@RequestMapping("/dockerServeurAjout")
	public String bdserveurConfig(HttpSession session,
	@RequestParam("bdServ") String id_bdserveur,
	@RequestParam("nbServ") String nb_DockerServeur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		BdServeur bd = bdserv.findById(Long.valueOf(id_bdserveur)).get();		
		SshConnection sshCon = SshConnection.CreerConnection(new SshConfig("127.0.0.1", "root", "123456",2022));
		
		Configurateur conf = new Configurateur(sshCon);
		conf.configurer(Integer.parseInt(nb_DockerServeur));		
		
		return "dockerList";
	}

}
