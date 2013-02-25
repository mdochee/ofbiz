//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.25 at 02:09:28 PM CET 
//


package com.osafe.feeds.osafefeeds;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrderLineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderLineType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProductId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SequenceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Quantity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Price" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OfferPrice" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LineTotalGross" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SalesTax" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ShippingCharge" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ShippingTax" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Carrier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CarrierCustomInfo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OrderLinePromotion" type="{}OrderLinePromotionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderLineType", propOrder = {
    "productId",
    "sequenceId",
    "quantity",
    "price",
    "offerPrice",
    "lineTotalGross",
    "salesTax",
    "shippingCharge",
    "shippingTax",
    "carrier",
    "carrierCustomInfo",
    "orderLinePromotion"
})
public class OrderLineType {

    @XmlElement(name = "ProductId", required = true)
    protected String productId;
    @XmlElement(name = "SequenceId", required = true)
    protected String sequenceId;
    @XmlElement(name = "Quantity")
    protected int quantity;
    @XmlElement(name = "Price", required = true)
    protected String price;
    @XmlElement(name = "OfferPrice", required = true)
    protected String offerPrice;
    @XmlElement(name = "LineTotalGross", required = true)
    protected String lineTotalGross;
    @XmlElement(name = "SalesTax", required = true)
    protected String salesTax;
    @XmlElement(name = "ShippingCharge", required = true)
    protected String shippingCharge;
    @XmlElement(name = "ShippingTax", required = true)
    protected String shippingTax;
    @XmlElement(name = "Carrier", required = true)
    protected String carrier;
    @XmlElement(name = "CarrierCustomInfo", required = true)
    protected String carrierCustomInfo;
    @XmlElement(name = "OrderLinePromotion", required = true)
    protected List<OrderLinePromotionType> orderLinePromotion;

    /**
     * Gets the value of the productId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the value of the productId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductId(String value) {
        this.productId = value;
    }

    /**
     * Gets the value of the sequenceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequenceId() {
        return sequenceId;
    }

    /**
     * Sets the value of the sequenceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequenceId(String value) {
        this.sequenceId = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     */
    public void setQuantity(int value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrice(String value) {
        this.price = value;
    }

    /**
     * Gets the value of the offerPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfferPrice() {
        return offerPrice;
    }

    /**
     * Sets the value of the offerPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfferPrice(String value) {
        this.offerPrice = value;
    }

    /**
     * Gets the value of the lineTotalGross property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineTotalGross() {
        return lineTotalGross;
    }

    /**
     * Sets the value of the lineTotalGross property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineTotalGross(String value) {
        this.lineTotalGross = value;
    }

    /**
     * Gets the value of the salesTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesTax() {
        return salesTax;
    }

    /**
     * Sets the value of the salesTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesTax(String value) {
        this.salesTax = value;
    }

    /**
     * Gets the value of the shippingCharge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingCharge() {
        return shippingCharge;
    }

    /**
     * Sets the value of the shippingCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingCharge(String value) {
        this.shippingCharge = value;
    }

    /**
     * Gets the value of the shippingTax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingTax() {
        return shippingTax;
    }

    /**
     * Sets the value of the shippingTax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingTax(String value) {
        this.shippingTax = value;
    }

    /**
     * Gets the value of the carrier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * Sets the value of the carrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrier(String value) {
        this.carrier = value;
    }

    /**
     * Gets the value of the carrierCustomInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrierCustomInfo() {
        return carrierCustomInfo;
    }

    /**
     * Sets the value of the carrierCustomInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrierCustomInfo(String value) {
        this.carrierCustomInfo = value;
    }

    /**
     * Gets the value of the orderLinePromotion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orderLinePromotion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderLinePromotion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderLinePromotionType }
     * 
     * 
     */
    public List<OrderLinePromotionType> getOrderLinePromotion() {
        if (orderLinePromotion == null) {
            orderLinePromotion = new ArrayList<OrderLinePromotionType>();
        }
        return this.orderLinePromotion;
    }

}
