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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour physicalAddressType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="physicalAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="mrMrs" type="{http://www.evoting.ch/xmlns/config/4}mrMrsType" minOccurs="0"/&gt;
 *         &lt;element name="title" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="firstName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="lastName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="street" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="60"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="houseNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="12"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dwellingNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="postOfficeBoxText" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="postOfficeBoxNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedInt"&gt;
 *               &lt;maxInclusive value="99999999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="zipCode" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="town" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="country" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="2"/&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="belowTitleLine" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="belowNameLine" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="belowStreetLine" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="belowPostOfficeBoxLine" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="belowTownLine" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="belowCountryLine" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="frankingArea" type="{http://www.evoting.ch/xmlns/config/4}frankingAreaType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "physicalAddressType", propOrder = {
    "mrMrs",
    "title",
    "firstName",
    "lastName",
    "street",
    "houseNumber",
    "dwellingNumber",
    "postOfficeBoxText",
    "postOfficeBoxNumber",
    "zipCode",
    "town",
    "country",
    "belowTitleLine",
    "belowNameLine",
    "belowStreetLine",
    "belowPostOfficeBoxLine",
    "belowTownLine",
    "belowCountryLine",
    "frankingArea"
})
public class PhysicalAddressType {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String mrMrs;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String title;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String firstName;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String lastName;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String street;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String houseNumber;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dwellingNumber;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String postOfficeBoxText;
    protected Long postOfficeBoxNumber;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String zipCode;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String town;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String country;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> belowTitleLine;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> belowNameLine;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> belowStreetLine;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> belowPostOfficeBoxLine;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> belowTownLine;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> belowCountryLine;
    @XmlSchemaType(name = "string")
    protected FrankingAreaType frankingArea;

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
     * Obtient la valeur de la propriété lastName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Définit la valeur de la propriété lastName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Obtient la valeur de la propriété street.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Définit la valeur de la propriété street.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Obtient la valeur de la propriété houseNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * Définit la valeur de la propriété houseNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHouseNumber(String value) {
        this.houseNumber = value;
    }

    /**
     * Obtient la valeur de la propriété dwellingNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDwellingNumber() {
        return dwellingNumber;
    }

    /**
     * Définit la valeur de la propriété dwellingNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDwellingNumber(String value) {
        this.dwellingNumber = value;
    }

    /**
     * Obtient la valeur de la propriété postOfficeBoxText.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostOfficeBoxText() {
        return postOfficeBoxText;
    }

    /**
     * Définit la valeur de la propriété postOfficeBoxText.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostOfficeBoxText(String value) {
        this.postOfficeBoxText = value;
    }

    /**
     * Obtient la valeur de la propriété postOfficeBoxNumber.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPostOfficeBoxNumber() {
        return postOfficeBoxNumber;
    }

    /**
     * Définit la valeur de la propriété postOfficeBoxNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPostOfficeBoxNumber(Long value) {
        this.postOfficeBoxNumber = value;
    }

    /**
     * Obtient la valeur de la propriété zipCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Définit la valeur de la propriété zipCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZipCode(String value) {
        this.zipCode = value;
    }

    /**
     * Obtient la valeur de la propriété town.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTown() {
        return town;
    }

    /**
     * Définit la valeur de la propriété town.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTown(String value) {
        this.town = value;
    }

    /**
     * Obtient la valeur de la propriété country.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Définit la valeur de la propriété country.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the belowTitleLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the belowTitleLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBelowTitleLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBelowTitleLine() {
        if (belowTitleLine == null) {
            belowTitleLine = new ArrayList<String>();
        }
        return this.belowTitleLine;
    }

    /**
     * Gets the value of the belowNameLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the belowNameLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBelowNameLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBelowNameLine() {
        if (belowNameLine == null) {
            belowNameLine = new ArrayList<String>();
        }
        return this.belowNameLine;
    }

    /**
     * Gets the value of the belowStreetLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the belowStreetLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBelowStreetLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBelowStreetLine() {
        if (belowStreetLine == null) {
            belowStreetLine = new ArrayList<String>();
        }
        return this.belowStreetLine;
    }

    /**
     * Gets the value of the belowPostOfficeBoxLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the belowPostOfficeBoxLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBelowPostOfficeBoxLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBelowPostOfficeBoxLine() {
        if (belowPostOfficeBoxLine == null) {
            belowPostOfficeBoxLine = new ArrayList<String>();
        }
        return this.belowPostOfficeBoxLine;
    }

    /**
     * Gets the value of the belowTownLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the belowTownLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBelowTownLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBelowTownLine() {
        if (belowTownLine == null) {
            belowTownLine = new ArrayList<String>();
        }
        return this.belowTownLine;
    }

    /**
     * Gets the value of the belowCountryLine property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the belowCountryLine property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBelowCountryLine().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getBelowCountryLine() {
        if (belowCountryLine == null) {
            belowCountryLine = new ArrayList<String>();
        }
        return this.belowCountryLine;
    }

    /**
     * Obtient la valeur de la propriété frankingArea.
     * 
     * @return
     *     possible object is
     *     {@link FrankingAreaType }
     *     
     */
    public FrankingAreaType getFrankingArea() {
        return frankingArea;
    }

    /**
     * Définit la valeur de la propriété frankingArea.
     * 
     * @param value
     *     allowed object is
     *     {@link FrankingAreaType }
     *     
     */
    public void setFrankingArea(FrankingAreaType value) {
        this.frankingArea = value;
    }

    public PhysicalAddressType withMrMrs(String value) {
        setMrMrs(value);
        return this;
    }

    public PhysicalAddressType withTitle(String value) {
        setTitle(value);
        return this;
    }

    public PhysicalAddressType withFirstName(String value) {
        setFirstName(value);
        return this;
    }

    public PhysicalAddressType withLastName(String value) {
        setLastName(value);
        return this;
    }

    public PhysicalAddressType withStreet(String value) {
        setStreet(value);
        return this;
    }

    public PhysicalAddressType withHouseNumber(String value) {
        setHouseNumber(value);
        return this;
    }

    public PhysicalAddressType withDwellingNumber(String value) {
        setDwellingNumber(value);
        return this;
    }

    public PhysicalAddressType withPostOfficeBoxText(String value) {
        setPostOfficeBoxText(value);
        return this;
    }

    public PhysicalAddressType withPostOfficeBoxNumber(Long value) {
        setPostOfficeBoxNumber(value);
        return this;
    }

    public PhysicalAddressType withZipCode(String value) {
        setZipCode(value);
        return this;
    }

    public PhysicalAddressType withTown(String value) {
        setTown(value);
        return this;
    }

    public PhysicalAddressType withCountry(String value) {
        setCountry(value);
        return this;
    }

    public PhysicalAddressType withBelowTitleLine(String... values) {
        if (values!= null) {
            for (String value: values) {
                getBelowTitleLine().add(value);
            }
        }
        return this;
    }

    public PhysicalAddressType withBelowTitleLine(Collection<String> values) {
        if (values!= null) {
            getBelowTitleLine().addAll(values);
        }
        return this;
    }

    public PhysicalAddressType withBelowNameLine(String... values) {
        if (values!= null) {
            for (String value: values) {
                getBelowNameLine().add(value);
            }
        }
        return this;
    }

    public PhysicalAddressType withBelowNameLine(Collection<String> values) {
        if (values!= null) {
            getBelowNameLine().addAll(values);
        }
        return this;
    }

    public PhysicalAddressType withBelowStreetLine(String... values) {
        if (values!= null) {
            for (String value: values) {
                getBelowStreetLine().add(value);
            }
        }
        return this;
    }

    public PhysicalAddressType withBelowStreetLine(Collection<String> values) {
        if (values!= null) {
            getBelowStreetLine().addAll(values);
        }
        return this;
    }

    public PhysicalAddressType withBelowPostOfficeBoxLine(String... values) {
        if (values!= null) {
            for (String value: values) {
                getBelowPostOfficeBoxLine().add(value);
            }
        }
        return this;
    }

    public PhysicalAddressType withBelowPostOfficeBoxLine(Collection<String> values) {
        if (values!= null) {
            getBelowPostOfficeBoxLine().addAll(values);
        }
        return this;
    }

    public PhysicalAddressType withBelowTownLine(String... values) {
        if (values!= null) {
            for (String value: values) {
                getBelowTownLine().add(value);
            }
        }
        return this;
    }

    public PhysicalAddressType withBelowTownLine(Collection<String> values) {
        if (values!= null) {
            getBelowTownLine().addAll(values);
        }
        return this;
    }

    public PhysicalAddressType withBelowCountryLine(String... values) {
        if (values!= null) {
            for (String value: values) {
                getBelowCountryLine().add(value);
            }
        }
        return this;
    }

    public PhysicalAddressType withBelowCountryLine(Collection<String> values) {
        if (values!= null) {
            getBelowCountryLine().addAll(values);
        }
        return this;
    }

    public PhysicalAddressType withFrankingArea(FrankingAreaType value) {
        setFrankingArea(value);
        return this;
    }

    public void setBelowTitleLine(List<String> value) {
        this.belowTitleLine = null;
        if (value!= null) {
            List<String> draftl = this.getBelowTitleLine();
            draftl.addAll(value);
        }
    }

    public void setBelowNameLine(List<String> value) {
        this.belowNameLine = null;
        if (value!= null) {
            List<String> draftl = this.getBelowNameLine();
            draftl.addAll(value);
        }
    }

    public void setBelowStreetLine(List<String> value) {
        this.belowStreetLine = null;
        if (value!= null) {
            List<String> draftl = this.getBelowStreetLine();
            draftl.addAll(value);
        }
    }

    public void setBelowPostOfficeBoxLine(List<String> value) {
        this.belowPostOfficeBoxLine = null;
        if (value!= null) {
            List<String> draftl = this.getBelowPostOfficeBoxLine();
            draftl.addAll(value);
        }
    }

    public void setBelowTownLine(List<String> value) {
        this.belowTownLine = null;
        if (value!= null) {
            List<String> draftl = this.getBelowTownLine();
            draftl.addAll(value);
        }
    }

    public void setBelowCountryLine(List<String> value) {
        this.belowCountryLine = null;
        if (value!= null) {
            List<String> draftl = this.getBelowCountryLine();
            draftl.addAll(value);
        }
    }

}
