
package ch.post.it.evoting.verifier.common.block.dto;

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
 * &lt;complexType name="voteInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vote" type="{http://www.evoting.ch/xmlns/config/3}voteType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voteInformationType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "vote"
})
public class VoteInformationType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
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

}
