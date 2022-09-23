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
 * <p>Classe Java pour standardBallotType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="standardBallotType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="questionIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
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
 *         &lt;element name="answer" type="{http://www.evoting.ch/xmlns/config/4}standardAnswerType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "standardBallotType", propOrder = {
    "questionIdentification",
    "answerType",
    "ballotQuestion",
    "answer"
})
public class StandardBallotType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String questionIdentification;
    protected BigInteger answerType;
    @XmlElement(required = true)
    protected BallotQuestionType ballotQuestion;
    @XmlElement(required = true)
    protected List<StandardAnswerType> answer;

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
     * {@link StandardAnswerType }
     * 
     * 
     */
    public List<StandardAnswerType> getAnswer() {
        if (answer == null) {
            answer = new ArrayList<StandardAnswerType>();
        }
        return this.answer;
    }

    public StandardBallotType withQuestionIdentification(String value) {
        setQuestionIdentification(value);
        return this;
    }

    public StandardBallotType withAnswerType(BigInteger value) {
        setAnswerType(value);
        return this;
    }

    public StandardBallotType withBallotQuestion(BallotQuestionType value) {
        setBallotQuestion(value);
        return this;
    }

    public StandardBallotType withAnswer(StandardAnswerType... values) {
        if (values!= null) {
            for (StandardAnswerType value: values) {
                getAnswer().add(value);
            }
        }
        return this;
    }

    public StandardBallotType withAnswer(Collection<StandardAnswerType> values) {
        if (values!= null) {
            getAnswer().addAll(values);
        }
        return this;
    }

    public void setAnswer(List<StandardAnswerType> value) {
        this.answer = null;
        if (value!= null) {
            List<StandardAnswerType> draftl = this.getAnswer();
            draftl.addAll(value);
        }
    }

}
