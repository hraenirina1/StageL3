package mg.orange.automatisation.controller;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.ReseauDAO;
import mg.orange.automatisation.dao.ServeurDAO;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.metier.Deployeur;
import mg.orange.automatisation.metier.SshConnection;

@Controller
public class ReseauController {
	@Autowired 
	private ServeurDAO serv;
	@Autowired
	private ReseauDAO reseau;

	
	@GetMapping("/reseauAjout")
	public String ajoutReseauGet(HttpSession session,Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";	
		model.addAttribute("serveur", serv.findAll());
		return "AjoutReseau";
	}
	@RequestMapping(value="/reseauAjout",method=RequestMethod.POST)
	public String ajoutReseauPost(HttpSession session,
			@RequestParam("nomreseau")String nomreseau,
			@RequestParam("reseau1")String reseau1,
			@RequestParam("reseau2")String reseau2,
			@RequestParam("reseau3")String reseau3,
			@RequestParam("reseau4")String reseau4,
			@RequestParam("masque")String masque,
			@RequestParam("idserveur")String idserveur,
			@RequestParam("reseauAction")String reseauAction,
			@RequestParam("reseauType")String reseauType,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";		
		
		
		Serveur serveur = serv.findById(Long.valueOf(idserveur)).get();
		
		SshConnection sshCon = SshConnection.CreerConnection(new SshConfig("127.0.0.1", "root", "123456",2022));
		Deployeur deploy = new Deployeur(sshCon);
		
		if(sshCon!=null)
		{
			if(reseauAction.equals("ReseauExistant")) {
				
					//commande de teste reseau
					if(deploy.testerReseau(nomreseau)==0) reseau.save(new Reseau(nomreseau,new IP(	Integer.parseInt(reseau1),Integer.parseInt(reseau2),Integer.parseInt(reseau3),Integer.parseInt(reseau4)),Integer.parseInt(masque),serveur,reseauType));
					else session.setAttribute("Erreur", "Le reseau n'est pas present sur le serveur");
			
			}
			else if(reseauAction.equals("ReseauDeploy"))
			{
				Reseau res = new Reseau(nomreseau,new IP(Integer.parseInt(reseau1),Integer.parseInt(reseau2),Integer.parseInt(reseau3),Integer.parseInt(reseau4)),Integer.parseInt(masque),serveur,reseauType);
				
				//commande de deployer reseau
				if(deploy.DeployerReseau(res)==0) reseau.save(res);
				else session.setAttribute("Erreur", "Impossible de deployer le reseau");				
			}
		}
		else
		{
			session.setAttribute("Erreur", "Le serveur est inaccessible");
		}
		
		return "redirect:/reseauList";
	}
	
	@GetMapping(value="/reseauList")
	public String ListReseau(HttpSession session,Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";	
		model.addAttribute("reseau", reseau.findAll());
		return "listReseau";
	}
	
	@GetMapping("/reseauDelete")
	public String bdServeurDelete(HttpSession session,
			@RequestParam(value="reseau")String id_reseau,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		Reseau res = reseau.findById(Long.valueOf(id_reseau)).get();
		reseau.delete(res);			
		
		return "redirect:/reseauList";
	}
}
