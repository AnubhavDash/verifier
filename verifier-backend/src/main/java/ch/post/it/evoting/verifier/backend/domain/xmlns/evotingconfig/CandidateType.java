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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour candidateType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="candidateType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="candidateIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="mrMrs" type="{http://www.evoting.ch/xmlns/config/4}mrMrsType" minOccurs="0"/&gt;
 *         &lt;element name="title" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="familyName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="firstName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="callName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="candidateText" type="{http://www.evoting.ch/xmlns/config/4}candidateTextInformationType"/&gt;
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="sex"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *               &lt;enumeration value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="incumbent" type="{http://www.evoting.ch/xmlns/config/4}incumbentType"/&gt;
 *         &lt;element name="dwellingAddress" type="{http://www.evoting.ch/xmlns/config/4}dwellingAddressType" minOccurs="0"/&gt;
 *         &lt;element name="swiss"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="origin" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="80"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="occupationalTitle" type="{http://www.evoting.ch/xmlns/config/4}occupationalTitleInformationType" minOccurs="0"/&gt;
 *         &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="referenceOnPosition" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="partyAffiliation" type="{http://www.evoting.ch/xmlns/config/4}partyAffiliationformationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "candidateType", propOrder = {
    "candidateIdentification",
    "mrMrs",
    "title",
    "familyName",
    "firstName",
    "callName",
    "candidateText",
    "dateOfBirth",
    "sex",
    "incumbent",
    "dwellingAddress",
    "swiss",
    "occupationalTitle",
    "position",
    "referenceOnPosition",
    "partyAffiliation"
})
public class CandidateType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String candidateIdentification;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String mrMrs;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String title;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String familyName;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String firstName;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String callName;
    @XmlElement(required = true)
    protected CandidateTextInformationType candidateText;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(required = true)
    protected String sex;
    @XmlElement(required = true)
    protected IncumbentType incumbent;
    protected DwellingAddressType dwellingAddress;
    @XmlElement(required = true)
    protected Swiss swiss;
    protected OccupationalTitleInformationType occupationalTitle;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger position;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String referenceOnPosition;
    protected PartyAffiliationformationType partyAffiliation;

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
     * Obtient la valeur de la propriété title.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
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
     *     {@link CandidateTextInformationType }
     *     
     */
    public CandidateTextInformationType getCandidateText() {
        return candidateText;
    }

    /**
     * Définit la valeur de la propriété candidateText.
     * 
     * @param value
     *     allowed object is
     *     {@link CandidateTextInformationType }
     *     
     */
    public void setCandidateText(CandidateTextInformationType value) {
        this.candidateText = value;
    }

    /**
     * Obtient la valeur de la propriété dateOfBirth.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Définit la valeur de la propriété dateOfBirth.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
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
     *     {@link IncumbentType }
     *     
     */
    public IncumbentType getIncumbent() {
        return incumbent;
    }

    /**
     * Définit la valeur de la propriété incumbent.
     * 
     * @param value
     *     allowed object is
     *     {@link IncumbentType }
     *     
     */
    public void setIncumbent(IncumbentType value) {
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
     *     {@link Swiss }
     *     
     */
    public Swiss getSwiss() {
        return swiss;
    }

    /**
     * Définit la valeur de la propriété swiss.
     * 
     * @param value
     *     allowed object is
     *     {@link Swiss }
     *     
     */
    public void setSwiss(Swiss value) {
        this.swiss = value;
    }

    /**
     * Obtient la valeur de la propriété occupationalTitle.
     * 
     * @return
     *     possible object is
     *     {@link OccupationalTitleInformationType }
     *     
     */
    public OccupationalTitleInformationType getOccupationalTitle() {
        return occupationalTitle;
    }

    /**
     * Définit la valeur de la propriété occupationalTitle.
     * 
     * @param value
     *     allowed object is
     *     {@link OccupationalTitleInformationType }
     *     
     */
    public void setOccupationalTitle(OccupationalTitleInformationType value) {
        this.occupationalTitle = value;
    }

    /**
     * Obtient la valeur de la propriété position.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPosition() {
        return position;
    }

    /**
     * Définit la valeur de la propriété position.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPosition(BigInteger value) {
        this.position = value;
    }

    /**
     * Obtient la valeur de la propriété referenceOnPosition.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceOnPosition() {
        return referenceOnPosition;
    }

    /**
     * Définit la valeur de la propriété referenceOnPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceOnPosition(String value) {
        this.referenceOnPosition = value;
    }

    /**
     * Obtient la valeur de la propriété partyAffiliation.
     * 
     * @return
     *     possible object is
     *     {@link PartyAffiliationformationType }
     *     
     */
    public PartyAffiliationformationType getPartyAffiliation() {
        return partyAffiliation;
    }

    /**
     * Définit la valeur de la propriété partyAffiliation.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyAffiliationformationType }
     *     
     */
    public void setPartyAffiliation(PartyAffiliationformationType value) {
        this.partyAffiliation = value;
    }

    public CandidateType withCandidateIdentification(String value) {
        setCandidateIdentification(value);
        return this;
    }

    public CandidateType withMrMrs(String value) {
        setMrMrs(value);
        return this;
    }

    public CandidateType withTitle(String value) {
        setTitle(value);
        return this;
    }

    public CandidateType withFamilyName(String value) {
        setFamilyName(value);
        return this;
    }

    public CandidateType withFirstName(String value) {
        setFirstName(value);
        return this;
    }

    public CandidateType withCallName(String value) {
        setCallName(value);
        return this;
    }

    public CandidateType withCandidateText(CandidateTextInformationType value) {
        setCandidateText(value);
        return this;
    }

    public CandidateType withDateOfBirth(XMLGregorianCalendar value) {
        setDateOfBirth(value);
        return this;
    }

    public CandidateType withSex(String value) {
        setSex(value);
        return this;
    }

    public CandidateType withIncumbent(IncumbentType value) {
        setIncumbent(value);
        return this;
    }

    public CandidateType withDwellingAddress(DwellingAddressType value) {
        setDwellingAddress(value);
        return this;
    }

    public CandidateType withSwiss(Swiss value) {
        setSwiss(value);
        return this;
    }

    public CandidateType withOccupationalTitle(OccupationalTitleInformationType value) {
        setOccupationalTitle(value);
        return this;
    }

    public CandidateType withPosition(BigInteger value) {
        setPosition(value);
        return this;
    }

    public CandidateType withReferenceOnPosition(String value) {
        setReferenceOnPosition(value);
        return this;
    }

    public CandidateType withPartyAffiliation(PartyAffiliationformationType value) {
        setPartyAffiliation(value);
        return this;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="origin" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="80"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "origin"
    })
    public static class Swiss {

        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected List<String> origin;

        /**
         * Gets the value of the origin property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the origin property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOrigin().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getOrigin() {
            if (origin == null) {
                origin = new ArrayList<String>();
            }
            return this.origin;
        }

        public Swiss withOrigin(String... values) {
            if (values!= null) {
                for (String value: values) {
                    getOrigin().add(value);
                }
            }
            return this;
        }

        public Swiss withOrigin(Collection<String> values) {
            if (values!= null) {
                getOrigin().addAll(values);
            }
            return this;
        }

        public void setOrigin(List<String> value) {
            this.origin = null;
            if (value!= null) {
                List<String> draftl = this.getOrigin();
                draftl.addAll(value);
            }
        }

    }

}
