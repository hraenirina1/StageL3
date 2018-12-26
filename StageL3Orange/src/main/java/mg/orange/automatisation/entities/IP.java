package mg.orange.automatisation.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class IP {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id_ip;
	private Integer part1;
	private Integer part2;
	private Integer part3;
	private Integer part4;
	
	public IP() {
		super();
	}
	
	public static IP IPfromString(String ip) {
		String[] ListPartIp = ip.split(".");
		if(ip.contains("."))
		{
		System.out.println(ip);
		}
		if(ListPartIp.length == 4)
		{
			return new IP(Integer.valueOf(ListPartIp[0]),Integer.valueOf(ListPartIp[1]),Integer.valueOf(ListPartIp[2]),Integer.valueOf(ListPartIp[3]));
		}
		return null;
	}
	public IP(Integer part1, Integer part2, Integer part3, Integer part4) {
		super();
		this.part1 = part1;
		this.part2 = part2;
		this.part3 = part3;
		this.part4 = part4;
	}

	public Long getId_ip() {
		return id_ip;
	}

	public void setId_ip(Long id_ip) {
		this.id_ip = id_ip;
	}

	public Integer getPart1() {
		return part1;
	}

	public void setPart1(Integer part1) {
		this.part1 = part1;
	}

	public Integer getPart2() {
		return part2;
	}

	public void setPart2(Integer part2) {
		this.part2 = part2;
	}

	public Integer getPart3() {
		return part3;
	}

	public void setPart3(Integer part3) {
		this.part3 = part3;
	}

	public Integer getPart4() {
		return part4;
	}

	public void setPart4(Integer part4) {
		this.part4 = part4;
	}
	
	@Override
	public String toString()
	{
		return part1.toString() + "." + part2.toString() + "."  + part3.toString() + "." + part4.toString();
	}
	public String reseauTostring()
	{
		return part1.toString() + "." + part2.toString() + "."  + part3.toString() + ".";
	}
}
