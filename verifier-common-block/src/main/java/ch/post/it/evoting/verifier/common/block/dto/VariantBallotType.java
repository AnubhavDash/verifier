
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour variantBallotType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="variantBallotType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="standardQuestion" type="{http://www.evoting.ch/xmlns/config/3}standardQuestionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tieBreakQuestion" type="{http://www.evoting.ch/xmlns/config/3}tieBreakQuestionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "variantBallotType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "standardQuestion",
    "tieBreakQuestion"
})
public class VariantBallotType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<StandardQuestionType> standardQuestion;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected TieBreakQuestionType tieBreakQuestion;

    /**
     * Gets the value of the standardQuestion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the standardQuestion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStandardQuestion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StandardQuestionType }
     * 
     * 
     */
    public List<StandardQuestionType> getStandardQuestion() {
        if (standardQuestion == null) {
            standardQuestion = new ArrayList<StandardQuestionType>();
        }
        return this.standardQuestion;
    }

    /**
     * Obtient la valeur de la propriété tieBreakQuestion.
     * 
     * @return
     *     possible object is
     *     {@link TieBreakQuestionType }
     *     
     */
    public TieBreakQuestionType getTieBreakQuestion() {
        return tieBreakQuestion;
    }

    /**
     * Définit la valeur de la propriété tieBreakQuestion.
     * 
     * @param value
     *     allowed object is
     *     {@link TieBreakQuestionType }
     *     
     */
    public void setTieBreakQuestion(TieBreakQuestionType value) {
        this.tieBreakQuestion = value;
    }

}
