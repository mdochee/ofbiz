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
 * <p>Java class for PdpThumbnailImageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PdpThumbnailImageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Url"/>
 *         &lt;element ref="{}ThruDate"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PdpThumbnailImageType", propOrder = {
    "url",
    "thruDate"
})
public class PdpThumbnailImageType {

    @XmlElement(name = "Url", required = true, defaultValue = "")
    protected String url;
    @XmlElement(name = "ThruDate", required = true, defaultValue = "")
    protected String thruDate;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the thruDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThruDate() {
        return thruDate;
    }

    /**
     * Sets the value of the thruDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThruDate(String value) {
        this.thruDate = value;
    }

}
