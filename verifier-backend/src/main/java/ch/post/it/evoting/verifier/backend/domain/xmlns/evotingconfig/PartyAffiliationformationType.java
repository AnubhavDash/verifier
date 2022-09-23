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
 * <p>Classe Java pour partyAffiliationformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="partyAffiliationformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="partyAffiliationInfo" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *                   &lt;element name="partyAffiliationShort"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="12"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="partyAffiliationLong" minOccurs="0"&gt;
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
@XmlType(name = "partyAffiliationformationType", propOrder = {
    "partyAffiliationInfo"
})
public class PartyAffiliationformationType {

    @XmlElement(required = true)
    protected List<PartyAffiliationInfo> partyAffiliationInfo;

    /**
     * Gets the value of the partyAffiliationInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partyAffiliationInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartyAffiliationInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartyAffiliationInfo }
     * 
     * 
     */
    public List<PartyAffiliationInfo> getPartyAffiliationInfo() {
        if (partyAffiliationInfo == null) {
            partyAffiliationInfo = new ArrayList<PartyAffiliationInfo>();
        }
        return this.partyAffiliationInfo;
    }

    public PartyAffiliationformationType withPartyAffiliationInfo(PartyAffiliationInfo... values) {
        if (values!= null) {
            for (PartyAffiliationInfo value: values) {
                getPartyAffiliationInfo().add(value);
            }
        }
        return this;
    }

    public PartyAffiliationformationType withPartyAffiliationInfo(Collection<PartyAffiliationInfo> values) {
        if (values!= null) {
            getPartyAffiliationInfo().addAll(values);
        }
        return this;
    }

    public void setPartyAffiliationInfo(List<PartyAffiliationInfo> value) {
        this.partyAffiliationInfo = null;
        if (value!= null) {
            List<PartyAffiliationInfo> draftl = this.getPartyAffiliationInfo();
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
     *         &lt;element name="partyAffiliationShort"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="12"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="partyAffiliationLong" minOccurs="0"&gt;
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
        "partyAffiliationShort",
        "partyAffiliationLong"
    })
    public static class PartyAffiliationInfo {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected LanguageType language;
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String partyAffiliationShort;
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String partyAffiliationLong;

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
         * Obtient la valeur de la propriété partyAffiliationShort.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPartyAffiliationShort() {
            return partyAffiliationShort;
        }

        /**
         * Définit la valeur de la propriété partyAffiliationShort.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPartyAffiliationShort(String value) {
            this.partyAffiliationShort = value;
        }

        /**
         * Obtient la valeur de la propriété partyAffiliationLong.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPartyAffiliationLong() {
            return partyAffiliationLong;
        }

        /**
         * Définit la valeur de la propriété partyAffiliationLong.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPartyAffiliationLong(String value) {
            this.partyAffiliationLong = value;
        }

        public PartyAffiliationInfo withLanguage(LanguageType value) {
            setLanguage(value);
            return this;
        }

        public PartyAffiliationInfo withPartyAffiliationShort(String value) {
            setPartyAffiliationShort(value);
            return this;
        }

        public PartyAffiliationInfo withPartyAffiliationLong(String value) {
            setPartyAffiliationLong(value);
            return this;
        }

    }

}
