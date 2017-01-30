package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.List;

@XmlRootElement(name = "person")
@XmlType(propOrder = {"idPerson", "firstname", "lastname", "birthdate", "username", "sex", "healthMeasures", "healthMeasuresHistories", "goals"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "idPerson")
	private int idPerson;

	@XmlElement(name = "firstname")
	private String firstname;
	
	@XmlElement(name = "lastname")
	private String lastname;

	@XmlElement(name = "birthdate")
	private String birthdate;

	@XmlElement(name = "username")
	private String username;

	@XmlElement(name = "sex")
	private int sex;

	@XmlElementWrapper(name = "healthMeasures")
	private List<HealthMeasure> healthMeasures;

	@XmlElementWrapper(name = "healthMeasuresHistories")
	private List<HealthMeasureHistory> healthMeasuresHistories;
	
	@XmlElementWrapper(name = "goals")
	private List<Goal> goals;

	public Person() {
	}

	public int getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(int idPerson) {
		this.idPerson = idPerson;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public List<HealthMeasure> getHealthMeasures() {
		return healthMeasures;
	}

	public void setHealthMeasures(List<HealthMeasure> healthMeasures) {
		this.healthMeasures = healthMeasures;
	}

	public List<HealthMeasureHistory> getHealthMeasuresHistories() {
		return healthMeasuresHistories;
	}

	public void setHealthMeasuresHistories(List<HealthMeasureHistory> healthMeasuresHistories) {
		this.healthMeasuresHistories = healthMeasuresHistories;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}
}
