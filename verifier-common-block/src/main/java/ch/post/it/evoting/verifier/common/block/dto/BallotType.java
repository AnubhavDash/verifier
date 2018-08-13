
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ballotType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ballotType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ballotIdentification">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="b1"/>
 *               &lt;enumeration value="b2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ballotPosition">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ballotDescription" type="{http://www.evoting.ch/xmlns/config/3}ballotDescriptionType"/>
 *         &lt;element name="variantBallot" type="{http://www.evoting.ch/xmlns/config/3}variantBallotType" minOccurs="0"/>
 *         &lt;element name="standardBallot" type="{http://www.evoting.ch/xmlns/config/3}standardBallotType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ballotType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "ballotIdentification",
    "ballotPosition",
    "ballotDescription",
    "variantBallot",
    "standardBallot"
})
public class BallotType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String ballotIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String ballotPosition;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected BallotDescriptionType ballotDescription;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected VariantBallotType variantBallot;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected StandardBallotType standardBallot;

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
     *     {@link String }
     *     
     */
    public String getBallotPosition() {
        return ballotPosition;
    }

    /**
     * Définit la valeur de la propriété ballotPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBallotPosition(String value) {
        this.ballotPosition = value;
    }

    /**
     * Obtient la valeur de la propriété ballotDescription.
     * 
     * @return
     *     possible object is
     *     {@link BallotDescriptionType }
     *     
     */
    public BallotDescriptionType getBallotDescription() {
        return ballotDescription;
    }

    /**
     * Définit la valeur de la propriété ballotDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link BallotDescriptionType }
     *     
     */
    public void setBallotDescription(BallotDescriptionType value) {
        this.ballotDescription = value;
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

}
