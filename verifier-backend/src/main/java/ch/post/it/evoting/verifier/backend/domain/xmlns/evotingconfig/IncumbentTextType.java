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
 * <p>Classe Java pour incumbentTextType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="incumbentTextType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="incumbentTextInfo" type="{http://www.evoting.ch/xmlns/config/4}incumbentTextInfoType" maxOccurs="unbounded" minOccurs="4"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "incumbentTextType", propOrder = {
    "incumbentTextInfo"
})
public class IncumbentTextType {

    @XmlElement(required = true)
    protected List<IncumbentTextInfoType> incumbentTextInfo;

    /**
     * Gets the value of the incumbentTextInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the incumbentTextInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncumbentTextInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IncumbentTextInfoType }
     * 
     * 
     */
    public List<IncumbentTextInfoType> getIncumbentTextInfo() {
        if (incumbentTextInfo == null) {
            incumbentTextInfo = new ArrayList<IncumbentTextInfoType>();
        }
        return this.incumbentTextInfo;
    }

    public IncumbentTextType withIncumbentTextInfo(IncumbentTextInfoType... values) {
        if (values!= null) {
            for (IncumbentTextInfoType value: values) {
                getIncumbentTextInfo().add(value);
            }
        }
        return this;
    }

    public IncumbentTextType withIncumbentTextInfo(Collection<IncumbentTextInfoType> values) {
        if (values!= null) {
            getIncumbentTextInfo().addAll(values);
        }
        return this;
    }

    public void setIncumbentTextInfo(List<IncumbentTextInfoType> value) {
        this.incumbentTextInfo = null;
        if (value!= null) {
            List<IncumbentTextInfoType> draftl = this.getIncumbentTextInfo();
            draftl.addAll(value);
        }
    }

}
