//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour electionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="electionIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="domainOfInfluence" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="typeOfElection"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="electionDescription" type="{http://www.evoting.ch/xmlns/config/4}electionDescriptionInformationType"/&gt;
 *         &lt;element name="numberOfMandates" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="writeInsAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="candidateAccumulation"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *               &lt;enumeration value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="minimalCandidateSelectionInList" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="referencedElection" type="{http://www.evoting.ch/xmlns/config/4}referencedElectionInformationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="uiProperties" type="{http://www.evoting.ch/xmlns/config/4}uiPropertiesType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "electionType", propOrder = {
    "electionIdentification",
    "domainOfInfluence",
    "typeOfElection",
    "electionDescription",
    "numberOfMandates",
    "writeInsAllowed",
    "candidateAccumulation",
    "minimalCandidateSelectionInList",
    "referencedElection",
    "uiProperties"
})
public class ElectionType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String electionIdentification;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String domainOfInfluence;
    @XmlElement(required = true)
    protected BigInteger typeOfElection;
    @XmlElement(required = true)
    protected ElectionDescriptionInformationType electionDescription;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numberOfMandates;
    protected boolean writeInsAllowed;
    @XmlElement(required = true)
    protected BigInteger candidateAccumulation;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger minimalCandidateSelectionInList;
    protected List<ReferencedElectionInformationType> referencedElection;
    protected UiPropertiesType uiProperties;

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
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTypeOfElection() {
        return typeOfElection;
    }

    /**
     * Définit la valeur de la propriété typeOfElection.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTypeOfElection(BigInteger value) {
        this.typeOfElection = value;
    }

    /**
     * Obtient la valeur de la propriété electionDescription.
     * 
     * @return
     *     possible object is
     *     {@link ElectionDescriptionInformationType }
     *     
     */
    public ElectionDescriptionInformationType getElectionDescription() {
        return electionDescription;
    }

    /**
     * Définit la valeur de la propriété electionDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ElectionDescriptionInformationType }
     *     
     */
    public void setElectionDescription(ElectionDescriptionInformationType value) {
        this.electionDescription = value;
    }

    /**
     * Obtient la valeur de la propriété numberOfMandates.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfMandates() {
        return numberOfMandates;
    }

    /**
     * Définit la valeur de la propriété numberOfMandates.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfMandates(BigInteger value) {
        this.numberOfMandates = value;
    }

    /**
     * Obtient la valeur de la propriété writeInsAllowed.
     * 
     */
    public boolean isWriteInsAllowed() {
        return writeInsAllowed;
    }

    /**
     * Définit la valeur de la propriété writeInsAllowed.
     * 
     */
    public void setWriteInsAllowed(boolean value) {
        this.writeInsAllowed = value;
    }

    /**
     * Obtient la valeur de la propriété candidateAccumulation.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCandidateAccumulation() {
        return candidateAccumulation;
    }

    /**
     * Définit la valeur de la propriété candidateAccumulation.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCandidateAccumulation(BigInteger value) {
        this.candidateAccumulation = value;
    }

    /**
     * Obtient la valeur de la propriété minimalCandidateSelectionInList.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinimalCandidateSelectionInList() {
        return minimalCandidateSelectionInList;
    }

    /**
     * Définit la valeur de la propriété minimalCandidateSelectionInList.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinimalCandidateSelectionInList(BigInteger value) {
        this.minimalCandidateSelectionInList = value;
    }

    /**
     * Gets the value of the referencedElection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referencedElection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencedElection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferencedElectionInformationType }
     * 
     * 
     */
    public List<ReferencedElectionInformationType> getReferencedElection() {
        if (referencedElection == null) {
            referencedElection = new ArrayList<ReferencedElectionInformationType>();
        }
        return this.referencedElection;
    }

    /**
     * Obtient la valeur de la propriété uiProperties.
     * 
     * @return
     *     possible object is
     *     {@link UiPropertiesType }
     *     
     */
    public UiPropertiesType getUiProperties() {
        return uiProperties;
    }

    /**
     * Définit la valeur de la propriété uiProperties.
     * 
     * @param value
     *     allowed object is
     *     {@link UiPropertiesType }
     *     
     */
    public void setUiProperties(UiPropertiesType value) {
        this.uiProperties = value;
    }

    public ElectionType withElectionIdentification(String value) {
        setElectionIdentification(value);
        return this;
    }

    public ElectionType withDomainOfInfluence(String value) {
        setDomainOfInfluence(value);
        return this;
    }

    public ElectionType withTypeOfElection(BigInteger value) {
        setTypeOfElection(value);
        return this;
    }

    public ElectionType withElectionDescription(ElectionDescriptionInformationType value) {
        setElectionDescription(value);
        return this;
    }

    public ElectionType withNumberOfMandates(BigInteger value) {
        setNumberOfMandates(value);
        return this;
    }

    public ElectionType withWriteInsAllowed(boolean value) {
        setWriteInsAllowed(value);
        return this;
    }

    public ElectionType withCandidateAccumulation(BigInteger value) {
        setCandidateAccumulation(value);
        return this;
    }

    public ElectionType withMinimalCandidateSelectionInList(BigInteger value) {
        setMinimalCandidateSelectionInList(value);
        return this;
    }

    public ElectionType withReferencedElection(ReferencedElectionInformationType... values) {
        if (values!= null) {
            for (ReferencedElectionInformationType value: values) {
                getReferencedElection().add(value);
            }
        }
        return this;
    }

    public ElectionType withReferencedElection(Collection<ReferencedElectionInformationType> values) {
        if (values!= null) {
            getReferencedElection().addAll(values);
        }
        return this;
    }

    public ElectionType withUiProperties(UiPropertiesType value) {
        setUiProperties(value);
        return this;
    }

    public void setReferencedElection(List<ReferencedElectionInformationType> value) {
        this.referencedElection = null;
        if (value!= null) {
            List<ReferencedElectionInformationType> draftl = this.getReferencedElection();
            draftl.addAll(value);
        }
    }

}
