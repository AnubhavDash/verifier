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
 * <p>Classe Java pour ballotDescriptionInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ballotDescriptionInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ballotDescriptionInfo" maxOccurs="unbounded" minOccurs="4"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *                   &lt;element name="ballotDescriptionLong" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="255"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="ballotDescriptionShort" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="100"/&gt;
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
@XmlType(name = "ballotDescriptionInformationType", propOrder = {
    "ballotDescriptionInfo"
})
public class BallotDescriptionInformationType {

    @XmlElement(required = true)
    protected List<BallotDescriptionInfo> ballotDescriptionInfo;

    /**
     * Gets the value of the ballotDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ballotDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBallotDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BallotDescriptionInfo }
     * 
     * 
     */
    public List<BallotDescriptionInfo> getBallotDescriptionInfo() {
        if (ballotDescriptionInfo == null) {
            ballotDescriptionInfo = new ArrayList<BallotDescriptionInfo>();
        }
        return this.ballotDescriptionInfo;
    }

    public BallotDescriptionInformationType withBallotDescriptionInfo(BallotDescriptionInfo... values) {
        if (values!= null) {
            for (BallotDescriptionInfo value: values) {
                getBallotDescriptionInfo().add(value);
            }
        }
        return this;
    }

    public BallotDescriptionInformationType withBallotDescriptionInfo(Collection<BallotDescriptionInfo> values) {
        if (values!= null) {
            getBallotDescriptionInfo().addAll(values);
        }
        return this;
    }

    public void setBallotDescriptionInfo(List<BallotDescriptionInfo> value) {
        this.ballotDescriptionInfo = null;
        if (value!= null) {
            List<BallotDescriptionInfo> draftl = this.getBallotDescriptionInfo();
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
     *         &lt;element name="ballotDescriptionLong" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="255"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="ballotDescriptionShort" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="100"/&gt;
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
        "ballotDescriptionLong",
        "ballotDescriptionShort"
    })
    public static class BallotDescriptionInfo {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected LanguageType language;
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ballotDescriptionLong;
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String ballotDescriptionShort;

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
         * Obtient la valeur de la propriété ballotDescriptionLong.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBallotDescriptionLong() {
            return ballotDescriptionLong;
        }

        /**
         * Définit la valeur de la propriété ballotDescriptionLong.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBallotDescriptionLong(String value) {
            this.ballotDescriptionLong = value;
        }

        /**
         * Obtient la valeur de la propriété ballotDescriptionShort.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBallotDescriptionShort() {
            return ballotDescriptionShort;
        }

        /**
         * Définit la valeur de la propriété ballotDescriptionShort.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBallotDescriptionShort(String value) {
            this.ballotDescriptionShort = value;
        }

        public BallotDescriptionInfo withLanguage(LanguageType value) {
            setLanguage(value);
            return this;
        }

        public BallotDescriptionInfo withBallotDescriptionLong(String value) {
            setBallotDescriptionLong(value);
            return this;
        }

        public BallotDescriptionInfo withBallotDescriptionShort(String value) {
            setBallotDescriptionShort(value);
            return this;
        }

    }

}
