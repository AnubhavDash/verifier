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
 * <p>Classe Java pour voteDescriptionInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="voteDescriptionInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="voteDescriptionInfo" maxOccurs="unbounded" minOccurs="4"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *                   &lt;element name="voteDescription"&gt;
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
@XmlType(name = "voteDescriptionInformationType", propOrder = {
    "voteDescriptionInfo"
})
public class VoteDescriptionInformationType {

    @XmlElement(required = true)
    protected List<VoteDescriptionInfo> voteDescriptionInfo;

    /**
     * Gets the value of the voteDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the voteDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVoteDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VoteDescriptionInfo }
     * 
     * 
     */
    public List<VoteDescriptionInfo> getVoteDescriptionInfo() {
        if (voteDescriptionInfo == null) {
            voteDescriptionInfo = new ArrayList<VoteDescriptionInfo>();
        }
        return this.voteDescriptionInfo;
    }

    public VoteDescriptionInformationType withVoteDescriptionInfo(VoteDescriptionInfo... values) {
        if (values!= null) {
            for (VoteDescriptionInfo value: values) {
                getVoteDescriptionInfo().add(value);
            }
        }
        return this;
    }

    public VoteDescriptionInformationType withVoteDescriptionInfo(Collection<VoteDescriptionInfo> values) {
        if (values!= null) {
            getVoteDescriptionInfo().addAll(values);
        }
        return this;
    }

    public void setVoteDescriptionInfo(List<VoteDescriptionInfo> value) {
        this.voteDescriptionInfo = null;
        if (value!= null) {
            List<VoteDescriptionInfo> draftl = this.getVoteDescriptionInfo();
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
     *         &lt;element name="voteDescription"&gt;
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
        "voteDescription"
    })
    public static class VoteDescriptionInfo {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected LanguageType language;
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String voteDescription;

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
         * Obtient la valeur de la propriété voteDescription.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVoteDescription() {
            return voteDescription;
        }

        /**
         * Définit la valeur de la propriété voteDescription.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVoteDescription(String value) {
            this.voteDescription = value;
        }

        public VoteDescriptionInfo withLanguage(LanguageType value) {
            setLanguage(value);
            return this;
        }

        public VoteDescriptionInfo withVoteDescription(String value) {
            setVoteDescription(value);
            return this;
        }

    }

}
