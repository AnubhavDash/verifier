
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour candidateType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="candidateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="candidateIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mrMrs" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="familyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="callName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="candidateText" type="{http://www.evoting.ch/xmlns/config/3}candidateTextType"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sex">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="incumbent">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="true"/>
 *               &lt;enumeration value="false"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="dwellingAddress" type="{http://www.evoting.ch/xmlns/config/3}dwellingAddressType"/>
 *         &lt;element name="swiss" type="{http://www.evoting.ch/xmlns/config/3}swissType"/>
 *         &lt;element name="occupationalTitle" type="{http://www.evoting.ch/xmlns/config/3}occupationalTitleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "candidateType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "candidateIdentification",
    "mrMrs",
    "familyName",
    "firstName",
    "callName",
    "candidateText",
    "dateOfBirth",
    "sex",
    "incumbent",
    "dwellingAddress",
    "swiss",
    "occupationalTitle"
})
public class CandidateType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String candidateIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected String mrMrs;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String familyName;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String firstName;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String callName;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected CandidateTextType candidateText;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String dateOfBirth;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String sex;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String incumbent;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected DwellingAddressType dwellingAddress;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected SwissType swiss;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected OccupationalTitleType occupationalTitle;

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
     * Obtient la valeur de la propriété mrMrs.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMrMrs() {
        return mrMrs;
    }

    /**
     * Définit la valeur de la propriété mrMrs.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMrMrs(String value) {
        this.mrMrs = value;
    }

    /**
     * Obtient la valeur de la propriété familyName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Définit la valeur de la propriété familyName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyName(String value) {
        this.familyName = value;
    }

    /**
     * Obtient la valeur de la propriété firstName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Définit la valeur de la propriété firstName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Obtient la valeur de la propriété callName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallName() {
        return callName;
    }

    /**
     * Définit la valeur de la propriété callName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallName(String value) {
        this.callName = value;
    }

    /**
     * Obtient la valeur de la propriété candidateText.
     * 
     * @return
     *     possible object is
     *     {@link CandidateTextType }
     *     
     */
    public CandidateTextType getCandidateText() {
        return candidateText;
    }

    /**
     * Définit la valeur de la propriété candidateText.
     * 
     * @param value
     *     allowed object is
     *     {@link CandidateTextType }
     *     
     */
    public void setCandidateText(CandidateTextType value) {
        this.candidateText = value;
    }

    /**
     * Obtient la valeur de la propriété dateOfBirth.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Définit la valeur de la propriété dateOfBirth.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateOfBirth(String value) {
        this.dateOfBirth = value;
    }

    /**
     * Obtient la valeur de la propriété sex.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSex() {
        return sex;
    }

    /**
     * Définit la valeur de la propriété sex.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSex(String value) {
        this.sex = value;
    }

    /**
     * Obtient la valeur de la propriété incumbent.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncumbent() {
        return incumbent;
    }

    /**
     * Définit la valeur de la propriété incumbent.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncumbent(String value) {
        this.incumbent = value;
    }

    /**
     * Obtient la valeur de la propriété dwellingAddress.
     * 
     * @return
     *     possible object is
     *     {@link DwellingAddressType }
     *     
     */
    public DwellingAddressType getDwellingAddress() {
        return dwellingAddress;
    }

    /**
     * Définit la valeur de la propriété dwellingAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link DwellingAddressType }
     *     
     */
    public void setDwellingAddress(DwellingAddressType value) {
        this.dwellingAddress = value;
    }

    /**
     * Obtient la valeur de la propriété swiss.
     * 
     * @return
     *     possible object is
     *     {@link SwissType }
     *     
     */
    public SwissType getSwiss() {
        return swiss;
    }

    /**
     * Définit la valeur de la propriété swiss.
     * 
     * @param value
     *     allowed object is
     *     {@link SwissType }
     *     
     */
    public void setSwiss(SwissType value) {
        this.swiss = value;
    }

    /**
     * Obtient la valeur de la propriété occupationalTitle.
     * 
     * @return
     *     possible object is
     *     {@link OccupationalTitleType }
     *     
     */
    public OccupationalTitleType getOccupationalTitle() {
        return occupationalTitle;
    }

    /**
     * Définit la valeur de la propriété occupationalTitle.
     * 
     * @param value
     *     allowed object is
     *     {@link OccupationalTitleType }
     *     
     */
    public void setOccupationalTitle(OccupationalTitleType value) {
        this.occupationalTitle = value;
    }

}
