package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "measureRange")
@XmlType(propOrder = {"idMeasureRange", "rangeName", "startValue", "alarmLevel", "endValue"})
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasureRange implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "idMeasureRange")
	private int idMeasureRange;

	@XmlElement(name = "rangeName")
	private String rangeName;

	@XmlElement(name = "startValue")
	private double startValue;

	@XmlElement(name = "alarmLevel")
	private int alarmLevel;

	@XmlElement(name = "endValue")
	private double endValue;

	public MeasureRange() {
	}

	public int getIdMeasureRange() {
		return idMeasureRange;
	}

	public void setIdMeasureRange(int idMeasureRange) {
		this.idMeasureRange = idMeasureRange;
	}

	public String getRangeName() {
		return rangeName;
	}

	public void setRangeName(String rangeName) {
		this.rangeName = rangeName;
	}

	public double getStartValue() {
		return startValue;
	}

	public void setStartValue(double startValue) {
		this.startValue = startValue;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public double getEndValue() {
		return endValue;
	}

	public void setEndValue(double endValue) {
		this.endValue = endValue;
	}

}
