
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour contestType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="contestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contestIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contestDefaultLanguage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contestDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contestDescription" type="{http://www.evoting.ch/xmlns/config/3}contestDescriptionType"/>
 *         &lt;element name="evotingFromDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="evotingToDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extendedAuthenticationKeys" type="{http://www.evoting.ch/xmlns/config/3}extendedAuthenticationKeysType"/>
 *         &lt;element name="electionInformation" type="{http://www.evoting.ch/xmlns/config/3}electionInformationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="voteInformation" type="{http://www.evoting.ch/xmlns/config/3}voteInformationType"/>
 *         &lt;element name="uiProperties" type="{http://www.evoting.ch/xmlns/config/3}uiPropertiesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contestType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "contestIdentification",
    "contestDefaultLanguage",
    "contestDate",
    "contestDescription",
    "evotingFromDate",
    "evotingToDate",
    "extendedAuthenticationKeys",
    "electionInformation",
    "voteInformation",
    "uiProperties"
})
public class ContestType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String contestIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String contestDefaultLanguage;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String contestDate;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ContestDescriptionType contestDescription;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String evotingFromDate;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String evotingToDate;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ExtendedAuthenticationKeysType extendedAuthenticationKeys;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<ElectionInformationType> electionInformation;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected VoteInformationType voteInformation;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
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
     *     {@link String }
     *     
     */
    public String getContestDefaultLanguage() {
        return contestDefaultLanguage;
    }

    /**
     * Définit la valeur de la propriété contestDefaultLanguage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContestDefaultLanguage(String value) {
        this.contestDefaultLanguage = value;
    }

    /**
     * Obtient la valeur de la propriété contestDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContestDate() {
        return contestDate;
    }

    /**
     * Définit la valeur de la propriété contestDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContestDate(String value) {
        this.contestDate = value;
    }

    /**
     * Obtient la valeur de la propriété contestDescription.
     * 
     * @return
     *     possible object is
     *     {@link ContestDescriptionType }
     *     
     */
    public ContestDescriptionType getContestDescription() {
        return contestDescription;
    }

    /**
     * Définit la valeur de la propriété contestDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ContestDescriptionType }
     *     
     */
    public void setContestDescription(ContestDescriptionType value) {
        this.contestDescription = value;
    }

    /**
     * Obtient la valeur de la propriété evotingFromDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvotingFromDate() {
        return evotingFromDate;
    }

    /**
     * Définit la valeur de la propriété evotingFromDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvotingFromDate(String value) {
        this.evotingFromDate = value;
    }

    /**
     * Obtient la valeur de la propriété evotingToDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvotingToDate() {
        return evotingToDate;
    }

    /**
     * Définit la valeur de la propriété evotingToDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvotingToDate(String value) {
        this.evotingToDate = value;
    }

    /**
     * Obtient la valeur de la propriété extendedAuthenticationKeys.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedAuthenticationKeysType }
     *     
     */
    public ExtendedAuthenticationKeysType getExtendedAuthenticationKeys() {
        return extendedAuthenticationKeys;
    }

    /**
     * Définit la valeur de la propriété extendedAuthenticationKeys.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedAuthenticationKeysType }
     *     
     */
    public void setExtendedAuthenticationKeys(ExtendedAuthenticationKeysType value) {
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
     * Obtient la valeur de la propriété voteInformation.
     * 
     * @return
     *     possible object is
     *     {@link VoteInformationType }
     *     
     */
    public VoteInformationType getVoteInformation() {
        return voteInformation;
    }

    /**
     * Définit la valeur de la propriété voteInformation.
     * 
     * @param value
     *     allowed object is
     *     {@link VoteInformationType }
     *     
     */
    public void setVoteInformation(VoteInformationType value) {
        this.voteInformation = value;
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

}
