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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour authorizationObjectType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="authorizationObjectType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="domainOfInfluence" type="{http://www.evoting.ch/xmlns/config/4}domainOfInfluenceType"/&gt;
 *         &lt;element name="countingCircle" type="{http://www.evoting.ch/xmlns/config/4}countingCircleType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "authorizationObjectType", propOrder = {
    "domainOfInfluence",
    "countingCircle"
})
public class AuthorizationObjectType {

    @XmlElement(required = true)
    protected DomainOfInfluenceType domainOfInfluence;
    @XmlElement(required = true)
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

    public AuthorizationObjectType withDomainOfInfluence(DomainOfInfluenceType value) {
        setDomainOfInfluence(value);
        return this;
    }

    public AuthorizationObjectType withCountingCircle(CountingCircleType value) {
        setCountingCircle(value);
        return this;
    }

}
