
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour authorizationObjectType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="authorizationObjectType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="domainOfInfluence" type="{http://www.evoting.ch/xmlns/config/3}domainOfInfluenceType"/>
 *         &lt;element name="countingCircle" type="{http://www.evoting.ch/xmlns/config/3}countingCircleType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "authorizationObjectType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "domainOfInfluence",
    "countingCircle"
})
public class AuthorizationObjectType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected DomainOfInfluenceType domainOfInfluence;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected CountingCircleType countingCircle;

    /**
     * Obtient la valeur de la propriété domainOfInfluence.
     * 
     * @return
     *     possible object is
     *     {@link DomainOfInfluenceType }
     *     
     */
    public DomainOfInfluenceType getDomainOfInfluence() {
        return domainOfInfluence;
    }

    /**
     * Définit la valeur de la propriété domainOfInfluence.
     * 
     * @param value
     *     allowed object is
     *     {@link DomainOfInfluenceType }
     *     
     */
    public void setDomainOfInfluence(DomainOfInfluenceType value) {
        this.domainOfInfluence = value;
    }

    /**
     * Obtient la valeur de la propriété countingCircle.
     * 
     * @return
     *     possible object is
     *     {@link CountingCircleType }
     *     
     */
    public CountingCircleType getCountingCircle() {
        return countingCircle;
    }

    /**
     * Définit la valeur de la propriété countingCircle.
     * 
     * @param value
     *     allowed object is
     *     {@link CountingCircleType }
     *     
     */
    public void setCountingCircle(CountingCircleType value) {
        this.countingCircle = value;
    }

}
