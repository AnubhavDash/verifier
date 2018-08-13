
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour contestDescriptionInfoType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="contestDescriptionInfoType">
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
 *         &lt;element name="contestDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contestDescriptionInfoType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "language",
    "contestDescription"
})
public class ContestDescriptionInfoType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String language;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String contestDescription;

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
     * Obtient la valeur de la propriété contestDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContestDescription() {
        return contestDescription;
    }

    /**
     * Définit la valeur de la propriété contestDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContestDescription(String value) {
        this.contestDescription = value;
    }

}
