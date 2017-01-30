package model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "goal")
@XmlType(propOrder = {"idGoal", "goalName", "current", "idealWeight", "date", "idealBmi", "shavedCalories", "activitySelections"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Goal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "idGoal")
	private int idGoal;
	
	@XmlElement(name = "goalName")
	private String goalName;
	
	@XmlElement(name = "current")
	private int current;
	
	@XmlElement(name = "idealWeight")
	private double idealWeight;
	
	@XmlElement(name = "date")
	private String date;
	
	@XmlElement(name = "idealBmi")
	private double idealBmi;
	
	@XmlElement(name = "shavedCalories")
	private double shavedCalories;
	
	@XmlElementWrapper(name = "activitySelections")
	private List<ActivitySelection> activitySelections;
	
	public Goal() {
		
	}
	
	public int getIdGoal() {
		return idGoal;
	}

	public void setIdGoal(int idGoal) {
		this.idGoal = idGoal;
	}

	public String getGoalName() {
		return goalName;
	}

	public void setGoalName(String goalName) {
		this.goalName = goalName;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public double getIdealWeight() {
		return idealWeight;
	}

	public void setIdealWeight(double idealWeight) {
		this.idealWeight = idealWeight;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getIdealBmi() {
		return idealBmi;
	}

	public void setIdealBmi(double idealBmi) {
		this.idealBmi = idealBmi;
	}

	public double getShavedCalories() {
		return shavedCalories;
	}

	public void setShavedCalories(double shavedCalories) {
		this.shavedCalories = shavedCalories;
	}
	
	public List<ActivitySelection> getActivitySelections() {
		return activitySelections;
	}

	public void setActivitySelections(List<ActivitySelection> activitySelections) {
		this.activitySelections = activitySelections;
	}

}
