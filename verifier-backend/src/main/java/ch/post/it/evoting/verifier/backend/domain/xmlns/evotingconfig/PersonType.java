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
 * <p>Classe Java pour personType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="personType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="officialName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="firstName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="sex"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="languageOfCorrespondance" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *         &lt;element name="residenceCountryId"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="municipality"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="municipalityId"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"&gt;
 *                         &lt;totalDigits value="4"/&gt;
 *                         &lt;minInclusive value="1"/&gt;
 *                         &lt;maxInclusive value="9999"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="municipalityName"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;maxLength value="50"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="physicalAddress" type="{http://www.evoting.ch/xmlns/config/4}physicalAddressType" minOccurs="0"/&gt;
 *         &lt;element name="electronicAddress" type="{http://www.evoting.ch/xmlns/config/4}electronicAddressType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType", propOrder = {
    "officialName",
    "firstName",
    "sex",
    "dateOfBirth",
    "languageOfCorrespondance",
    "residenceCountryId",
    "municipality",
    "physicalAddress",
    "electronicAddress"
})
public class PersonType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String officialName;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String firstName;
    @XmlElement(required = true)
    protected String sex;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected LanguageType languageOfCorrespondance;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String residenceCountryId;
    @XmlElement(required = true)
    protected Municipality municipality;
    protected PhysicalAddressType physicalAddress;
    protected List<ElectronicAddressType> electronicAddress;

    /**
     * Obtient la valeur de la propriété officialName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfficialName() {
        return officialName;
    }

    /**
     * Définit la valeur de la propriété officialName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfficialName(String value) {
        this.officialName = value;
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
     * Obtient la valeur de la propriété languageOfCorrespondance.
     * 
     * @return
     *     possible object is
     *     {@link LanguageType }
     *     
     */
    public LanguageType getLanguageOfCorrespondance() {
        return languageOfCorrespondance;
    }

    /**
     * Définit la valeur de la propriété languageOfCorrespondance.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageType }
     *     
     */
    public void setLanguageOfCorrespondance(LanguageType value) {
        this.languageOfCorrespondance = value;
    }

    /**
     * Obtient la valeur de la propriété residenceCountryId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResidenceCountryId() {
        return residenceCountryId;
    }

    /**
     * Définit la valeur de la propriété residenceCountryId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResidenceCountryId(String value) {
        this.residenceCountryId = value;
    }

    /**
     * Obtient la valeur de la propriété municipality.
     * 
     * @return
     *     possible object is
     *     {@link Municipality }
     *     
     */
    public Municipality getMunicipality() {
        return municipality;
    }

    /**
     * Définit la valeur de la propriété municipality.
     * 
     * @param value
     *     allowed object is
     *     {@link Municipality }
     *     
     */
    public void setMunicipality(Municipality value) {
        this.municipality = value;
    }

    /**
     * Obtient la valeur de la propriété physicalAddress.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalAddressType }
     *     
     */
    public PhysicalAddressType getPhysicalAddress() {
        return physicalAddress;
    }

    /**
     * Définit la valeur de la propriété physicalAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalAddressType }
     *     
     */
    public void setPhysicalAddress(PhysicalAddressType value) {
        this.physicalAddress = value;
    }

    /**
     * Gets the value of the electronicAddress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the electronicAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElectronicAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElectronicAddressType }
     * 
     * 
     */
    public List<ElectronicAddressType> getElectronicAddress() {
        if (electronicAddress == null) {
            electronicAddress = new ArrayList<ElectronicAddressType>();
        }
        return this.electronicAddress;
    }

    public PersonType withOfficialName(String value) {
        setOfficialName(value);
        return this;
    }

    public PersonType withFirstName(String value) {
        setFirstName(value);
        return this;
    }

    public PersonType withSex(String value) {
        setSex(value);
        return this;
    }

    public PersonType withDateOfBirth(XMLGregorianCalendar value) {
        setDateOfBirth(value);
        return this;
    }

    public PersonType withLanguageOfCorrespondance(LanguageType value) {
        setLanguageOfCorrespondance(value);
        return this;
    }

    public PersonType withResidenceCountryId(String value) {
        setResidenceCountryId(value);
        return this;
    }

    public PersonType withMunicipality(Municipality value) {
        setMunicipality(value);
        return this;
    }

    public PersonType withPhysicalAddress(PhysicalAddressType value) {
        setPhysicalAddress(value);
        return this;
    }

    public PersonType withElectronicAddress(ElectronicAddressType... values) {
        if (values!= null) {
            for (ElectronicAddressType value: values) {
                getElectronicAddress().add(value);
            }
        }
        return this;
    }

    public PersonType withElectronicAddress(Collection<ElectronicAddressType> values) {
        if (values!= null) {
            getElectronicAddress().addAll(values);
        }
        return this;
    }

    public void setElectronicAddress(List<ElectronicAddressType> value) {
        this.electronicAddress = null;
        if (value!= null) {
            List<ElectronicAddressType> draftl = this.getElectronicAddress();
            draftl.addAll(value);
        }
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
     *         &lt;element name="municipalityId"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"&gt;
     *               &lt;totalDigits value="4"/&gt;
     *               &lt;minInclusive value="1"/&gt;
     *               &lt;maxInclusive value="9999"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="municipalityName"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;maxLength value="50"/&gt;
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
        "municipalityId",
        "municipalityName"
    })
    public static class Municipality {

        protected int municipalityId;
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String municipalityName;

        /**
         * Obtient la valeur de la propriété municipalityId.
         * 
         */
        public int getMunicipalityId() {
            return municipalityId;
        }

        /**
         * Définit la valeur de la propriété municipalityId.
         * 
         */
        public void setMunicipalityId(int value) {
            this.municipalityId = value;
        }

        /**
         * Obtient la valeur de la propriété municipalityName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMunicipalityName() {
            return municipalityName;
        }

        /**
         * Définit la valeur de la propriété municipalityName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMunicipalityName(String value) {
            this.municipalityName = value;
        }

        public Municipality withMunicipalityId(int value) {
            setMunicipalityId(value);
            return this;
        }

        public Municipality withMunicipalityName(String value) {
            setMunicipalityName(value);
            return this;
        }

    }

}
