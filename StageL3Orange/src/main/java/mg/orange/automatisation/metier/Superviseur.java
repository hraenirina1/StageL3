package mg.orange.automatisation.metier;

import java.sql.DriverManager;
import java.sql.SQLException;

import mg.orange.automatisation.dassh.SshConnection;
import mg.orange.automatisation.entities.Stat;

public class Superviseur {

	public Superviseur() {
		// TODO Auto-generated constructor stub
	}
	public static Boolean testMysql(String ip)
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");				
			DriverManager.getConnection("jdbc:mysql://"+ip+"/","haproxy","").close();
			return true;
		} catch (ClassNotFoundException | SQLException es) {
				System.out.println(es.getMessage());
				return false;
		}
	}
}
