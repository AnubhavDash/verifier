//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour referencedElectionInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="referencedElectionInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="referencedElection" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="electionRelation"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "referencedElectionInformationType", propOrder = {
    "referencedElection",
    "electionRelation"
})
public class ReferencedElectionInformationType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String referencedElection;
    @XmlElement(required = true)
    protected BigInteger electionRelation;

    /**
     * Obtient la valeur de la propriété referencedElection.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencedElection() {
        return referencedElection;
    }

    /**
     * Définit la valeur de la propriété referencedElection.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencedElection(String value) {
        this.referencedElection = value;
    }

    /**
     * Obtient la valeur de la propriété electionRelation.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getElectionRelation() {
        return electionRelation;
    }

    /**
     * Définit la valeur de la propriété electionRelation.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setElectionRelation(BigInteger value) {
        this.electionRelation = value;
    }

    public ReferencedElectionInformationType withReferencedElection(String value) {
        setReferencedElection(value);
        return this;
    }

    public ReferencedElectionInformationType withElectionRelation(BigInteger value) {
        setElectionRelation(value);
        return this;
    }

}
