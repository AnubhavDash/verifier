
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour listType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="listIndentureNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="E1-L1"/>
 *               &lt;enumeration value="E1-L2"/>
 *               &lt;enumeration value="E1-L3"/>
 *               &lt;enumeration value="E1-L4"/>
 *               &lt;enumeration value="E1-L5"/>
 *               &lt;enumeration value="99"/>
 *               &lt;enumeration value="5"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="listDescription" type="{http://www.evoting.ch/xmlns/config/3}listDescriptionType"/>
 *         &lt;element name="listOrderOfPrecedence">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="4"/>
 *               &lt;enumeration value="5"/>
 *               &lt;enumeration value="6"/>
 *               &lt;enumeration value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="listEmpty">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="false"/>
 *               &lt;enumeration value="true"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="candidatePosition" type="{http://www.evoting.ch/xmlns/config/3}candidatePositionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "listIdentification",
    "listIndentureNumber",
    "listDescription",
    "listOrderOfPrecedence",
    "listEmpty",
    "candidatePosition"
})
public class ListType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listIndentureNumber;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ListDescriptionType listDescription;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listOrderOfPrecedence;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listEmpty;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<CandidatePositionType> candidatePosition;

    /**
     * Obtient la valeur de la propriété listIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListIdentification() {
        return listIdentification;
    }

    /**
     * Définit la valeur de la propriété listIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListIdentification(String value) {
        this.listIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété listIndentureNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListIndentureNumber() {
        return listIndentureNumber;
    }

    /**
     * Définit la valeur de la propriété listIndentureNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListIndentureNumber(String value) {
        this.listIndentureNumber = value;
    }

    /**
     * Obtient la valeur de la propriété listDescription.
     * 
     * @return
     *     possible object is
     *     {@link ListDescriptionType }
     *     
     */
    public ListDescriptionType getListDescription() {
        return listDescription;
    }

    /**
     * Définit la valeur de la propriété listDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ListDescriptionType }
     *     
     */
    public void setListDescription(ListDescriptionType value) {
        this.listDescription = value;
    }

    /**
     * Obtient la valeur de la propriété listOrderOfPrecedence.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListOrderOfPrecedence() {
        return listOrderOfPrecedence;
    }

    /**
     * Définit la valeur de la propriété listOrderOfPrecedence.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListOrderOfPrecedence(String value) {
        this.listOrderOfPrecedence = value;
    }

    /**
     * Obtient la valeur de la propriété listEmpty.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListEmpty() {
        return listEmpty;
    }

    /**
     * Définit la valeur de la propriété listEmpty.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListEmpty(String value) {
        this.listEmpty = value;
    }

    /**
     * Gets the value of the candidatePosition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the candidatePosition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCandidatePosition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CandidatePositionType }
     * 
     * 
     */
    public List<CandidatePositionType> getCandidatePosition() {
        if (candidatePosition == null) {
            candidatePosition = new ArrayList<CandidatePositionType>();
        }
        return this.candidatePosition;
    }

}
