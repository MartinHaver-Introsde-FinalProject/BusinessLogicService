package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "healthMeasureHistory")
@XmlType(propOrder = {"idHealthMeasureHistory", "value", "timestamp", "measureDefinition"})
@XmlAccessorType(XmlAccessType.FIELD)
public class HealthMeasureHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "idHealthMeasureHistory")
	private int idHealthMeasureHistory;

	@XmlElement(name = "value")
	private double value;

	@XmlElement(name = "timestamp")
	private String timestamp;

	@XmlElement(name = "measureDefinition")
	private MeasureDefinition measureDefinition;

	public HealthMeasureHistory() {
	}

	public int getIdHealthMeasureHistory() {
		return idHealthMeasureHistory;
	}

	public void setIdHealthMeasureHistory(int idHealthMeasureHistory) {
		this.idHealthMeasureHistory = idHealthMeasureHistory;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public MeasureDefinition getMeasureDefinition() {
		return measureDefinition;
	}

	public void setMeasureDefinition(MeasureDefinition param) {
		this.measureDefinition = param;
	}

	
}
