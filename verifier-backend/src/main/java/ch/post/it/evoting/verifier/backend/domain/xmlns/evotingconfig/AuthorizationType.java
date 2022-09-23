//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import java.math.BigInteger;
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
 * <p>Classe Java pour authorizationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="authorizationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="authorizationIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="authorizationName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="authorizationAlias"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="authorizationTest" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="authorizationFromDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="authorizationToDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="authorizationGracePeriod" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="authorizationObject" type="{http://www.evoting.ch/xmlns/config/4}authorizationObjectType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "authorizationType", propOrder = {
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

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String authorizationIdentification;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String authorizationName;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String authorizationAlias;
    protected boolean authorizationTest;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar authorizationFromDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar authorizationToDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger authorizationGracePeriod;
    @XmlElement(required = true)
    protected List<AuthorizationObjectType> authorizationObject;

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
     */
    public boolean isAuthorizationTest() {
        return authorizationTest;
    }

    /**
     * Définit la valeur de la propriété authorizationTest.
     * 
     */
    public void setAuthorizationTest(boolean value) {
        this.authorizationTest = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationFromDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAuthorizationFromDate() {
        return authorizationFromDate;
    }

    /**
     * Définit la valeur de la propriété authorizationFromDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAuthorizationFromDate(XMLGregorianCalendar value) {
        this.authorizationFromDate = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationToDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAuthorizationToDate() {
        return authorizationToDate;
    }

    /**
     * Définit la valeur de la propriété authorizationToDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAuthorizationToDate(XMLGregorianCalendar value) {
        this.authorizationToDate = value;
    }

    /**
     * Obtient la valeur de la propriété authorizationGracePeriod.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAuthorizationGracePeriod() {
        return authorizationGracePeriod;
    }

    /**
     * Définit la valeur de la propriété authorizationGracePeriod.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAuthorizationGracePeriod(BigInteger value) {
        this.authorizationGracePeriod = value;
    }

    /**
     * Gets the value of the authorizationObject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authorizationObject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthorizationObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthorizationObjectType }
     * 
     * 
     */
    public List<AuthorizationObjectType> getAuthorizationObject() {
        if (authorizationObject == null) {
            authorizationObject = new ArrayList<AuthorizationObjectType>();
        }
        return this.authorizationObject;
    }

    public AuthorizationType withAuthorizationIdentification(String value) {
        setAuthorizationIdentification(value);
        return this;
    }

    public AuthorizationType withAuthorizationName(String value) {
        setAuthorizationName(value);
        return this;
    }

    public AuthorizationType withAuthorizationAlias(String value) {
        setAuthorizationAlias(value);
        return this;
    }

    public AuthorizationType withAuthorizationTest(boolean value) {
        setAuthorizationTest(value);
        return this;
    }

    public AuthorizationType withAuthorizationFromDate(XMLGregorianCalendar value) {
        setAuthorizationFromDate(value);
        return this;
    }

    public AuthorizationType withAuthorizationToDate(XMLGregorianCalendar value) {
        setAuthorizationToDate(value);
        return this;
    }

    public AuthorizationType withAuthorizationGracePeriod(BigInteger value) {
        setAuthorizationGracePeriod(value);
        return this;
    }

    public AuthorizationType withAuthorizationObject(AuthorizationObjectType... values) {
        if (values!= null) {
            for (AuthorizationObjectType value: values) {
                getAuthorizationObject().add(value);
            }
        }
        return this;
    }

    public AuthorizationType withAuthorizationObject(Collection<AuthorizationObjectType> values) {
        if (values!= null) {
            getAuthorizationObject().addAll(values);
        }
        return this;
    }

    public void setAuthorizationObject(List<AuthorizationObjectType> value) {
        this.authorizationObject = null;
        if (value!= null) {
            List<AuthorizationObjectType> draftl = this.getAuthorizationObject();
            draftl.addAll(value);
        }
    }

}
