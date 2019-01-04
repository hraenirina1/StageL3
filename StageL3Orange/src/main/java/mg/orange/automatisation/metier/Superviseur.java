package mg.orange.automatisation.metier;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Superviseur {

	public Superviseur() {
		// TODO Auto-generated constructor stub
	}
	public static Boolean testMysql(String ip)
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");				
			DriverManager.getConnection("jdbc:mysql://"+ip+"/mysql");
			return true;
		} catch (ClassNotFoundException | SQLException es) {
			String Error = es.getMessage();
			
			if(Error.contains("Access denied for user"))
				return true;
			else
				return false;
		}
		
	}
}
