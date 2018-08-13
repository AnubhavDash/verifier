
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour answerType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="answerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="answerIdentification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="answerPosition">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="standardAnswerType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="YES"/>
 *               &lt;enumeration value="NO"/>
 *               &lt;enumeration value="EMPTY"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="hiddenAnswer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="standardQuestionReference" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Q1"/>
 *               &lt;enumeration value="Q2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="answerInfo" type="{http://www.evoting.ch/xmlns/config/3}answerInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "answerType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "answerIdentification",
    "answerPosition",
    "standardAnswerType",
    "hiddenAnswer",
    "standardQuestionReference",
    "answerInfo"
})
public class AnswerType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String answerIdentification;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String answerPosition;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected String standardAnswerType;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected String hiddenAnswer;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected String standardQuestionReference;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<AnswerInfoType> answerInfo;

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
     *     {@link String }
     *     
     */
    public String getAnswerPosition() {
        return answerPosition;
    }

    /**
     * Définit la valeur de la propriété answerPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnswerPosition(String value) {
        this.answerPosition = value;
    }

    /**
     * Obtient la valeur de la propriété standardAnswerType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStandardAnswerType() {
        return standardAnswerType;
    }

    /**
     * Définit la valeur de la propriété standardAnswerType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStandardAnswerType(String value) {
        this.standardAnswerType = value;
    }

    /**
     * Obtient la valeur de la propriété hiddenAnswer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHiddenAnswer() {
        return hiddenAnswer;
    }

    /**
     * Définit la valeur de la propriété hiddenAnswer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHiddenAnswer(String value) {
        this.hiddenAnswer = value;
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
     * {@link AnswerInfoType }
     * 
     * 
     */
    public List<AnswerInfoType> getAnswerInfo() {
        if (answerInfo == null) {
            answerInfo = new ArrayList<AnswerInfoType>();
        }
        return this.answerInfo;
    }

}
