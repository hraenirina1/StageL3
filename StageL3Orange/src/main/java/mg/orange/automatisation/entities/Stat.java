package mg.orange.automatisation.entities;

public class Stat {
	private Boolean Mysql;
	private String RAM;
	private String Disque;
	private String CPU;
	
	public Stat() {
		
	}

	public Stat(Boolean mysql, String rAM, String disque, String cPU) {
		super();
		Mysql = mysql;
		RAM = rAM;
		Disque = disque;
		CPU = cPU;
	}

	public Boolean getMysql() {
		return Mysql;
	}

	public void setMysql(Boolean mysql) {
		Mysql = mysql;
	}

	public String getRAM() {
		return RAM;
	}

	public void setRAM(String rAM) {
		RAM = rAM;
	}

	public String getDisque() {
		return Disque;
	}

	public void setDisque(String disque) {
		Disque = disque;
	}

	public String getCPU() {
		return CPU;
	}

	public void setCPU(String cPU) {
		CPU = cPU;
	}
	
	
}
