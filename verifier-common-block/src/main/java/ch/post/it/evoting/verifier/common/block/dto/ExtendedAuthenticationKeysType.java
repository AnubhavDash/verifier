
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour extendedAuthenticationKeysType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="extendedAuthenticationKeysType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extendedAuthenticationKey" type="{http://www.evoting.ch/xmlns/config/3}extendedAuthenticationKeyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedAuthenticationKeysType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "keyName",
    "extendedAuthenticationKey"
})
public class ExtendedAuthenticationKeysType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected String keyName;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected ExtendedAuthenticationKeyType extendedAuthenticationKey;

    /**
     * Obtient la valeur de la propriété keyName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Définit la valeur de la propriété keyName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyName(String value) {
        this.keyName = value;
    }

    /**
     * Obtient la valeur de la propriété extendedAuthenticationKey.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedAuthenticationKeyType }
     *     
     */
    public ExtendedAuthenticationKeyType getExtendedAuthenticationKey() {
        return extendedAuthenticationKey;
    }

    /**
     * Définit la valeur de la propriété extendedAuthenticationKey.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedAuthenticationKeyType }
     *     
     */
    public void setExtendedAuthenticationKey(ExtendedAuthenticationKeyType value) {
        this.extendedAuthenticationKey = value;
    }

}
