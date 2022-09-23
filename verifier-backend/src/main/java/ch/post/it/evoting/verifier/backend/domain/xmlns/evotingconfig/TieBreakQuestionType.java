//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import java.math.BigInteger;
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
 * <p>Classe Java pour tieBreakQuestionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="tieBreakQuestionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="questionIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="questionPosition" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="questionNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="15"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="answerType" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *               &lt;enumeration value="3"/&gt;
 *               &lt;enumeration value="4"/&gt;
 *               &lt;enumeration value="5"/&gt;
 *               &lt;enumeration value="6"/&gt;
 *               &lt;enumeration value="7"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ballotQuestion" type="{http://www.evoting.ch/xmlns/config/4}ballotQuestionType"/&gt;
 *         &lt;element name="answer" type="{http://www.evoting.ch/xmlns/config/4}tiebreakAnswerType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tieBreakQuestionType", propOrder = {
    "questionIdentification",
    "questionPosition",
    "questionNumber",
    "answerType",
    "ballotQuestion",
    "answer"
})
public class TieBreakQuestionType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String questionIdentification;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger questionPosition;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String questionNumber;
    protected BigInteger answerType;
    @XmlElement(required = true)
    protected BallotQuestionType ballotQuestion;
    @XmlElement(required = true)
    protected List<TiebreakAnswerType> answer;

    /**
     * Obtient la valeur de la propriété questionIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuestionIdentification() {
        return questionIdentification;
    }

    /**
     * Définit la valeur de la propriété questionIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuestionIdentification(String value) {
        this.questionIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété questionPosition.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQuestionPosition() {
        return questionPosition;
    }

    /**
     * Définit la valeur de la propriété questionPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQuestionPosition(BigInteger value) {
        this.questionPosition = value;
    }

    /**
     * Obtient la valeur de la propriété questionNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuestionNumber() {
        return questionNumber;
    }

    /**
     * Définit la valeur de la propriété questionNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuestionNumber(String value) {
        this.questionNumber = value;
    }

    /**
     * Obtient la valeur de la propriété answerType.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAnswerType() {
        return answerType;
    }

    /**
     * Définit la valeur de la propriété answerType.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAnswerType(BigInteger value) {
        this.answerType = value;
    }

    /**
     * Obtient la valeur de la propriété ballotQuestion.
     * 
     * @return
     *     possible object is
     *     {@link BallotQuestionType }
     *     
     */
    public BallotQuestionType getBallotQuestion() {
        return ballotQuestion;
    }

    /**
     * Définit la valeur de la propriété ballotQuestion.
     * 
     * @param value
     *     allowed object is
     *     {@link BallotQuestionType }
     *     
     */
    public void setBallotQuestion(BallotQuestionType value) {
        this.ballotQuestion = value;
    }

    /**
     * Gets the value of the answer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the answer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnswer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TiebreakAnswerType }
     * 
     * 
     */
    public List<TiebreakAnswerType> getAnswer() {
        if (answer == null) {
            answer = new ArrayList<TiebreakAnswerType>();
        }
        return this.answer;
    }

    public TieBreakQuestionType withQuestionIdentification(String value) {
        setQuestionIdentification(value);
        return this;
    }

    public TieBreakQuestionType withQuestionPosition(BigInteger value) {
        setQuestionPosition(value);
        return this;
    }

    public TieBreakQuestionType withQuestionNumber(String value) {
        setQuestionNumber(value);
        return this;
    }

    public TieBreakQuestionType withAnswerType(BigInteger value) {
        setAnswerType(value);
        return this;
    }

    public TieBreakQuestionType withBallotQuestion(BallotQuestionType value) {
        setBallotQuestion(value);
        return this;
    }

    public TieBreakQuestionType withAnswer(TiebreakAnswerType... values) {
        if (values!= null) {
            for (TiebreakAnswerType value: values) {
                getAnswer().add(value);
            }
        }
        return this;
    }

    public TieBreakQuestionType withAnswer(Collection<TiebreakAnswerType> values) {
        if (values!= null) {
            getAnswer().addAll(values);
        }
        return this;
    }

    public void setAnswer(List<TiebreakAnswerType> value) {
        this.answer = null;
        if (value!= null) {
            List<TiebreakAnswerType> draftl = this.getAnswer();
            draftl.addAll(value);
        }
    }

}
