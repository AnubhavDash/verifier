
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour configurationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="configurationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="header" type="{http://www.evoting.ch/xmlns/config/3}headerType"/>
 *         &lt;element name="contest" type="{http://www.evoting.ch/xmlns/config/3}contestType"/>
 *         &lt;element name="authorizations" type="{http://www.evoting.ch/xmlns/config/3}authorizationsType"/>
 *         &lt;element name="register" type="{http://www.evoting.ch/xmlns/config/3}registerType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurationType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "header",
    "contest",
    "authorizations",
    "register"
})
public class ConfigurationType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected HeaderType header;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ContestType contest;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected AuthorizationsType authorizations;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected RegisterType register;

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

}
