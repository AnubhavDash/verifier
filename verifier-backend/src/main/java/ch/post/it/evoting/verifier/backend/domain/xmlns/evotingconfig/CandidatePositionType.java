//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour candidatePositionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="candidatePositionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="candidateListIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="positionOnList" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="candidateReferenceOnPosition"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="candidateIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType" minOccurs="0"/&gt;
 *         &lt;element name="candidateTextOnPosition" type="{http://www.evoting.ch/xmlns/config/4}candidateTextInformationType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "candidatePositionType", propOrder = {
    "candidateListIdentification",
    "positionOnList",
    "candidateReferenceOnPosition",
    "candidateIdentification",
    "candidateTextOnPosition"
})
public class CandidatePositionType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String candidateListIdentification;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger positionOnList;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String candidateReferenceOnPosition;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String candidateIdentification;
    @XmlElement(required = true)
    protected CandidateTextInformationType candidateTextOnPosition;

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
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPositionOnList() {
        return positionOnList;
    }

    /**
     * Définit la valeur de la propriété positionOnList.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPositionOnList(BigInteger value) {
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
     *     {@link CandidateTextInformationType }
     *     
     */
    public CandidateTextInformationType getCandidateTextOnPosition() {
        return candidateTextOnPosition;
    }

    /**
     * Définit la valeur de la propriété candidateTextOnPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link CandidateTextInformationType }
     *     
     */
    public void setCandidateTextOnPosition(CandidateTextInformationType value) {
        this.candidateTextOnPosition = value;
    }

    public CandidatePositionType withCandidateListIdentification(String value) {
        setCandidateListIdentification(value);
        return this;
    }

    public CandidatePositionType withPositionOnList(BigInteger value) {
        setPositionOnList(value);
        return this;
    }

    public CandidatePositionType withCandidateReferenceOnPosition(String value) {
        setCandidateReferenceOnPosition(value);
        return this;
    }

    public CandidatePositionType withCandidateIdentification(String value) {
        setCandidateIdentification(value);
        return this;
    }

    public CandidatePositionType withCandidateTextOnPosition(CandidateTextInformationType value) {
        setCandidateTextOnPosition(value);
        return this;
    }

}
