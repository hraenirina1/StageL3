package mg.orange.automatisation.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Log {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(length=6)
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	//autor
	private String autor;
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	//type
	private String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	//message
	private String message; 
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	//lecture
	private boolean lecture;
	public boolean isLecture() {
		return lecture;
	}
	public void setLecture(boolean lecture) {
		this.lecture = lecture;
	}
	
	//datetime
	private Date date;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	public Log(String autor, String type, String message, boolean lecture, Date date) {
		super();
		this.autor = autor;
		this.type = type;
		this.message = message;
		this.lecture = lecture;
		this.date = date;
	}
	public Log() {
	}
}
