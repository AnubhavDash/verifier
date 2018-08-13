
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour listUnionDescriptionInfoType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listUnionDescriptionInfoType">
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
 *         &lt;element name="listUnionDescription">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Listenverbindung Musiker, Mathematiker, Wissenschafter"/>
 *               &lt;enumeration value="Listes apparentées Musiker, Mathematiker, Wissenschafter"/>
 *               &lt;enumeration value="Lista apparentmento Musiker, Mathematiker, Wissenschafter"/>
 *               &lt;enumeration value="Colliaziun da glistas Musiker, Mathematiker, Wissenschafter"/>
 *               &lt;enumeration value="Unterlistenverbindung Mathematiker, Wissenschafter"/>
 *               &lt;enumeration value="Listes sous-apparentées Mathematiker, Wissenschafter"/>
 *               &lt;enumeration value="Sottocongiunzione di liste Mathematiker, Wissenschafter"/>
 *               &lt;enumeration value="Sutcolliaziun da glistas Mathematiker, Wissenschafter"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listUnionDescriptionInfoType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "language",
    "listUnionDescription"
})
public class ListUnionDescriptionInfoType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String language;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String listUnionDescription;

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
     * Obtient la valeur de la propriété listUnionDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListUnionDescription() {
        return listUnionDescription;
    }

    /**
     * Définit la valeur de la propriété listUnionDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListUnionDescription(String value) {
        this.listUnionDescription = value;
    }

}
