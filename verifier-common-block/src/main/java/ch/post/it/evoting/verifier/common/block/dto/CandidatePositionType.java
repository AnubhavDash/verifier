
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour candidatePositionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="candidatePositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="candidateListIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="positionOnList">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="4"/>
 *               &lt;enumeration value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="candidateReferenceOnPosition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="candidateIdentification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="candidateTextOnPosition" type="{http://www.evoting.ch/xmlns/config/3}candidateTextOnPositionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "candidatePositionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "candidateListIdentification",
    "positionOnList",
    "candidateReferenceOnPosition",
    "candidateIdentification",
    "candidateTextOnPosition"
})
public class CandidatePositionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String candidateListIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String positionOnList;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String candidateReferenceOnPosition;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected String candidateIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected CandidateTextOnPositionType candidateTextOnPosition;

    /**
     * Obtient la valeur de la propriété candidateListIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCandidateListIdentification() {
        return candidateListIdentification;
    }

    /**
     * Définit la valeur de la propriété candidateListIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCandidateListIdentification(String value) {
        this.candidateListIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété positionOnList.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPositionOnList() {
        return positionOnList;
    }

    /**
     * Définit la valeur de la propriété positionOnList.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPositionOnList(String value) {
        this.positionOnList = value;
    }

    /**
     * Obtient la valeur de la propriété candidateReferenceOnPosition.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCandidateReferenceOnPosition() {
        return candidateReferenceOnPosition;
    }

    /**
     * Définit la valeur de la propriété candidateReferenceOnPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCandidateReferenceOnPosition(String value) {
        this.candidateReferenceOnPosition = value;
    }

    /**
     * Obtient la valeur de la propriété candidateIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCandidateIdentification() {
        return candidateIdentification;
    }

    /**
     * Définit la valeur de la propriété candidateIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCandidateIdentification(String value) {
        this.candidateIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété candidateTextOnPosition.
     * 
     * @return
     *     possible object is
     *     {@link CandidateTextOnPositionType }
     *     
     */
    public CandidateTextOnPositionType getCandidateTextOnPosition() {
        return candidateTextOnPosition;
    }

    /**
     * Définit la valeur de la propriété candidateTextOnPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link CandidateTextOnPositionType }
     *     
     */
    public void setCandidateTextOnPosition(CandidateTextOnPositionType value) {
        this.candidateTextOnPosition = value;
    }

}
