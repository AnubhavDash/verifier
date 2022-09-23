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
 * <p>Classe Java pour voteInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="voteInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vote" type="{http://www.evoting.ch/xmlns/config/4}voteType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voteInformationType", propOrder = {
    "vote"
})
public class VoteInformationType {

    @XmlElement(required = true)
    protected VoteType vote;

    /**
     * Obtient la valeur de la propriété vote.
     * 
     * @return
     *     possible object is
     *     {@link VoteType }
     *     
     */
    public VoteType getVote() {
        return vote;
    }

    /**
     * Définit la valeur de la propriété vote.
     * 
     * @param value
     *     allowed object is
     *     {@link VoteType }
     *     
     */
    public void setVote(VoteType value) {
        this.vote = value;
    }

    public VoteInformationType withVote(VoteType value) {
        setVote(value);
        return this;
    }

}
