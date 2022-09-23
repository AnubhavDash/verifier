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
 * <p>Classe Java pour tiebreakAnswerType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="tiebreakAnswerType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="answerIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="answerPosition" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="standardQuestionReference" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="hiddenAnswer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="answerInfo" type="{http://www.evoting.ch/xmlns/config/4}answerInformationType" maxOccurs="unbounded" minOccurs="4"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tiebreakAnswerType", propOrder = {
    "answerIdentification",
    "answerPosition",
    "standardQuestionReference",
    "hiddenAnswer",
    "answerInfo"
})
public class TiebreakAnswerType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String answerIdentification;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger answerPosition;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String standardQuestionReference;
    protected Boolean hiddenAnswer;
    @XmlElement(required = true)
    protected List<AnswerInformationType> answerInfo;

    /**
     * Obtient la valeur de la propriété answerIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnswerIdentification() {
        return answerIdentification;
    }

    /**
     * Définit la valeur de la propriété answerIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnswerIdentification(String value) {
        this.answerIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété answerPosition.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAnswerPosition() {
        return answerPosition;
    }

    /**
     * Définit la valeur de la propriété answerPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAnswerPosition(BigInteger value) {
        this.answerPosition = value;
    }

    /**
     * Obtient la valeur de la propriété standardQuestionReference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStandardQuestionReference() {
        return standardQuestionReference;
    }

    /**
     * Définit la valeur de la propriété standardQuestionReference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStandardQuestionReference(String value) {
        this.standardQuestionReference = value;
    }

    /**
     * Obtient la valeur de la propriété hiddenAnswer.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHiddenAnswer() {
        return hiddenAnswer;
    }

    /**
     * Définit la valeur de la propriété hiddenAnswer.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHiddenAnswer(Boolean value) {
        this.hiddenAnswer = value;
    }

    /**
     * Gets the value of the answerInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the answerInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnswerInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnswerInformationType }
     * 
     * 
     */
    public List<AnswerInformationType> getAnswerInfo() {
        if (answerInfo == null) {
            answerInfo = new ArrayList<AnswerInformationType>();
        }
        return this.answerInfo;
    }

    public TiebreakAnswerType withAnswerIdentification(String value) {
        setAnswerIdentification(value);
        return this;
    }

    public TiebreakAnswerType withAnswerPosition(BigInteger value) {
        setAnswerPosition(value);
        return this;
    }

    public TiebreakAnswerType withStandardQuestionReference(String value) {
        setStandardQuestionReference(value);
        return this;
    }

    public TiebreakAnswerType withHiddenAnswer(Boolean value) {
        setHiddenAnswer(value);
        return this;
    }

    public TiebreakAnswerType withAnswerInfo(AnswerInformationType... values) {
        if (values!= null) {
            for (AnswerInformationType value: values) {
                getAnswerInfo().add(value);
            }
        }
        return this;
    }

    public TiebreakAnswerType withAnswerInfo(Collection<AnswerInformationType> values) {
        if (values!= null) {
            getAnswerInfo().addAll(values);
        }
        return this;
    }

    public void setAnswerInfo(List<AnswerInformationType> value) {
        this.answerInfo = null;
        if (value!= null) {
            List<AnswerInformationType> draftl = this.getAnswerInfo();
            draftl.addAll(value);
        }
    }

}
