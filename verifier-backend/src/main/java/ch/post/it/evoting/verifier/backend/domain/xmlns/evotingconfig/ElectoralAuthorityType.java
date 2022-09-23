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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour electoralAuthorityType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electoralAuthorityType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="electoralAuthorityIdentification"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;maxLength value="50"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="electoralAuthorityName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="electoralAuthorityDescription"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="electoralAuthorityThresholdValue" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="electoralAuthorityMembers" type="{http://www.evoting.ch/xmlns/config/4}electoralAuthorityMembersType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "electoralAuthorityType", propOrder = {
    "electoralAuthorityIdentification",
    "electoralAuthorityName",
    "electoralAuthorityDescription",
    "electoralAuthorityThresholdValue",
    "electoralAuthorityMembers"
})
public class ElectoralAuthorityType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String electoralAuthorityIdentification;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String electoralAuthorityName;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String electoralAuthorityDescription;
    @XmlElement(required = true, defaultValue = "2")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger electoralAuthorityThresholdValue;
    @XmlElement(required = true)
    protected ElectoralAuthorityMembersType electoralAuthorityMembers;

    /**
     * Obtient la valeur de la propriété electoralAuthorityIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElectoralAuthorityIdentification() {
        return electoralAuthorityIdentification;
    }

    /**
     * Définit la valeur de la propriété electoralAuthorityIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElectoralAuthorityIdentification(String value) {
        this.electoralAuthorityIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété electoralAuthorityName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElectoralAuthorityName() {
        return electoralAuthorityName;
    }

    /**
     * Définit la valeur de la propriété electoralAuthorityName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElectoralAuthorityName(String value) {
        this.electoralAuthorityName = value;
    }

    /**
     * Obtient la valeur de la propriété electoralAuthorityDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElectoralAuthorityDescription() {
        return electoralAuthorityDescription;
    }

    /**
     * Définit la valeur de la propriété electoralAuthorityDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElectoralAuthorityDescription(String value) {
        this.electoralAuthorityDescription = value;
    }

    /**
     * Obtient la valeur de la propriété electoralAuthorityThresholdValue.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getElectoralAuthorityThresholdValue() {
        return electoralAuthorityThresholdValue;
    }

    /**
     * Définit la valeur de la propriété electoralAuthorityThresholdValue.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setElectoralAuthorityThresholdValue(BigInteger value) {
        this.electoralAuthorityThresholdValue = value;
    }

    /**
     * Obtient la valeur de la propriété electoralAuthorityMembers.
     * 
     * @return
     *     possible object is
     *     {@link ElectoralAuthorityMembersType }
     *     
     */
    public ElectoralAuthorityMembersType getElectoralAuthorityMembers() {
        return electoralAuthorityMembers;
    }

    /**
     * Définit la valeur de la propriété electoralAuthorityMembers.
     * 
     * @param value
     *     allowed object is
     *     {@link ElectoralAuthorityMembersType }
     *     
     */
    public void setElectoralAuthorityMembers(ElectoralAuthorityMembersType value) {
        this.electoralAuthorityMembers = value;
    }

    public ElectoralAuthorityType withElectoralAuthorityIdentification(String value) {
        setElectoralAuthorityIdentification(value);
        return this;
    }

    public ElectoralAuthorityType withElectoralAuthorityName(String value) {
        setElectoralAuthorityName(value);
        return this;
    }

    public ElectoralAuthorityType withElectoralAuthorityDescription(String value) {
        setElectoralAuthorityDescription(value);
        return this;
    }

    public ElectoralAuthorityType withElectoralAuthorityThresholdValue(BigInteger value) {
        setElectoralAuthorityThresholdValue(value);
        return this;
    }

    public ElectoralAuthorityType withElectoralAuthorityMembers(ElectoralAuthorityMembersType value) {
        setElectoralAuthorityMembers(value);
        return this;
    }

}
