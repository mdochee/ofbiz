//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.25 at 02:09:28 PM CET 
//


package com.osafe.feeds.osafefeeds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FeatureSwatchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FeatureSwatchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FeatureId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PlpSwatch" type="{}PlpSwatchType"/>
 *         &lt;element name="PdpSwatch" type="{}PdpSwatchType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureSwatchType", propOrder = {
    "featureId",
    "value",
    "plpSwatch",
    "pdpSwatch"
})
public class FeatureSwatchType {

    @XmlElement(name = "FeatureId", required = true, defaultValue = "")
    protected String featureId;
    @XmlElement(name = "Value", required = true, defaultValue = "")
    protected String value;
    @XmlElement(name = "PlpSwatch", required = true)
    protected PlpSwatchType plpSwatch;
    @XmlElement(name = "PdpSwatch", required = true)
    protected PdpSwatchType pdpSwatch;

    /**
     * Gets the value of the featureId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeatureId() {
        return featureId;
    }

    /**
     * Sets the value of the featureId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeatureId(String value) {
        this.featureId = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the plpSwatch property.
     * 
     * @return
     *     possible object is
     *     {@link PlpSwatchType }
     *     
     */
    public PlpSwatchType getPlpSwatch() {
        return plpSwatch;
    }

    /**
     * Sets the value of the plpSwatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlpSwatchType }
     *     
     */
    public void setPlpSwatch(PlpSwatchType value) {
        this.plpSwatch = value;
    }

    /**
     * Gets the value of the pdpSwatch property.
     * 
     * @return
     *     possible object is
     *     {@link PdpSwatchType }
     *     
     */
    public PdpSwatchType getPdpSwatch() {
        return pdpSwatch;
    }

    /**
     * Sets the value of the pdpSwatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link PdpSwatchType }
     *     
     */
    public void setPdpSwatch(PdpSwatchType value) {
        this.pdpSwatch = value;
    }

}
