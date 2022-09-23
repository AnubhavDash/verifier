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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="header" type="{http://www.evoting.ch/xmlns/config/4}headerType"/&gt;
 *         &lt;element name="contest" type="{http://www.evoting.ch/xmlns/config/4}contestType"/&gt;
 *         &lt;element name="authorizations" type="{http://www.evoting.ch/xmlns/config/4}authorizationsType"/&gt;
 *         &lt;element name="register" type="{http://www.evoting.ch/xmlns/config/4}registerType"/&gt;
 *         &lt;element name="signature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "header",
    "contest",
    "authorizations",
    "register",
    "signature"
})
@XmlRootElement(name = "configuration")
public class Configuration {

    @XmlElement(required = true)
    protected HeaderType header;
    @XmlElement(required = true)
    protected ContestType contest;
    @XmlElement(required = true)
    protected AuthorizationsType authorizations;
    @XmlElement(required = true)
    protected RegisterType register;
    protected byte[] signature;

    /**
     * Obtient la valeur de la propriété header.
     * 
     * @return
     *     possible object is
     *     {@link HeaderType }
     *     
     */
    public HeaderType getHeader() {
        return header;
    }

    /**
     * Définit la valeur de la propriété header.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderType }
     *     
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Obtient la valeur de la propriété contest.
     * 
     * @return
     *     possible object is
     *     {@link ContestType }
     *     
     */
    public ContestType getContest() {
        return contest;
    }

    /**
     * Définit la valeur de la propriété contest.
     * 
     * @param value
     *     allowed object is
     *     {@link ContestType }
     *     
     */
    public void setContest(ContestType value) {
        this.contest = value;
    }

    /**
     * Obtient la valeur de la propriété authorizations.
     * 
     * @return
     *     possible object is
     *     {@link AuthorizationsType }
     *     
     */
    public AuthorizationsType getAuthorizations() {
        return authorizations;
    }

    /**
     * Définit la valeur de la propriété authorizations.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorizationsType }
     *     
     */
    public void setAuthorizations(AuthorizationsType value) {
        this.authorizations = value;
    }

    /**
     * Obtient la valeur de la propriété register.
     * 
     * @return
     *     possible object is
     *     {@link RegisterType }
     *     
     */
    public RegisterType getRegister() {
        return register;
    }

    /**
     * Définit la valeur de la propriété register.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisterType }
     *     
     */
    public void setRegister(RegisterType value) {
        this.register = value;
    }

    /**
     * Obtient la valeur de la propriété signature.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Définit la valeur de la propriété signature.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSignature(byte[] value) {
        this.signature = value;
    }

    public Configuration withHeader(HeaderType value) {
        setHeader(value);
        return this;
    }

    public Configuration withContest(ContestType value) {
        setContest(value);
        return this;
    }

    public Configuration withAuthorizations(AuthorizationsType value) {
        setAuthorizations(value);
        return this;
    }

    public Configuration withRegister(RegisterType value) {
        setRegister(value);
        return this;
    }

    public Configuration withSignature(byte[] value) {
        setSignature(value);
        return this;
    }

}
