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
 * <p>Classe Java pour electionDescriptionInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electionDescriptionInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="electionDescriptionInfo" maxOccurs="unbounded" minOccurs="4"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *                   &lt;element name="electionDescriptionShort" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;maxLength value="100"/&gt;
 *                         &lt;minLength value="1"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="electionDescription"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="255"/&gt;
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
@XmlType(name = "electionDescriptionInformationType", propOrder = {
    "electionDescriptionInfo"
})
public class ElectionDescriptionInformationType {

    @XmlElement(required = true)
    protected List<ElectionDescriptionInfo> electionDescriptionInfo;

    /**
     * Gets the value of the electionDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the electionDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElectionDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElectionDescriptionInfo }
     * 
     * 
     */
    public List<ElectionDescriptionInfo> getElectionDescriptionInfo() {
        if (electionDescriptionInfo == null) {
            electionDescriptionInfo = new ArrayList<ElectionDescriptionInfo>();
        }
        return this.electionDescriptionInfo;
    }

    public ElectionDescriptionInformationType withElectionDescriptionInfo(ElectionDescriptionInfo... values) {
        if (values!= null) {
            for (ElectionDescriptionInfo value: values) {
                getElectionDescriptionInfo().add(value);
            }
        }
        return this;
    }

    public ElectionDescriptionInformationType withElectionDescriptionInfo(Collection<ElectionDescriptionInfo> values) {
        if (values!= null) {
            getElectionDescriptionInfo().addAll(values);
        }
        return this;
    }

    public void setElectionDescriptionInfo(List<ElectionDescriptionInfo> value) {
        this.electionDescriptionInfo = null;
        if (value!= null) {
            List<ElectionDescriptionInfo> draftl = this.getElectionDescriptionInfo();
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
     *         &lt;element name="electionDescriptionShort" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;maxLength value="100"/&gt;
     *               &lt;minLength value="1"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="electionDescription"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="255"/&gt;
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
        "electionDescriptionShort",
        "electionDescription"
    })
    public static class ElectionDescriptionInfo {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected LanguageType language;
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String electionDescriptionShort;
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String electionDescription;

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
         * Obtient la valeur de la propriété electionDescriptionShort.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getElectionDescriptionShort() {
            return electionDescriptionShort;
        }

        /**
         * Définit la valeur de la propriété electionDescriptionShort.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setElectionDescriptionShort(String value) {
            this.electionDescriptionShort = value;
        }

        /**
         * Obtient la valeur de la propriété electionDescription.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getElectionDescription() {
            return electionDescription;
        }

        /**
         * Définit la valeur de la propriété electionDescription.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setElectionDescription(String value) {
            this.electionDescription = value;
        }

        public ElectionDescriptionInfo withLanguage(LanguageType value) {
            setLanguage(value);
            return this;
        }

        public ElectionDescriptionInfo withElectionDescriptionShort(String value) {
            setElectionDescriptionShort(value);
            return this;
        }

        public ElectionDescriptionInfo withElectionDescription(String value) {
            setElectionDescription(value);
            return this;
        }

    }

}
