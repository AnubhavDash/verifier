
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour electionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="electionIdentification">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Eidgenoessische_Wahlen"/>
 *               &lt;enumeration value="majorz_wahl"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="domainOfInfluence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="typeOfElection">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="electionDescription" type="{http://www.evoting.ch/xmlns/config/3}electionDescriptionType"/>
 *         &lt;element name="numberOfMandates">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="5"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="writeInsAllowed">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="false"/>
 *               &lt;enumeration value="true"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="candidateAccumulation">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="1"/>
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
@XmlType(name = "electionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "electionIdentification",
    "domainOfInfluence",
    "typeOfElection",
    "electionDescription",
    "numberOfMandates",
    "writeInsAllowed",
    "candidateAccumulation"
})
public class ElectionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String electionIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String domainOfInfluence;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String typeOfElection;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ElectionDescriptionType electionDescription;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String numberOfMandates;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String writeInsAllowed;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String candidateAccumulation;

    /**
     * Obtient la valeur de la propriété electionIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElectionIdentification() {
        return electionIdentification;
    }

    /**
     * Définit la valeur de la propriété electionIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElectionIdentification(String value) {
        this.electionIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété domainOfInfluence.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainOfInfluence() {
        return domainOfInfluence;
    }

    /**
     * Définit la valeur de la propriété domainOfInfluence.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainOfInfluence(String value) {
        this.domainOfInfluence = value;
    }

    /**
     * Obtient la valeur de la propriété typeOfElection.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeOfElection() {
        return typeOfElection;
    }

    /**
     * Définit la valeur de la propriété typeOfElection.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeOfElection(String value) {
        this.typeOfElection = value;
    }

    /**
     * Obtient la valeur de la propriété electionDescription.
     * 
     * @return
     *     possible object is
     *     {@link ElectionDescriptionType }
     *     
     */
    public ElectionDescriptionType getElectionDescription() {
        return electionDescription;
    }

    /**
     * Définit la valeur de la propriété electionDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ElectionDescriptionType }
     *     
     */
    public void setElectionDescription(ElectionDescriptionType value) {
        this.electionDescription = value;
    }

    /**
     * Obtient la valeur de la propriété numberOfMandates.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfMandates() {
        return numberOfMandates;
    }

    /**
     * Définit la valeur de la propriété numberOfMandates.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfMandates(String value) {
        this.numberOfMandates = value;
    }

    /**
     * Obtient la valeur de la propriété writeInsAllowed.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWriteInsAllowed() {
        return writeInsAllowed;
    }

    /**
     * Définit la valeur de la propriété writeInsAllowed.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWriteInsAllowed(String value) {
        this.writeInsAllowed = value;
    }

    /**
     * Obtient la valeur de la propriété candidateAccumulation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCandidateAccumulation() {
        return candidateAccumulation;
    }

    /**
     * Définit la valeur de la propriété candidateAccumulation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCandidateAccumulation(String value) {
        this.candidateAccumulation = value;
    }

}
