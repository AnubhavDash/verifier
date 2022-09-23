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
 * <p>Classe Java pour ballotType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ballotType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ballotIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="ballotPosition" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="ballotDescription" type="{http://www.evoting.ch/xmlns/config/4}ballotDescriptionInformationType" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="standardBallot" type="{http://www.evoting.ch/xmlns/config/4}standardBallotType"/&gt;
 *           &lt;element name="variantBallot" type="{http://www.evoting.ch/xmlns/config/4}variantBallotType"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ballotType", propOrder = {
    "ballotIdentification",
    "ballotPosition",
    "ballotDescription",
    "standardBallot",
    "variantBallot"
})
public class BallotType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String ballotIdentification;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger ballotPosition;
    protected BallotDescriptionInformationType ballotDescription;
    protected StandardBallotType standardBallot;
    protected VariantBallotType variantBallot;

    /**
     * Obtient la valeur de la propriété ballotIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBallotIdentification() {
        return ballotIdentification;
    }

    /**
     * Définit la valeur de la propriété ballotIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBallotIdentification(String value) {
        this.ballotIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété ballotPosition.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBallotPosition() {
        return ballotPosition;
    }

    /**
     * Définit la valeur de la propriété ballotPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBallotPosition(BigInteger value) {
        this.ballotPosition = value;
    }

    /**
     * Obtient la valeur de la propriété ballotDescription.
     * 
     * @return
     *     possible object is
     *     {@link BallotDescriptionInformationType }
     *     
     */
    public BallotDescriptionInformationType getBallotDescription() {
        return ballotDescription;
    }

    /**
     * Définit la valeur de la propriété ballotDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link BallotDescriptionInformationType }
     *     
     */
    public void setBallotDescription(BallotDescriptionInformationType value) {
        this.ballotDescription = value;
    }

    /**
     * Obtient la valeur de la propriété standardBallot.
     * 
     * @return
     *     possible object is
     *     {@link StandardBallotType }
     *     
     */
    public StandardBallotType getStandardBallot() {
        return standardBallot;
    }

    /**
     * Définit la valeur de la propriété standardBallot.
     * 
     * @param value
     *     allowed object is
     *     {@link StandardBallotType }
     *     
     */
    public void setStandardBallot(StandardBallotType value) {
        this.standardBallot = value;
    }

    /**
     * Obtient la valeur de la propriété variantBallot.
     * 
     * @return
     *     possible object is
     *     {@link VariantBallotType }
     *     
     */
    public VariantBallotType getVariantBallot() {
        return variantBallot;
    }

    /**
     * Définit la valeur de la propriété variantBallot.
     * 
     * @param value
     *     allowed object is
     *     {@link VariantBallotType }
     *     
     */
    public void setVariantBallot(VariantBallotType value) {
        this.variantBallot = value;
    }

    public BallotType withBallotIdentification(String value) {
        setBallotIdentification(value);
        return this;
    }

    public BallotType withBallotPosition(BigInteger value) {
        setBallotPosition(value);
        return this;
    }

    public BallotType withBallotDescription(BallotDescriptionInformationType value) {
        setBallotDescription(value);
        return this;
    }

    public BallotType withStandardBallot(StandardBallotType value) {
        setStandardBallot(value);
        return this;
    }

    public BallotType withVariantBallot(VariantBallotType value) {
        setVariantBallot(value);
        return this;
    }

}
