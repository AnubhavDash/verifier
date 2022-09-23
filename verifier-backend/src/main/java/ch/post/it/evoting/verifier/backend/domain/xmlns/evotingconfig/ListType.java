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
 * <p>Classe Java pour listType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="listIdentification" type="{http://www.evoting.ch/xmlns/config/4}identifierType"/&gt;
 *         &lt;element name="listIndentureNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="6"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="listDescription" type="{http://www.evoting.ch/xmlns/config/4}listDescriptionInformationType"/&gt;
 *         &lt;element name="listOrderOfPrecedence" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="listEmpty" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="candidatePosition" type="{http://www.evoting.ch/xmlns/config/4}candidatePositionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="varListText1" type="{http://www.evoting.ch/xmlns/config/4}varListTextType" minOccurs="0"/&gt;
 *         &lt;element name="varListText2" type="{http://www.evoting.ch/xmlns/config/4}varListTextType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listType", propOrder = {
    "listIdentification",
    "listIndentureNumber",
    "listDescription",
    "listOrderOfPrecedence",
    "listEmpty",
    "candidatePosition",
    "varListText1",
    "varListText2"
})
public class ListType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String listIdentification;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String listIndentureNumber;
    @XmlElement(required = true)
    protected ListDescriptionInformationType listDescription;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger listOrderOfPrecedence;
    protected boolean listEmpty;
    protected List<CandidatePositionType> candidatePosition;
    protected VarListTextType varListText1;
    protected VarListTextType varListText2;

    /**
     * Obtient la valeur de la propriété listIdentification.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListIdentification() {
        return listIdentification;
    }

    /**
     * Définit la valeur de la propriété listIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListIdentification(String value) {
        this.listIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété listIndentureNumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListIndentureNumber() {
        return listIndentureNumber;
    }

    /**
     * Définit la valeur de la propriété listIndentureNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListIndentureNumber(String value) {
        this.listIndentureNumber = value;
    }

    /**
     * Obtient la valeur de la propriété listDescription.
     * 
     * @return
     *     possible object is
     *     {@link ListDescriptionInformationType }
     *     
     */
    public ListDescriptionInformationType getListDescription() {
        return listDescription;
    }

    /**
     * Définit la valeur de la propriété listDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ListDescriptionInformationType }
     *     
     */
    public void setListDescription(ListDescriptionInformationType value) {
        this.listDescription = value;
    }

    /**
     * Obtient la valeur de la propriété listOrderOfPrecedence.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getListOrderOfPrecedence() {
        return listOrderOfPrecedence;
    }

    /**
     * Définit la valeur de la propriété listOrderOfPrecedence.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setListOrderOfPrecedence(BigInteger value) {
        this.listOrderOfPrecedence = value;
    }

    /**
     * Obtient la valeur de la propriété listEmpty.
     * 
     */
    public boolean isListEmpty() {
        return listEmpty;
    }

    /**
     * Définit la valeur de la propriété listEmpty.
     * 
     */
    public void setListEmpty(boolean value) {
        this.listEmpty = value;
    }

    /**
     * Gets the value of the candidatePosition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the candidatePosition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCandidatePosition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CandidatePositionType }
     * 
     * 
     */
    public List<CandidatePositionType> getCandidatePosition() {
        if (candidatePosition == null) {
            candidatePosition = new ArrayList<CandidatePositionType>();
        }
        return this.candidatePosition;
    }

    /**
     * Obtient la valeur de la propriété varListText1.
     * 
     * @return
     *     possible object is
     *     {@link VarListTextType }
     *     
     */
    public VarListTextType getVarListText1() {
        return varListText1;
    }

    /**
     * Définit la valeur de la propriété varListText1.
     * 
     * @param value
     *     allowed object is
     *     {@link VarListTextType }
     *     
     */
    public void setVarListText1(VarListTextType value) {
        this.varListText1 = value;
    }

    /**
     * Obtient la valeur de la propriété varListText2.
     * 
     * @return
     *     possible object is
     *     {@link VarListTextType }
     *     
     */
    public VarListTextType getVarListText2() {
        return varListText2;
    }

    /**
     * Définit la valeur de la propriété varListText2.
     * 
     * @param value
     *     allowed object is
     *     {@link VarListTextType }
     *     
     */
    public void setVarListText2(VarListTextType value) {
        this.varListText2 = value;
    }

    public ListType withListIdentification(String value) {
        setListIdentification(value);
        return this;
    }

    public ListType withListIndentureNumber(String value) {
        setListIndentureNumber(value);
        return this;
    }

    public ListType withListDescription(ListDescriptionInformationType value) {
        setListDescription(value);
        return this;
    }

    public ListType withListOrderOfPrecedence(BigInteger value) {
        setListOrderOfPrecedence(value);
        return this;
    }

    public ListType withListEmpty(boolean value) {
        setListEmpty(value);
        return this;
    }

    public ListType withCandidatePosition(CandidatePositionType... values) {
        if (values!= null) {
            for (CandidatePositionType value: values) {
                getCandidatePosition().add(value);
            }
        }
        return this;
    }

    public ListType withCandidatePosition(Collection<CandidatePositionType> values) {
        if (values!= null) {
            getCandidatePosition().addAll(values);
        }
        return this;
    }

    public ListType withVarListText1(VarListTextType value) {
        setVarListText1(value);
        return this;
    }

    public ListType withVarListText2(VarListTextType value) {
        setVarListText2(value);
        return this;
    }

    public void setCandidatePosition(List<CandidatePositionType> value) {
        this.candidatePosition = null;
        if (value!= null) {
            List<CandidatePositionType> draftl = this.getCandidatePosition();
            draftl.addAll(value);
        }
    }

}
