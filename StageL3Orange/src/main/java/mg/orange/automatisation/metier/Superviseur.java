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
			DriverManager.getConnection("jdbc:mysql://"+ip+"/mysql");
			return true;
		} catch (ClassNotFoundException | SQLException es) {
			String Error = es.getMessage();
			if(Error.contains("Acc"))
				return true;
			else
				return false;
		}
	}
	
	public static Stat statistique(String ip, SshConnection	connectionssh)
	{
		Stat stat = new Stat();
		//stat	
			String[] ligne = 
					connectionssh.ExecuterCommandeRecupOutStat("nc "+ip+" 1234").split("\n");
			
			/*
			 * 1 - ram total
			 * 2 - ram libre
			 * 3 - ram utiliser
			 * 
			 * 4 - cpu sys
			 * 5 - cpu ni
			 * 6 - cpu libre
			 * 
			 * 0verlay - 
			 * */
			//int i = Integer.parseInt(ligne[1]) + Integer.parseInt(ligne[2]);
			stat.setRAM(""+ ligne[1] +"");	
			
			Double j = Double.valueOf(ligne[3]) + Double.valueOf(ligne[4]);
			//Double k = j + Double.valueOf(ligne[5]);
			
			stat.setCPU(""+ j +"");
			stat.setDisque(ligne[7]);
				
			connectionssh.ExecuterCommandeRecupOut("pkill -fx 'nc "+ip+" 1234'");
			
			return stat;

	}
}
