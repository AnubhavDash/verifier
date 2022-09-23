//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour dwellingAddressType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="dwellingAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;sequence minOccurs="0"&gt;
 *           &lt;element name="street"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                 &lt;minLength value="0"/&gt;
 *                 &lt;maxLength value="60"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="houseNumber" minOccurs="0"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                 &lt;maxLength value="12"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *         &lt;/sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="swissZipCode"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedInt"&gt;
 *                 &lt;minInclusive value="1000"/&gt;
 *                 &lt;maxInclusive value="9999"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="foreignZipCode" minOccurs="0"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                 &lt;maxLength value="15"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="town"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="40"/&gt;
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
@XmlType(name = "dwellingAddressType", propOrder = {
    "street",
    "houseNumber",
    "swissZipCode",
    "foreignZipCode",
    "town"
})
public class DwellingAddressType {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String street;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String houseNumber;
    protected Long swissZipCode;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String foreignZipCode;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String town;

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
     * Obtient la valeur de la propriété swissZipCode.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSwissZipCode() {
        return swissZipCode;
    }

    /**
     * Définit la valeur de la propriété swissZipCode.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSwissZipCode(Long value) {
        this.swissZipCode = value;
    }

    /**
     * Obtient la valeur de la propriété foreignZipCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignZipCode() {
        return foreignZipCode;
    }

    /**
     * Définit la valeur de la propriété foreignZipCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignZipCode(String value) {
        this.foreignZipCode = value;
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

    public DwellingAddressType withStreet(String value) {
        setStreet(value);
        return this;
    }

    public DwellingAddressType withHouseNumber(String value) {
        setHouseNumber(value);
        return this;
    }

    public DwellingAddressType withSwissZipCode(Long value) {
        setSwissZipCode(value);
        return this;
    }

    public DwellingAddressType withForeignZipCode(String value) {
        setForeignZipCode(value);
        return this;
    }

    public DwellingAddressType withTown(String value) {
        setTown(value);
        return this;
    }

}
