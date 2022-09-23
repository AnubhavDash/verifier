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
 * <p>Classe Java pour occupationalTitleInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="occupationalTitleInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="occupationalTitleInfo" maxOccurs="unbounded" minOccurs="4"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="language" type="{http://www.evoting.ch/xmlns/config/4}languageType"/&gt;
 *                   &lt;element name="occupationalTitle"&gt;
 *                     &lt;simpleType&gt;
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                         &lt;minLength value="1"/&gt;
 *                         &lt;maxLength value="250"/&gt;
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
@XmlType(name = "occupationalTitleInformationType", propOrder = {
    "occupationalTitleInfo"
})
public class OccupationalTitleInformationType {

    @XmlElement(required = true)
    protected List<OccupationalTitleInfo> occupationalTitleInfo;

    /**
     * Gets the value of the occupationalTitleInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the occupationalTitleInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOccupationalTitleInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OccupationalTitleInfo }
     * 
     * 
     */
    public List<OccupationalTitleInfo> getOccupationalTitleInfo() {
        if (occupationalTitleInfo == null) {
            occupationalTitleInfo = new ArrayList<OccupationalTitleInfo>();
        }
        return this.occupationalTitleInfo;
    }

    public OccupationalTitleInformationType withOccupationalTitleInfo(OccupationalTitleInfo... values) {
        if (values!= null) {
            for (OccupationalTitleInfo value: values) {
                getOccupationalTitleInfo().add(value);
            }
        }
        return this;
    }

    public OccupationalTitleInformationType withOccupationalTitleInfo(Collection<OccupationalTitleInfo> values) {
        if (values!= null) {
            getOccupationalTitleInfo().addAll(values);
        }
        return this;
    }

    public void setOccupationalTitleInfo(List<OccupationalTitleInfo> value) {
        this.occupationalTitleInfo = null;
        if (value!= null) {
            List<OccupationalTitleInfo> draftl = this.getOccupationalTitleInfo();
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
     *         &lt;element name="occupationalTitle"&gt;
     *           &lt;simpleType&gt;
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
     *               &lt;minLength value="1"/&gt;
     *               &lt;maxLength value="250"/&gt;
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
        "occupationalTitle"
    })
    public static class OccupationalTitleInfo {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected LanguageType language;
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String occupationalTitle;

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
         * Obtient la valeur de la propriété occupationalTitle.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOccupationalTitle() {
            return occupationalTitle;
        }

        /**
         * Définit la valeur de la propriété occupationalTitle.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOccupationalTitle(String value) {
            this.occupationalTitle = value;
        }

        public OccupationalTitleInfo withLanguage(LanguageType value) {
            setLanguage(value);
            return this;
        }

        public OccupationalTitleInfo withOccupationalTitle(String value) {
            setOccupationalTitle(value);
            return this;
        }

    }

}
