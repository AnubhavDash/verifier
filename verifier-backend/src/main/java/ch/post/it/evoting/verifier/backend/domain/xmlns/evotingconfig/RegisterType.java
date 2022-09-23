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
 * <p>Classe Java pour registerType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="registerType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="voter" type="{http://www.evoting.ch/xmlns/config/4}voterType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registerType", propOrder = {
    "voter"
})
public class RegisterType {

    @XmlElement(required = true)
    protected List<VoterType> voter;

    /**
     * Gets the value of the voter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the voter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVoter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VoterType }
     * 
     * 
     */
    public List<VoterType> getVoter() {
        if (voter == null) {
            voter = new ArrayList<VoterType>();
        }
        return this.voter;
    }

    public RegisterType withVoter(VoterType... values) {
        if (values!= null) {
            for (VoterType value: values) {
                getVoter().add(value);
            }
        }
        return this;
    }

    public RegisterType withVoter(Collection<VoterType> values) {
        if (values!= null) {
            getVoter().addAll(values);
        }
        return this;
    }

    public void setVoter(List<VoterType> value) {
        this.voter = null;
        if (value!= null) {
            List<VoterType> draftl = this.getVoter();
            draftl.addAll(value);
        }
    }

}
