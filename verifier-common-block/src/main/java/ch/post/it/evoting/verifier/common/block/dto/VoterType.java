
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour voterType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="voterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="voterIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorization" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extendedAuthenticationKeys" type="{http://www.evoting.ch/xmlns/config/3}extendedAuthenticationKeysType"/>
 *         &lt;element name="sex">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="MALE"/>
 *               &lt;enumeration value="FEMALE"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="voterType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voterType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "voterIdentification",
    "authorization",
    "extendedAuthenticationKeys",
    "sex",
    "voterType"
})
public class VoterType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String voterIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorization;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ExtendedAuthenticationKeysType extendedAuthenticationKeys;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String sex;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String voterType;

    /**
     * Obtient la valeur de la propriété voterIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoterIdentification() {
        return voterIdentification;
    }

    /**
     * Définit la valeur de la propriété voterIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoterIdentification(String value) {
        this.voterIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété authorization.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorization() {
        return authorization;
    }

    /**
     * Définit la valeur de la propriété authorization.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorization(String value) {
        this.authorization = value;
    }

    /**
     * Obtient la valeur de la propriété extendedAuthenticationKeys.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedAuthenticationKeysType }
     *     
     */
    public ExtendedAuthenticationKeysType getExtendedAuthenticationKeys() {
        return extendedAuthenticationKeys;
    }

    /**
     * Définit la valeur de la propriété extendedAuthenticationKeys.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedAuthenticationKeysType }
     *     
     */
    public void setExtendedAuthenticationKeys(ExtendedAuthenticationKeysType value) {
        this.extendedAuthenticationKeys = value;
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
     * Obtient la valeur de la propriété voterType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoterType() {
        return voterType;
    }

    /**
     * Définit la valeur de la propriété voterType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoterType(String value) {
        this.voterType = value;
    }

}
