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
 * <p>Classe Java pour voterType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="voterType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="voterIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="authorization" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="extendedAuthenticationKeys" type="{http://www.evoting.ch/xmlns/config/4}extendedAuthenticationKeysType" minOccurs="0"/&gt;
 *         &lt;element name="sex" type="{http://www.evoting.ch/xmlns/config/4}sexType"/&gt;
 *         &lt;element name="voterType" type="{http://www.evoting.ch/xmlns/config/4}voterTypeType"/&gt;
 *         &lt;element name="person" type="{http://www.evoting.ch/xmlns/config/4}personType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voterType", propOrder = {
    "voterIdentification",
    "authorization",
    "extendedAuthenticationKeys",
    "sex",
    "voterType",
    "person"
})
public class VoterType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String voterIdentification;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String authorization;
    protected ExtendedAuthenticationKeysType extendedAuthenticationKeys;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected SexType sex;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected VoterTypeType voterType;
    protected PersonType person;

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
     *     {@link SexType }
     *     
     */
    public SexType getSex() {
        return sex;
    }

    /**
     * Définit la valeur de la propriété sex.
     * 
     * @param value
     *     allowed object is
     *     {@link SexType }
     *     
     */
    public void setSex(SexType value) {
        this.sex = value;
    }

    /**
     * Obtient la valeur de la propriété voterType.
     * 
     * @return
     *     possible object is
     *     {@link VoterTypeType }
     *     
     */
    public VoterTypeType getVoterType() {
        return voterType;
    }

    /**
     * Définit la valeur de la propriété voterType.
     * 
     * @param value
     *     allowed object is
     *     {@link VoterTypeType }
     *     
     */
    public void setVoterType(VoterTypeType value) {
        this.voterType = value;
    }

    /**
     * Obtient la valeur de la propriété person.
     * 
     * @return
     *     possible object is
     *     {@link PersonType }
     *     
     */
    public PersonType getPerson() {
        return person;
    }

    /**
     * Définit la valeur de la propriété person.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType }
     *     
     */
    public void setPerson(PersonType value) {
        this.person = value;
    }

    public VoterType withVoterIdentification(String value) {
        setVoterIdentification(value);
        return this;
    }

    public VoterType withAuthorization(String value) {
        setAuthorization(value);
        return this;
    }

    public VoterType withExtendedAuthenticationKeys(ExtendedAuthenticationKeysType value) {
        setExtendedAuthenticationKeys(value);
        return this;
    }

    public VoterType withSex(SexType value) {
        setSex(value);
        return this;
    }

    public VoterType withVoterType(VoterTypeType value) {
        setVoterType(value);
        return this;
    }

    public VoterType withPerson(PersonType value) {
        setPerson(value);
        return this;
    }

}
