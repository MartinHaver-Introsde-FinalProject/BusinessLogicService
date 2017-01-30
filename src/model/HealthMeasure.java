package model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "healthMeasure")
@XmlType(propOrder = {"idHealthMeasure", "measureDefinition", "value"})
@XmlAccessorType(XmlAccessType.FIELD)
public class HealthMeasure implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "idHealthMeasure")
	private int idHealthMeasure;

	@XmlElement(name = "measureDefinition")
	private MeasureDefinition measureDefinition;

	@XmlElement(name = "value")
	private double value;

	public HealthMeasure() {
	}

	public int getIdHealthMeasure() {
		return idHealthMeasure;
	}

	public void setIdHealthMeasure(int idHealthMeasure) {
		this.idHealthMeasure = idHealthMeasure;
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
