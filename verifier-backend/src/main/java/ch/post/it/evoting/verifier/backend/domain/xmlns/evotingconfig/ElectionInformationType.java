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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour electionInformationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electionInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="election" type="{http://www.evoting.ch/xmlns/config/4}electionType"/&gt;
 *         &lt;element name="candidate" type="{http://www.evoting.ch/xmlns/config/4}candidateType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="list" type="{http://www.evoting.ch/xmlns/config/4}listType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="listUnion" type="{http://www.evoting.ch/xmlns/config/4}listUnionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "electionInformationType", propOrder = {
    "election",
    "candidate",
    "list",
    "listUnion"
})
public class ElectionInformationType {

    @XmlElement(required = true)
    protected ElectionType election;
    protected List<CandidateType> candidate;
    protected List<ListType> list;
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

    public ElectionInformationType withElection(ElectionType value) {
        setElection(value);
        return this;
    }

    public ElectionInformationType withCandidate(CandidateType... values) {
        if (values!= null) {
            for (CandidateType value: values) {
                getCandidate().add(value);
            }
        }
        return this;
    }

    public ElectionInformationType withCandidate(Collection<CandidateType> values) {
        if (values!= null) {
            getCandidate().addAll(values);
        }
        return this;
    }

    public ElectionInformationType withList(ListType... values) {
        if (values!= null) {
            for (ListType value: values) {
                getList().add(value);
            }
        }
        return this;
    }

    public ElectionInformationType withList(Collection<ListType> values) {
        if (values!= null) {
            getList().addAll(values);
        }
        return this;
    }

    public ElectionInformationType withListUnion(ListUnionType... values) {
        if (values!= null) {
            for (ListUnionType value: values) {
                getListUnion().add(value);
            }
        }
        return this;
    }

    public ElectionInformationType withListUnion(Collection<ListUnionType> values) {
        if (values!= null) {
            getListUnion().addAll(values);
        }
        return this;
    }

    public void setCandidate(List<CandidateType> value) {
        this.candidate = null;
        if (value!= null) {
            List<CandidateType> draftl = this.getCandidate();
            draftl.addAll(value);
        }
    }

    public void setList(List<ListType> value) {
        this.list = null;
        if (value!= null) {
            List<ListType> draftl = this.getList();
            draftl.addAll(value);
        }
    }

    public void setListUnion(List<ListUnionType> value) {
        this.listUnion = null;
        if (value!= null) {
            List<ListUnionType> draftl = this.getListUnion();
            draftl.addAll(value);
        }
    }

}
