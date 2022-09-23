//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import java.math.BigInteger;
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
 * <p>Classe Java pour listUnionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listUnionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="listUnionIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="listUnionDescription" type="{http://www.evoting.ch/xmlns/config/4}listUnionDescriptionType"/&gt;
 *         &lt;element name="listUnionType"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="referencedList" type="{http://www.evoting.ch/xmlns/config/4}identifierType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listUnionType", propOrder = {
    "listUnionIdentification",
    "listUnionDescription",
    "listUnionType",
    "referencedList"
})
public class ListUnionType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String listUnionIdentification;
    @XmlElement(required = true)
    protected ListUnionDescriptionType listUnionDescription;
    @XmlElement(required = true)
    protected BigInteger listUnionType;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> referencedList;

    /**
     * Obtient la valeur de la propriété listUnionIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListUnionIdentification() {
        return listUnionIdentification;
    }

    /**
     * Définit la valeur de la propriété listUnionIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListUnionIdentification(String value) {
        this.listUnionIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété listUnionDescription.
     * 
     * @return
     *     possible object is
     *     {@link ListUnionDescriptionType }
     *     
     */
    public ListUnionDescriptionType getListUnionDescription() {
        return listUnionDescription;
    }

    /**
     * Définit la valeur de la propriété listUnionDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ListUnionDescriptionType }
     *     
     */
    public void setListUnionDescription(ListUnionDescriptionType value) {
        this.listUnionDescription = value;
    }

    /**
     * Obtient la valeur de la propriété listUnionType.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getListUnionType() {
        return listUnionType;
    }

    /**
     * Définit la valeur de la propriété listUnionType.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setListUnionType(BigInteger value) {
        this.listUnionType = value;
    }

    /**
     * Gets the value of the referencedList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referencedList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencedList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getReferencedList() {
        if (referencedList == null) {
            referencedList = new ArrayList<String>();
        }
        return this.referencedList;
    }

    public ListUnionType withListUnionIdentification(String value) {
        setListUnionIdentification(value);
        return this;
    }

    public ListUnionType withListUnionDescription(ListUnionDescriptionType value) {
        setListUnionDescription(value);
        return this;
    }

    public ListUnionType withListUnionType(BigInteger value) {
        setListUnionType(value);
        return this;
    }

    public ListUnionType withReferencedList(String... values) {
        if (values!= null) {
            for (String value: values) {
                getReferencedList().add(value);
            }
        }
        return this;
    }

    public ListUnionType withReferencedList(Collection<String> values) {
        if (values!= null) {
            getReferencedList().addAll(values);
        }
        return this;
    }

    public void setReferencedList(List<String> value) {
        this.referencedList = null;
        if (value!= null) {
            List<String> draftl = this.getReferencedList();
            draftl.addAll(value);
        }
    }

}
