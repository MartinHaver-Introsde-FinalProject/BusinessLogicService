package model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "measureDefinition")
@XmlType(propOrder = { "idMeasureDefinition", "measureName", "measureType", "measureRanges" })
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasureDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "idMeasureDefinition")
	private int idMeasureDefinition;

	@XmlElement(name = "measureName")
	private String measureName;

	@XmlElement(name = "measureType")
	private String measureType;

	@XmlElementWrapper(name = "measureRanges")
	private List<MeasureRange> measureRanges;

	public MeasureDefinition() {
	}

	public int getIdMeasureDefinition() {
		return idMeasureDefinition;
	}

	public void setIdMeasureDefinition(int idMeasureDefinition) {
		this.idMeasureDefinition = idMeasureDefinition;
	}

	public String getMeasureName() {
		return this.measureName;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	public String getMeasureType() {
		return this.measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public List<MeasureRange> getMeasureRanges() {
		return measureRanges;
	}

	public void setMeasureRanges(List<MeasureRange> measureRanges) {
		this.measureRanges = measureRanges;
	}

}
