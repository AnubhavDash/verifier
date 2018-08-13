
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour standardBallotType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="standardBallotType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="questionIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="answerType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ballotQuestion" type="{http://www.evoting.ch/xmlns/config/3}ballotQuestionType"/>
 *         &lt;element name="answer" type="{http://www.evoting.ch/xmlns/config/3}answerType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "standardBallotType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "questionIdentification",
    "answerType",
    "ballotQuestion",
    "answer"
})
public class StandardBallotType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String questionIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String answerType;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected BallotQuestionType ballotQuestion;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<AnswerType> answer;

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
     *     {@link String }
     *     
     */
    public String getAnswerType() {
        return answerType;
    }

    /**
     * Définit la valeur de la propriété answerType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnswerType(String value) {
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
     * {@link AnswerType }
     * 
     * 
     */
    public List<AnswerType> getAnswer() {
        if (answer == null) {
            answer = new ArrayList<AnswerType>();
        }
        return this.answer;
    }

}
