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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour ballotQuestionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ballotQuestionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ballotQuestionInfo" maxOccurs="unbounded" minOccurs="4"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *                   &lt;element name="ballotQuestionTitle" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="100"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ballotQuestion"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="700"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ballotQuestionType", propOrder = {
    "ballotQuestionInfo"
})
public class BallotQuestionType {

    @XmlElement(required = true)
    protected List<BallotQuestionInfo> ballotQuestionInfo;

    /**
     * Gets the value of the ballotQuestionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ballotQuestionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBallotQuestionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BallotQuestionInfo }
     * 
     * 
     */
    public List<BallotQuestionInfo> getBallotQuestionInfo() {
        if (ballotQuestionInfo == null) {
            ballotQuestionInfo = new ArrayList<BallotQuestionInfo>();
        }
        return this.ballotQuestionInfo;
    }

    public BallotQuestionType withBallotQuestionInfo(BallotQuestionInfo... values) {
        if (values!= null) {
            for (BallotQuestionInfo value: values) {
                getBallotQuestionInfo().add(value);
            }
        }
        return this;
    }

    public BallotQuestionType withBallotQuestionInfo(Collection<BallotQuestionInfo> values) {
        if (values!= null) {
            getBallotQuestionInfo().addAll(values);
        }
        return this;
    }

    public void setBallotQuestionInfo(List<BallotQuestionInfo> value) {
        this.ballotQuestionInfo = null;
        if (value!= null) {
            List<BallotQuestionInfo> draftl = this.getBallotQuestionInfo();
            draftl.addAll(value);
        }
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
     *         &lt;element name="ballotQuestionTitle" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="100"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ballotQuestion"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="700"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "language",
        "ballotQuestionTitle",
        "ballotQuestion"
    })
    public static class BallotQuestionInfo {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected LanguageType language;
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ballotQuestionTitle;
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ballotQuestion;

        /**
         * Obtient la valeur de la propriété language.
         * 
         * @return
         *     possible object is
         *     {@link LanguageType }
         *     
         */
        public LanguageType getLanguage() {
            return language;
        }

        /**
         * Définit la valeur de la propriété language.
         * 
         * @param value
         *     allowed object is
         *     {@link LanguageType }
         *     
         */
        public void setLanguage(LanguageType value) {
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

        public BallotQuestionInfo withLanguage(LanguageType value) {
            setLanguage(value);
            return this;
        }

        public BallotQuestionInfo withBallotQuestionTitle(String value) {
            setBallotQuestionTitle(value);
            return this;
        }

        public BallotQuestionInfo withBallotQuestion(String value) {
            setBallotQuestion(value);
            return this;
        }

    }

}
