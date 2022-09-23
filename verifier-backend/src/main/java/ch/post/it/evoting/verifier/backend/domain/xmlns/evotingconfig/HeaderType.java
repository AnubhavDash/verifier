//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour headerType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="headerType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fileDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="voterTotal" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="partialDelivery" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="voterFrom" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *                   &lt;element name="voterTo" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
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
@XmlType(name = "headerType", propOrder = {
    "fileDate",
    "voterTotal",
    "partialDelivery"
})
public class HeaderType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fileDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger voterTotal;
    protected PartialDelivery partialDelivery;

    /**
     * Obtient la valeur de la propriété fileDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFileDate() {
        return fileDate;
    }

    /**
     * Définit la valeur de la propriété fileDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFileDate(XMLGregorianCalendar value) {
        this.fileDate = value;
    }

    /**
     * Obtient la valeur de la propriété voterTotal.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVoterTotal() {
        return voterTotal;
    }

    /**
     * Définit la valeur de la propriété voterTotal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVoterTotal(BigInteger value) {
        this.voterTotal = value;
    }

    /**
     * Obtient la valeur de la propriété partialDelivery.
     * 
     * @return
     *     possible object is
     *     {@link PartialDelivery }
     *     
     */
    public PartialDelivery getPartialDelivery() {
        return partialDelivery;
    }

    /**
     * Définit la valeur de la propriété partialDelivery.
     * 
     * @param value
     *     allowed object is
     *     {@link PartialDelivery }
     *     
     */
    public void setPartialDelivery(PartialDelivery value) {
        this.partialDelivery = value;
    }

    public HeaderType withFileDate(XMLGregorianCalendar value) {
        setFileDate(value);
        return this;
    }

    public HeaderType withVoterTotal(BigInteger value) {
        setVoterTotal(value);
        return this;
    }

    public HeaderType withPartialDelivery(PartialDelivery value) {
        setPartialDelivery(value);
        return this;
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
     *         &lt;element name="voterFrom" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
     *         &lt;element name="voterTo" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
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
        "voterFrom",
        "voterTo"
    })
    public static class PartialDelivery {

        @XmlElement(required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger voterFrom;
        @XmlElement(required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected BigInteger voterTo;

        /**
         * Obtient la valeur de la propriété voterFrom.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getVoterFrom() {
            return voterFrom;
        }

        /**
         * Définit la valeur de la propriété voterFrom.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setVoterFrom(BigInteger value) {
            this.voterFrom = value;
        }

        /**
         * Obtient la valeur de la propriété voterTo.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getVoterTo() {
            return voterTo;
        }

        /**
         * Définit la valeur de la propriété voterTo.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setVoterTo(BigInteger value) {
            this.voterTo = value;
        }

        public PartialDelivery withVoterFrom(BigInteger value) {
            setVoterFrom(value);
            return this;
        }

        public PartialDelivery withVoterTo(BigInteger value) {
            setVoterTo(value);
            return this;
        }

    }

}
