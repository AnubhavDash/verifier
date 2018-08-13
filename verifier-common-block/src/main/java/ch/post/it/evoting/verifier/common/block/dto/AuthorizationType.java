
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour authorizationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="authorizationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authorizationIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationAlias" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationTest" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationFromDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationToDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationGracePeriod" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationObject" type="{http://www.evoting.ch/xmlns/config/3}authorizationObjectType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "authorizationType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "authorizationIdentification",
    "authorizationName",
    "authorizationAlias",
    "authorizationTest",
    "authorizationFromDate",
    "authorizationToDate",
    "authorizationGracePeriod",
    "authorizationObject"
})
public class AuthorizationType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorizationIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorizationName;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorizationAlias;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorizationTest;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorizationFromDate;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorizationToDate;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String authorizationGracePeriod;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected AuthorizationObjectType authorizationObject;

    /**
     * Obtient la valeur de la propriété authorizationIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationIdentification() {
        return authorizationIdentification;
    }

    /**
     * Définit la valeur de la propriété authorizationIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationIdentification(String value) {
        this.authorizationIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationName() {
        return authorizationName;
    }

    /**
     * Définit la valeur de la propriété authorizationName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationName(String value) {
        this.authorizationName = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationAlias.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationAlias() {
        return authorizationAlias;
    }

    /**
     * Définit la valeur de la propriété authorizationAlias.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationAlias(String value) {
        this.authorizationAlias = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationTest.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationTest() {
        return authorizationTest;
    }

    /**
     * Définit la valeur de la propriété authorizationTest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationTest(String value) {
        this.authorizationTest = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationFromDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationFromDate() {
        return authorizationFromDate;
    }

    /**
     * Définit la valeur de la propriété authorizationFromDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationFromDate(String value) {
        this.authorizationFromDate = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationToDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationToDate() {
        return authorizationToDate;
    }

    /**
     * Définit la valeur de la propriété authorizationToDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationToDate(String value) {
        this.authorizationToDate = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationGracePeriod.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationGracePeriod() {
        return authorizationGracePeriod;
    }

    /**
     * Définit la valeur de la propriété authorizationGracePeriod.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationGracePeriod(String value) {
        this.authorizationGracePeriod = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationObject.
     * 
     * @return
     *     possible object is
     *     {@link AuthorizationObjectType }
     *     
     */
    public AuthorizationObjectType getAuthorizationObject() {
        return authorizationObject;
    }

    /**
     * Définit la valeur de la propriété authorizationObject.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorizationObjectType }
     *     
     */
    public void setAuthorizationObject(AuthorizationObjectType value) {
        this.authorizationObject = value;
    }

}
