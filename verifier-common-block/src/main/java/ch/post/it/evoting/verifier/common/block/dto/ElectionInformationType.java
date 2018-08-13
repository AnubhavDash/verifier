
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour electionInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electionInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="election" type="{http://www.evoting.ch/xmlns/config/3}electionType"/>
 *         &lt;element name="candidate" type="{http://www.evoting.ch/xmlns/config/3}candidateType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="list" type="{http://www.evoting.ch/xmlns/config/3}listType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="listUnion" type="{http://www.evoting.ch/xmlns/config/3}listUnionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "electionInformationType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "election",
    "candidate",
    "list",
    "listUnion"
})
public class ElectionInformationType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected ElectionType election;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<CandidateType> candidate;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<ListType> list;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<ListUnionType> listUnion;

    /**
     * Obtient la valeur de la propriété election.
     * 
     * @return
     *     possible object is
     *     {@link ElectionType }
     *     
     */
    public ElectionType getElection() {
        return election;
    }

    /**
     * Définit la valeur de la propriété election.
     * 
     * @param value
     *     allowed object is
     *     {@link ElectionType }
     *     
     */
    public void setElection(ElectionType value) {
        this.election = value;
    }

    /**
     * Gets the value of the candidate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the candidate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCandidate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CandidateType }
     * 
     * 
     */
    public List<CandidateType> getCandidate() {
        if (candidate == null) {
            candidate = new ArrayList<CandidateType>();
        }
        return this.candidate;
    }

    /**
     * Gets the value of the list property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the list property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListType }
     * 
     * 
     */
    public List<ListType> getList() {
        if (list == null) {
            list = new ArrayList<ListType>();
        }
        return this.list;
    }

    /**
     * Gets the value of the listUnion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listUnion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListUnion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListUnionType }
     * 
     * 
     */
    public List<ListUnionType> getListUnion() {
        if (listUnion == null) {
            listUnion = new ArrayList<ListUnionType>();
        }
        return this.listUnion;
    }

}
