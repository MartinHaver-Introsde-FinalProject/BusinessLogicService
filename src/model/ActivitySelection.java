package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "activitySelection")
@XmlType(propOrder = {"idActivitySelection", "time", "activity", "usedCalories", "current"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivitySelection implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "idActivitySelection")
	private int idActivitySelection;

	@XmlElement(name = "time")
	private double time;
	
	@XmlElement(name = "activity")
	private Activity activity;

	@XmlElement(name = "usedCalories")
	private double usedCalories;

	@XmlElement(name = "current")
	private int current;
	
	public ActivitySelection() {
		
	}

	public int getIdActivitySelection() {
		return idActivitySelection;
	}

	public void setIdActivitySelection(int idActivitySelection) {
		this.idActivitySelection = idActivitySelection;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getUsedCalories() {
		return usedCalories;
	}

	public void setUsedCalories(double usedCalories) {
		this.usedCalories = usedCalories;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
