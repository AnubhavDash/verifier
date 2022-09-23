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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour electronicAddressType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electronicAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="electronicAddressType"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *               &lt;enumeration value="3"/&gt;
 *               &lt;enumeration value="4"/&gt;
 *               &lt;enumeration value="5"/&gt;
 *               &lt;enumeration value="6"/&gt;
 *               &lt;enumeration value="7"/&gt;
 *               &lt;enumeration value="8"/&gt;
 *               &lt;enumeration value="9"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="electronicAddressValue" type="{http://www.w3.org/2001/XMLSchema}token"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "electronicAddressType", propOrder = {
    "electronicAddressType",
    "electronicAddressValue"
})
public class ElectronicAddressType {

    protected int electronicAddressType;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String electronicAddressValue;

    /**
     * Obtient la valeur de la propriété electronicAddressType.
     * 
     */
    public int getElectronicAddressType() {
        return electronicAddressType;
    }

    /**
     * Définit la valeur de la propriété electronicAddressType.
     * 
     */
    public void setElectronicAddressType(int value) {
        this.electronicAddressType = value;
    }

    /**
     * Obtient la valeur de la propriété electronicAddressValue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElectronicAddressValue() {
        return electronicAddressValue;
    }

    /**
     * Définit la valeur de la propriété electronicAddressValue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElectronicAddressValue(String value) {
        this.electronicAddressValue = value;
    }

    public ElectronicAddressType withElectronicAddressType(int value) {
        setElectronicAddressType(value);
        return this;
    }

    public ElectronicAddressType withElectronicAddressValue(String value) {
        setElectronicAddressValue(value);
        return this;
    }

}
