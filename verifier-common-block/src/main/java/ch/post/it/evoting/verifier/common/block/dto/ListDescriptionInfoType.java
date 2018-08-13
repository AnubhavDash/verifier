
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour listDescriptionInfoType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listDescriptionInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="language">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="de"/>
 *               &lt;enumeration value="fr"/>
 *               &lt;enumeration value="it"/>
 *               &lt;enumeration value="rm"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="listDescriptionShort" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="listDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listDescriptionInfoType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "language",
    "listDescriptionShort",
    "listDescription"
})
public class ListDescriptionInfoType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String language;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listDescriptionShort;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listDescription;

    /**
     * Obtient la valeur de la propriété language.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Définit la valeur de la propriété language.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Obtient la valeur de la propriété listDescriptionShort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListDescriptionShort() {
        return listDescriptionShort;
    }

    /**
     * Définit la valeur de la propriété listDescriptionShort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListDescriptionShort(String value) {
        this.listDescriptionShort = value;
    }

    /**
     * Obtient la valeur de la propriété listDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListDescription() {
        return listDescription;
    }

    /**
     * Définit la valeur de la propriété listDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListDescription(String value) {
        this.listDescription = value;
    }

}
