//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour contestType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="contestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="contestIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="contestDefaultLanguage" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *         &lt;element name="contestDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="contestDescription" type="{http://www.evoting.ch/xmlns/config/4}contestDescriptionInformationType"/&gt;
 *         &lt;element name="evotingFromDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="evotingToDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="electoralAuthority" type="{http://www.evoting.ch/xmlns/config/4}electoralAuthorityType" minOccurs="0"/&gt;
 *         &lt;element name="extendedAuthenticationKeys" type="{http://www.evoting.ch/xmlns/config/4}extendedAuthenticationKeysDefinitionType" minOccurs="0"/&gt;
 *         &lt;element name="electionInformation" type="{http://www.evoting.ch/xmlns/config/4}electionInformationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="voteInformation" type="{http://www.evoting.ch/xmlns/config/4}voteInformationType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "contestType", propOrder = {
    "contestIdentification",
    "contestDefaultLanguage",
    "contestDate",
    "contestDescription",
    "evotingFromDate",
    "evotingToDate",
    "electoralAuthority",
    "extendedAuthenticationKeys",
    "electionInformation",
    "voteInformation",
    "uiProperties"
})
public class ContestType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String contestIdentification;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected LanguageType contestDefaultLanguage;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar contestDate;
    @XmlElement(required = true)
    protected ContestDescriptionInformationType contestDescription;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar evotingFromDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar evotingToDate;
    protected ElectoralAuthorityType electoralAuthority;
    protected ExtendedAuthenticationKeysDefinitionType extendedAuthenticationKeys;
    protected List<ElectionInformationType> electionInformation;
    protected List<VoteInformationType> voteInformation;
    protected UiPropertiesType uiProperties;

    /**
     * Obtient la valeur de la propriété contestIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContestIdentification() {
        return contestIdentification;
    }

    /**
     * Définit la valeur de la propriété contestIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContestIdentification(String value) {
        this.contestIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété contestDefaultLanguage.
     * 
     * @return
     *     possible object is
     *     {@link LanguageType }
     *     
     */
    public LanguageType getContestDefaultLanguage() {
        return contestDefaultLanguage;
    }

    /**
     * Définit la valeur de la propriété contestDefaultLanguage.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageType }
     *     
     */
    public void setContestDefaultLanguage(LanguageType value) {
        this.contestDefaultLanguage = value;
    }

    /**
     * Obtient la valeur de la propriété contestDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getContestDate() {
        return contestDate;
    }

    /**
     * Définit la valeur de la propriété contestDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setContestDate(XMLGregorianCalendar value) {
        this.contestDate = value;
    }

    /**
     * Obtient la valeur de la propriété contestDescription.
     * 
     * @return
     *     possible object is
     *     {@link ContestDescriptionInformationType }
     *     
     */
    public ContestDescriptionInformationType getContestDescription() {
        return contestDescription;
    }

    /**
     * Définit la valeur de la propriété contestDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ContestDescriptionInformationType }
     *     
     */
    public void setContestDescription(ContestDescriptionInformationType value) {
        this.contestDescription = value;
    }

    /**
     * Obtient la valeur de la propriété evotingFromDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEvotingFromDate() {
        return evotingFromDate;
    }

    /**
     * Définit la valeur de la propriété evotingFromDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEvotingFromDate(XMLGregorianCalendar value) {
        this.evotingFromDate = value;
    }

    /**
     * Obtient la valeur de la propriété evotingToDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEvotingToDate() {
        return evotingToDate;
    }

    /**
     * Définit la valeur de la propriété evotingToDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEvotingToDate(XMLGregorianCalendar value) {
        this.evotingToDate = value;
    }

    /**
     * Obtient la valeur de la propriété electoralAuthority.
     * 
     * @return
     *     possible object is
     *     {@link ElectoralAuthorityType }
     *     
     */
    public ElectoralAuthorityType getElectoralAuthority() {
        return electoralAuthority;
    }

    /**
     * Définit la valeur de la propriété electoralAuthority.
     * 
     * @param value
     *     allowed object is
     *     {@link ElectoralAuthorityType }
     *     
     */
    public void setElectoralAuthority(ElectoralAuthorityType value) {
        this.electoralAuthority = value;
    }

    /**
     * Obtient la valeur de la propriété extendedAuthenticationKeys.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedAuthenticationKeysDefinitionType }
     *     
     */
    public ExtendedAuthenticationKeysDefinitionType getExtendedAuthenticationKeys() {
        return extendedAuthenticationKeys;
    }

    /**
     * Définit la valeur de la propriété extendedAuthenticationKeys.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedAuthenticationKeysDefinitionType }
     *     
     */
    public void setExtendedAuthenticationKeys(ExtendedAuthenticationKeysDefinitionType value) {
        this.extendedAuthenticationKeys = value;
    }

    /**
     * Gets the value of the electionInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the electionInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElectionInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElectionInformationType }
     * 
     * 
     */
    public List<ElectionInformationType> getElectionInformation() {
        if (electionInformation == null) {
            electionInformation = new ArrayList<ElectionInformationType>();
        }
        return this.electionInformation;
    }

    /**
     * Gets the value of the voteInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the voteInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVoteInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VoteInformationType }
     * 
     * 
     */
    public List<VoteInformationType> getVoteInformation() {
        if (voteInformation == null) {
            voteInformation = new ArrayList<VoteInformationType>();
        }
        return this.voteInformation;
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

    public ContestType withContestIdentification(String value) {
        setContestIdentification(value);
        return this;
    }

    public ContestType withContestDefaultLanguage(LanguageType value) {
        setContestDefaultLanguage(value);
        return this;
    }

    public ContestType withContestDate(XMLGregorianCalendar value) {
        setContestDate(value);
        return this;
    }

    public ContestType withContestDescription(ContestDescriptionInformationType value) {
        setContestDescription(value);
        return this;
    }

    public ContestType withEvotingFromDate(XMLGregorianCalendar value) {
        setEvotingFromDate(value);
        return this;
    }

    public ContestType withEvotingToDate(XMLGregorianCalendar value) {
        setEvotingToDate(value);
        return this;
    }

    public ContestType withElectoralAuthority(ElectoralAuthorityType value) {
        setElectoralAuthority(value);
        return this;
    }

    public ContestType withExtendedAuthenticationKeys(ExtendedAuthenticationKeysDefinitionType value) {
        setExtendedAuthenticationKeys(value);
        return this;
    }

    public ContestType withElectionInformation(ElectionInformationType... values) {
        if (values!= null) {
            for (ElectionInformationType value: values) {
                getElectionInformation().add(value);
            }
        }
        return this;
    }

    public ContestType withElectionInformation(Collection<ElectionInformationType> values) {
        if (values!= null) {
            getElectionInformation().addAll(values);
        }
        return this;
    }

    public ContestType withVoteInformation(VoteInformationType... values) {
        if (values!= null) {
            for (VoteInformationType value: values) {
                getVoteInformation().add(value);
            }
        }
        return this;
    }

    public ContestType withVoteInformation(Collection<VoteInformationType> values) {
        if (values!= null) {
            getVoteInformation().addAll(values);
        }
        return this;
    }

    public ContestType withUiProperties(UiPropertiesType value) {
        setUiProperties(value);
        return this;
    }

    public void setElectionInformation(List<ElectionInformationType> value) {
        this.electionInformation = null;
        if (value!= null) {
            List<ElectionInformationType> draftl = this.getElectionInformation();
            draftl.addAll(value);
        }
    }

    public void setVoteInformation(List<VoteInformationType> value) {
        this.voteInformation = null;
        if (value!= null) {
            List<VoteInformationType> draftl = this.getVoteInformation();
            draftl.addAll(value);
        }
    }

}
