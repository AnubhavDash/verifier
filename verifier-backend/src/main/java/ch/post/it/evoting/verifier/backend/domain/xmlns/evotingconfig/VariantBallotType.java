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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour variantBallotType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="variantBallotType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="standardQuestion" type="{http://www.evoting.ch/xmlns/config/4}standardQuestionType" maxOccurs="unbounded" minOccurs="2"/&gt;
 *         &lt;element name="tieBreakQuestion" type="{http://www.evoting.ch/xmlns/config/4}tieBreakQuestionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "variantBallotType", propOrder = {
    "standardQuestion",
    "tieBreakQuestion"
})
public class VariantBallotType {

    @XmlElement(required = true)
    protected List<StandardQuestionType> standardQuestion;
    protected List<TieBreakQuestionType> tieBreakQuestion;

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
     * Gets the value of the tieBreakQuestion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tieBreakQuestion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTieBreakQuestion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TieBreakQuestionType }
     * 
     * 
     */
    public List<TieBreakQuestionType> getTieBreakQuestion() {
        if (tieBreakQuestion == null) {
            tieBreakQuestion = new ArrayList<TieBreakQuestionType>();
        }
        return this.tieBreakQuestion;
    }

    public VariantBallotType withStandardQuestion(StandardQuestionType... values) {
        if (values!= null) {
            for (StandardQuestionType value: values) {
                getStandardQuestion().add(value);
            }
        }
        return this;
    }

    public VariantBallotType withStandardQuestion(Collection<StandardQuestionType> values) {
        if (values!= null) {
            getStandardQuestion().addAll(values);
        }
        return this;
    }

    public VariantBallotType withTieBreakQuestion(TieBreakQuestionType... values) {
        if (values!= null) {
            for (TieBreakQuestionType value: values) {
                getTieBreakQuestion().add(value);
            }
        }
        return this;
    }

    public VariantBallotType withTieBreakQuestion(Collection<TieBreakQuestionType> values) {
        if (values!= null) {
            getTieBreakQuestion().addAll(values);
        }
        return this;
    }

    public void setStandardQuestion(List<StandardQuestionType> value) {
        this.standardQuestion = null;
        if (value!= null) {
            List<StandardQuestionType> draftl = this.getStandardQuestion();
            draftl.addAll(value);
        }
    }

    public void setTieBreakQuestion(List<TieBreakQuestionType> value) {
        this.tieBreakQuestion = null;
        if (value!= null) {
            List<TieBreakQuestionType> draftl = this.getTieBreakQuestion();
            draftl.addAll(value);
        }
    }

}
