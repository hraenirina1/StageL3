package mg.orange.automatisation.metier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import mg.orange.automatisation.entities.SshConfig;

public class SshConnection {
	
	private SshConfig config;
	private Session session;
	
	private SshConnection() {
		
	}
	private SshConnection(SshConfig config, Session session) {
		super();
		this.config = config;
		this.session = session;
	}

	public static SshConnection CreerConnection(SshConfig conf)
	{
		JSch ssh = new JSch();

		try {
			Session session = ssh.getSession(conf.getUsername(), conf.getHost(),conf.getPort());
			session.setPassword(conf.getPassword());
			session.setConfig ("StrictHostKeyChecking", "no");
			session.connect();
			
			return new SshConnection(conf,session);
		} catch (JSchException e) {
			e.printStackTrace();
			return null;
		}		
		
	}
	public SshConfig getConfig() {
		return config;
	}
		
	
	//action
	public void ExecuterCommande(List<String> commandes)
	{
		try {
			
				/* session deja connecter */
				
				//execution des commandes
				for (String commande : commandes) {
					
					
					//creation d'un terminal
					Channel terminal = session.openChannel("exec");				
					
					//executer commande
					((ChannelExec)terminal).setCommand(commande);
					
					//ouverture du terminal
					terminal.connect();
					
					//affichage terminal
					InputStream in = terminal.getInputStream();
					
					byte[] tmp = new byte[1024];
			        while (true) {
			            while (in.available() > 0) {
			                int i = in.read(tmp, 0, 1024);
			                if (i < 0)
			                    break;
			                System.out.print(new String(tmp, 0, i));
			            }
			            if (terminal.isClosed()) {
			                System.out.println("exit-status: " + terminal.getExitStatus());
			                break;
			            }
			            try {
			                Thread.sleep(1000);
			            } catch (Exception ee) {
			                System.out.println(ee);
			            }
			        }
					//fermeture du terminal    
					terminal.disconnect();
				}				
				
			} catch (JSchException | IOException e) {
				e.printStackTrace();
			}	
	}
	public void ChangerConfig(SshConfig conf)
	{
		JSch ssh = new JSch();

		try {
				session.disconnect();
				Session session = ssh.getSession(conf.getUsername(), conf.getHost(),conf.getPort());
				session.setPassword(conf.getPassword());
				session.setConfig ("StrictHostKeyChecking", "no");
				session.connect();
				this.config = conf;
			
		} catch (JSchException e) {
			e.printStackTrace();
		}		
		
	}
	public int pinger(String adresse) throws Exception
	{
		//creation d'un terminal
		Channel terminal;
		try {
			
			terminal = session.openChannel("exec");
			
			//executer commande
			((ChannelExec)terminal).setCommand("ping -w 2 " + adresse);
			
			//ouverture du terminal
			terminal.connect();
			
	        while (true) {
	            if (terminal.isClosed()) {
	            	
	            	//fermeture du terminal    
	    			terminal.disconnect();
	    			
	            	return terminal.getExitStatus();	
	            	
	            	}
	            
	            try {
	                Thread.sleep(1000);
	            } catch (Exception ee) {
	                System.out.println(ee);
	            }
	        }
							
			
			
			
		} catch (JSchException e) {			
			throw new Exception("Erreur fatale");
		}
		
	}
}
