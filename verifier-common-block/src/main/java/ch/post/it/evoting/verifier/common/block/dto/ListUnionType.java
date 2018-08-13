
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour listUnionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listUnionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listUnionIdentification">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="LU-1"/>
 *               &lt;enumeration value="ULU-2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="listUnionDescription" type="{http://www.evoting.ch/xmlns/config/3}listUnionDescriptionType"/>
 *         &lt;element name="listUnionType">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="referencedList" maxOccurs="unbounded" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="L3"/>
 *               &lt;enumeration value="L4"/>
 *               &lt;enumeration value="L5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listUnionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "listUnionIdentification",
    "listUnionDescription",
    "listUnionType",
    "referencedList"
})
public class ListUnionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listUnionIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ListUnionDescriptionType listUnionDescription;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listUnionType;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<String> referencedList;

    /**
     * Obtient la valeur de la propriété listUnionIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListUnionIdentification() {
        return listUnionIdentification;
    }

    /**
     * Définit la valeur de la propriété listUnionIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListUnionIdentification(String value) {
        this.listUnionIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété listUnionDescription.
     * 
     * @return
     *     possible object is
     *     {@link ListUnionDescriptionType }
     *     
     */
    public ListUnionDescriptionType getListUnionDescription() {
        return listUnionDescription;
    }

    /**
     * Définit la valeur de la propriété listUnionDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ListUnionDescriptionType }
     *     
     */
    public void setListUnionDescription(ListUnionDescriptionType value) {
        this.listUnionDescription = value;
    }

    /**
     * Obtient la valeur de la propriété listUnionType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListUnionType() {
        return listUnionType;
    }

    /**
     * Définit la valeur de la propriété listUnionType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListUnionType(String value) {
        this.listUnionType = value;
    }

    /**
     * Gets the value of the referencedList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referencedList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencedList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getReferencedList() {
        if (referencedList == null) {
            referencedList = new ArrayList<String>();
        }
        return this.referencedList;
    }

}
