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
 * <p>Classe Java pour listDescriptionInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listDescriptionInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="listDescriptionInfo" maxOccurs="unbounded" minOccurs="4"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *                   &lt;element name="listDescriptionShort" minOccurs="0"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="20"/&gt;
 *                       &lt;/restriction&gt;
 *                     &lt;/simpleType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="listDescription"&gt;
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
@XmlType(name = "listDescriptionInformationType", propOrder = {
    "listDescriptionInfo"
})
public class ListDescriptionInformationType {

    @XmlElement(required = true)
    protected List<ListDescriptionInfo> listDescriptionInfo;

    /**
     * Gets the value of the listDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListDescriptionInfo }
     * 
     * 
     */
    public List<ListDescriptionInfo> getListDescriptionInfo() {
        if (listDescriptionInfo == null) {
            listDescriptionInfo = new ArrayList<ListDescriptionInfo>();
        }
        return this.listDescriptionInfo;
    }

    public ListDescriptionInformationType withListDescriptionInfo(ListDescriptionInfo... values) {
        if (values!= null) {
            for (ListDescriptionInfo value: values) {
                getListDescriptionInfo().add(value);
            }
        }
        return this;
    }

    public ListDescriptionInformationType withListDescriptionInfo(Collection<ListDescriptionInfo> values) {
        if (values!= null) {
            getListDescriptionInfo().addAll(values);
        }
        return this;
    }

    public void setListDescriptionInfo(List<ListDescriptionInfo> value) {
        this.listDescriptionInfo = null;
        if (value!= null) {
            List<ListDescriptionInfo> draftl = this.getListDescriptionInfo();
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
     *         &lt;element name="listDescriptionShort" minOccurs="0"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="20"/&gt;
     *             &lt;/restriction&gt;
     *           &lt;/simpleType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="listDescription"&gt;
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
        "listDescriptionShort",
        "listDescription"
    })
    public static class ListDescriptionInfo {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected LanguageType language;
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String listDescriptionShort;
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String listDescription;

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
         * Obtient la valeur de la propriété listDescriptionShort.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getListDescriptionShort() {
            return listDescriptionShort;
        }

        /**
         * Définit la valeur de la propriété listDescriptionShort.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setListDescriptionShort(String value) {
            this.listDescriptionShort = value;
        }

        /**
         * Obtient la valeur de la propriété listDescription.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getListDescription() {
            return listDescription;
        }

        /**
         * Définit la valeur de la propriété listDescription.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setListDescription(String value) {
            this.listDescription = value;
        }

        public ListDescriptionInfo withLanguage(LanguageType value) {
            setLanguage(value);
            return this;
        }

        public ListDescriptionInfo withListDescriptionShort(String value) {
            setListDescriptionShort(value);
            return this;
        }

        public ListDescriptionInfo withListDescription(String value) {
            setListDescription(value);
            return this;
        }

    }

}
