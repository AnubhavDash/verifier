//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

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


/**
 * <p>Classe Java pour voteType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="voteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="voteIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="domainOfInfluence" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="voteDescription" type="{http://www.evoting.ch/xmlns/config/4}voteDescriptionInformationType"/&gt;
 *         &lt;element name="ballot" type="{http://www.evoting.ch/xmlns/config/4}ballotType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voteType", propOrder = {
    "voteIdentification",
    "domainOfInfluence",
    "voteDescription",
    "ballot"
})
public class VoteType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String voteIdentification;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String domainOfInfluence;
    @XmlElement(required = true)
    protected VoteDescriptionInformationType voteDescription;
    @XmlElement(required = true)
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
     *     {@link VoteDescriptionInformationType }
     *     
     */
    public VoteDescriptionInformationType getVoteDescription() {
        return voteDescription;
    }

    /**
     * Définit la valeur de la propriété voteDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link VoteDescriptionInformationType }
     *     
     */
    public void setVoteDescription(VoteDescriptionInformationType value) {
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

    public VoteType withVoteIdentification(String value) {
        setVoteIdentification(value);
        return this;
    }

    public VoteType withDomainOfInfluence(String value) {
        setDomainOfInfluence(value);
        return this;
    }

    public VoteType withVoteDescription(VoteDescriptionInformationType value) {
        setVoteDescription(value);
        return this;
    }

    public VoteType withBallot(BallotType... values) {
        if (values!= null) {
            for (BallotType value: values) {
                getBallot().add(value);
            }
        }
        return this;
    }

    public VoteType withBallot(Collection<BallotType> values) {
        if (values!= null) {
            getBallot().addAll(values);
        }
        return this;
    }

    public void setBallot(List<BallotType> value) {
        this.ballot = null;
        if (value!= null) {
            List<BallotType> draftl = this.getBallot();
            draftl.addAll(value);
        }
    }

}
