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
import mg.orange.automatisation.dao.ConfigDao;
import mg.orange.automatisation.dao.DockerServeurDAO;
import mg.orange.automatisation.dao.IPDAO;
import mg.orange.automatisation.dao.ReseauDAO;
import mg.orange.automatisation.dao.ServeurDAO;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Config;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.Reseau;
import mg.orange.automatisation.entities.Serveur;
import mg.orange.automatisation.entities.SshConfig;
import mg.orange.automatisation.exception.sshException;
import mg.orange.automatisation.metier.Deployeur;
import mg.orange.automatisation.metier.SshConnection;

@Controller
public class BdServeurController {
	
	@Autowired
	private BdServeurDAO bdserv;
	@Autowired 
	private ServeurDAO serv;
	@Autowired
	private ReseauDAO reseau;
	@Autowired
	private ConfigDao config;
	@Autowired 
	private DockerServeurDAO dockerserv;
	
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
	public String bdserveurAjoutGet(HttpSession session,Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		model.addAttribute("reseau", reseau.findAll());
		model.addAttribute("serveur", serv.findAll());
		return "AjoutBdServeur";
	}
	@RequestMapping(value="/BdServeurAjout",method=RequestMethod.POST)
	public String bdserveurAjoutPost(HttpSession session,
			@RequestParam("nombdserveur")String nom,
			@RequestParam("ipbdserveur1")String ip1,
			@RequestParam("ipbdserveur2")String ip2,
			@RequestParam("ipbdserveur3")String ip3,
			@RequestParam("ipbdserveur4")String ip4,
			@RequestParam("adServ1")String ad1,
			@RequestParam("adServ2")String ad2,
			@RequestParam("adServ3")String ad3,
			@RequestParam("adServ4")String ad4,
			@RequestParam("masque")String masque,
			@RequestParam("idserveur")String serveur,
			@RequestParam("reseauType")String reseauType,			
			@RequestParam("mysqlPassword")String mysqlpassword,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		
		try {
			
		IP ipdbserv = new IP(Integer.parseInt(ip1),Integer.parseInt(ip2),Integer.parseInt(ip3),Integer.parseInt(ip4));
		IP addbserv = new IP(Integer.parseInt(ad1),Integer.parseInt(ad2),Integer.parseInt(ad3),Integer.parseInt(ad4));		
		
		SshConnection sshCon = SshConnection.CreerConnection(new SshConfig("127.0.0.1", "root", "123456",2022));		
		
		Deployeur deploy = new Deployeur(sshCon);
		
		if(sshCon!=null)
		{
			Serveur pserveur = serv.findById(Long.valueOf(serveur)).get();
			List<Reseau> res = reseau.findAll();
			Reseau resea = new Reseau();
					
			if(res.size()==0)
			{
				resea = new Reseau(nom,new IP(10,11,10,0),24,pserveur,reseauType);
				
			}
			else
			{
				resea = new Reseau(nom,new IP(10,10,res.get(res.size()-1).getIp_reseau().getPart3()+1,0),24,pserveur,reseauType);
			}
			
			if(deploy.DeployerReseau(resea)==0)
			{
				try {
					bdserv.save(new BdServeur(nom,Integer.parseInt(masque),ipdbserv,addbserv,"travail",pserveur,mysqlpassword,resea));	
				}
				catch(Exception e)
				{
					//erreur sql
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Impossible de creer le reseau !!");
				session.setAttribute("Erreur", "Impossible de creer le reseau !!");
			}
			
			
		}
		else
		{
			System.out.println("Le serveur est inaccessible");
			session.setAttribute("Erreur", "Le serveur est inaccessible");
		}
		
		}
		catch(sshException e)
		{
			e.printStackTrace();
		}
		
		return "redirect:/bdServeurList";
	}
	@GetMapping("/bdServeurConfig")
	public String bdserveurConfig(HttpSession session,
	@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		model.addAttribute("bdserv", id_bdserveur);
		model.addAttribute("ListDockerServeur", dockerserv.findBybdserveur(bdserv.getOne(Long.valueOf(id_bdserveur))));
		
		return "dockerList";
	}
	
	@GetMapping("/bdServeurDeploy")
	public String bdServeurDeploy(HttpSession session,
			@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		if(session.getAttribute("user")==null) return "redirect:/";
		try {
			if(id_bdserveur!=null && !id_bdserveur.isEmpty())
			{
				BdServeur bdserveur = bdserv.findById(Long.valueOf(id_bdserveur)).get();
				SshConnection sshCon = SshConnection.CreerConnection(new SshConfig("127.0.0.1", "root", "123456",2022));
				
				if(sshCon!=null)
				{
					Deployeur deploy = new Deployeur(sshCon); 		
				
					//commande de deploiement 
					deploy.ReelDeployer(bdserveur);
				}
				
			}
		}
		catch(sshException e)
		{
			e.printStackTrace();
		}
		
		return "redirect:/bdServeurList";
	}
	@GetMapping("/bdServeurDelete")
	public String bdServeurDelete(HttpSession session,
			@RequestParam(value="bdserv",required=false)String id_bdserveur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";
		
		if(id_bdserveur!=null && !id_bdserveur.isEmpty())
		{
			BdServeur bdserveur = bdserv.findById(Long.valueOf(id_bdserveur)).get();
			bdserv.delete(bdserveur);			
		}
		
		return "redirect:/bdServeurList";
	}
	@GetMapping("/conf")
	public String conf(HttpSession session)
	{
		config.save(new Config("Base", "WSREP_ON"));
		config.save(new Config("Base", "WSREP_PROVIDER"));
		config.save(new Config("Base", "WSREP_PROVIDER_OPTIONS"));
		config.save(new Config("Base", "WSREP_CLUSTER_ADDRESS"));
		config.save(new Config("Base", "WSREP_CLUSTER_NAME"));
		config.save(new Config("Base", "WSREP_NODE_ADDRESS"));
		config.save(new Config("Base", "WSREP_NODE_NAME"));
		config.save(new Config("Base", "BINLOG_FORMAT"));
		config.save(new Config("Base", "DEFAULT_STORAGE_ENGINE"));
		config.save(new Config("Base", "INNODB_AUTOINC_LOCK_MODE"));
		config.save(new Config("Base", "BIND_ADDRESS"));
		config.save(new Config("Base", "MYSQL_ROOT_PASSWORD"));
		
		return "redirect:/bdServeurList";
	}
}
