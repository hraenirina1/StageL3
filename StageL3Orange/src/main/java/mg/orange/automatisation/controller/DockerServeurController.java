package mg.orange.automatisation.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.orange.automatisation.dao.BdServeurDAO;
import mg.orange.automatisation.dao.ConfigDao;
import mg.orange.automatisation.dao.DockerConfig;
import mg.orange.automatisation.dao.DockerServeurDAO;
import mg.orange.automatisation.entities.BdServeur;
import mg.orange.automatisation.entities.Config;
import mg.orange.automatisation.entities.IP;
import mg.orange.automatisation.entities.dockerConfig;
import mg.orange.automatisation.entities.dockerserveur;

@Controller
public class DockerServeurController {
	@Autowired
	private BdServeurDAO bdserv;
	@Autowired 
	private DockerServeurDAO dockerserv; 
	@Autowired
	private ConfigDao config;
	@Autowired 
	private DockerConfig dockerconf; 
	
	@RequestMapping("/dockerServeurAjout")
	public String bdserveurConfig(HttpSession session,
	@RequestParam("bdServ") String id_bdserveur,
	@RequestParam("nbServ") String nb_DockerServeur,
			Model model)
	{
		//if(session.getAttribute("user")==null) return "redirect:/";		
			
		BdServeur bd = bdserv.findById(Long.valueOf(id_bdserveur)).get();
		int j = 1;
		for (int i = 1; i <= Integer.parseInt(nb_DockerServeur); i++) {
			
			while (j <= 255) {
				System.out.println(bd.getAdresseReseau().getPart4().toString() + " " + j);
				InetAddress ad;
				try {
					
					ad = InetAddress.getByName(bd.getAdresseReseau().getPart1().toString()+"." +bd.getAdresseReseau().getPart2().toString()+"."+bd.getAdresseReseau().getPart3().toString()+"."+String.valueOf(bd.getAdresseReseau().getPart4()+j));
					if(ad.isReachable(500)==false) {
						{
							dockerserveur docker = new dockerserveur(bd.getNomBdServeur() + "-galera-" + i, new IP(bd.getAdresseReseau().getPart1(),bd.getAdresseReseau().getPart2(),bd.getAdresseReseau().getPart3(),bd.getAdresseReseau().getPart4()+j),bdserv.getOne(Long.valueOf(id_bdserveur)));
							
							List<Config> conf = config.findByType("Base");
							List<dockerConfig> dconf = new ArrayList<>();
							
							for (Config conf1 : conf) {
								
								switch (conf1.getMot_cle()) {
								case "WSREP_ON":
									dconf.add(new dockerConfig(conf1, "ON"));	
													
									break;
								case "WSREP_PROVIDER":
									dconf.add(new dockerConfig(conf1, "rine"));	
													
									break;

								default:
									break;
								}
							}
							
							docker.setDockerConfig(dconf);
							dockerserv.save(docker);
							j++;
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}				
				j++;	
			}								
		}
		
		//SshConnection sshCon = SshConnection.CreerConnection(new SshConfig("127.0.0.1", "root", "123456",2022));
		
//		try{
//            InetAddress address = InetAddress.getByName("192.168.56.39");
//            boolean reachable = address.isReachable(5000);
//
//            System.out.println("Is host reachable? " + reachable);
//        } catch (Exception e){
//            e.printStackTrace();
        
		
		//Configurateur conf = new Configurateur(sshCon);
		//conf.configurer(Integer.parseInt(nb_DockerServeur));		
		
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
		
		for (dockerConfig dockerCon : conf) {
			System.out.println(dockerCon.getConfigDocker().getMot_cle());
			System.out.println(dockerCon.getValeur());
		}		
		
		return "index";	
	}
	
}