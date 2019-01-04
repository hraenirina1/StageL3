package mg.orange.automatisation.entities;

public class Utilisateur {
	private String user;
	private int port;
	private String adresse;
	private String password;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public Utilisateur(String user, int port, String adresse, String password) {
		super();
		this.user = user;
		this.port = port;
		this.adresse = adresse;
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	
	
}
