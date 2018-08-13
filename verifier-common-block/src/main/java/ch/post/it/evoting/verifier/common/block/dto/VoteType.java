
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour voteType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="voteType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="voteIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="domainOfInfluence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="voteDescription" type="{http://www.evoting.ch/xmlns/config/3}voteDescriptionType"/>
 *         &lt;element name="ballot" type="{http://www.evoting.ch/xmlns/config/3}ballotType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voteType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "voteIdentification",
    "domainOfInfluence",
    "voteDescription",
    "ballot"
})
public class VoteType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String voteIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String domainOfInfluence;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected VoteDescriptionType voteDescription;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<BallotType> ballot;

    /**
     * Obtient la valeur de la propriété voteIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoteIdentification() {
        return voteIdentification;
    }

    /**
     * Définit la valeur de la propriété voteIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoteIdentification(String value) {
        this.voteIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété domainOfInfluence.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainOfInfluence() {
        return domainOfInfluence;
    }

    /**
     * Définit la valeur de la propriété domainOfInfluence.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainOfInfluence(String value) {
        this.domainOfInfluence = value;
    }

    /**
     * Obtient la valeur de la propriété voteDescription.
     * 
     * @return
     *     possible object is
     *     {@link VoteDescriptionType }
     *     
     */
    public VoteDescriptionType getVoteDescription() {
        return voteDescription;
    }

    /**
     * Définit la valeur de la propriété voteDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link VoteDescriptionType }
     *     
     */
    public void setVoteDescription(VoteDescriptionType value) {
        this.voteDescription = value;
    }

    /**
     * Gets the value of the ballot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ballot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBallot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BallotType }
     * 
     * 
     */
    public List<BallotType> getBallot() {
        if (ballot == null) {
            ballot = new ArrayList<BallotType>();
        }
        return this.ballot;
    }

}
