package mg.orange.automatisation.entities;

public class SshConfig {
	
	private String host;
	private String username;
	private String password;
	private int port;
	
	public SshConfig() {
		// TODO Auto-generated constructor stub
	}
	
	public SshConfig(Utilisateur user)
	{
		this.host = user.getAdresse();
		this.username = user.getUser();
		this.password = user.getPassword();
		this.port = user.getPort();
	}
	
	public SshConfig(String host, String username, String password, int port) {
		super();
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	public SshConfig(String host, String username, String password) {
		super();
		this.host = host;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setUser(Utilisateur user)
	{
		this.host = user.getAdresse();
		this.username = user.getUser();
		this.password = user.getPassword();
		this.port = user.getPort();
		
	}
}
