package mg.orange.automatisation.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.ReseauDAO;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.exception.sshException;
import mg.orange.automatisation.metier.Deployeur;
import mg.orange.automatisation.metier.SshConnection;

@Controller
public class ReseauController {
	@Autowired
	private ReseauDAO reseau;
	@Autowired
	private BdServeurDAO bdserv;
	
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
		
		try {
			SshConnection sshCon = SshConnection.CreerConnection(new SshConfig("127.0.0.1", "root", "123456",2022));
			Deployeur deploy = new Deployeur(sshCon);
			System.out.println(bdserv.findByReseau(res).size());
			if(bdserv.findByReseau(res).size()==0)
			{
				if(deploy.SupprimerReseau(res)==0)
				{
					reseau.delete(res);		
				}
				else
					System.out.println("reseau non videfff");
			}
			else
			{
				System.out.println("reseau non vide");
			}
		}
		catch (sshException e) {
			e.printStackTrace();
		}			
		
		return "redirect:/reseauList";
	}
}
