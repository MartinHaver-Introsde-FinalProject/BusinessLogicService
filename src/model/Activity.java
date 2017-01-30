package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "activity")
@XmlType(propOrder = {"idActivity", "activityName", "caloriesPerHour", "activityType"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "idActivity")
	private int idActivity;
	
	@XmlElement(name = "activityName")
	private String activityName;
	
	@XmlElement(name = "caloriesPerHour")
	private double caloriesPerHour;
	
	@XmlElement(name = "activityType")
	private String activityType;
	
	public Activity() {
		
	}

	public int getIdActivity() {
		return idActivity;
	}

	public void setIdActivity(int idActivity) {
		this.idActivity = idActivity;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public double getCaloriesPerHour() {
		return caloriesPerHour;
	}

	public void setCaloriesPerHour(double caloriesPerHour) {
		this.caloriesPerHour = caloriesPerHour;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	
}
