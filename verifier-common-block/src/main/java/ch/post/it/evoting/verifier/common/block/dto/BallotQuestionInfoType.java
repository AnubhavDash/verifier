
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ballotQuestionInfoType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ballotQuestionInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="language">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="de"/>
 *               &lt;enumeration value="fr"/>
 *               &lt;enumeration value="it"/>
 *               &lt;enumeration value="rm"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ballotQuestionTitle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ballotQuestion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ballotQuestionInfoType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "language",
    "ballotQuestionTitle",
    "ballotQuestion"
})
public class BallotQuestionInfoType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String language;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String ballotQuestionTitle;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String ballotQuestion;

    /**
     * Obtient la valeur de la propriété language.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Définit la valeur de la propriété language.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Obtient la valeur de la propriété ballotQuestionTitle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBallotQuestionTitle() {
        return ballotQuestionTitle;
    }

    /**
     * Définit la valeur de la propriété ballotQuestionTitle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBallotQuestionTitle(String value) {
        this.ballotQuestionTitle = value;
    }

    /**
     * Obtient la valeur de la propriété ballotQuestion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBallotQuestion() {
        return ballotQuestion;
    }

    /**
     * Définit la valeur de la propriété ballotQuestion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBallotQuestion(String value) {
        this.ballotQuestion = value;
    }

}
