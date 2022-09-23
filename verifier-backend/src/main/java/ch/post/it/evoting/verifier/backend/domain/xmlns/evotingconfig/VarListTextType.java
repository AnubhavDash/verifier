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
 * <p>Classe Java pour varListTextType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="varListTextType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="varListTextInfo" type="{http://www.evoting.ch/xmlns/config/4}varListTextInfoType" maxOccurs="unbounded" minOccurs="4"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "varListTextType", propOrder = {
    "varListTextInfo"
})
public class VarListTextType {

    @XmlElement(required = true)
    protected List<VarListTextInfoType> varListTextInfo;

    /**
     * Gets the value of the varListTextInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the varListTextInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVarListTextInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VarListTextInfoType }
     * 
     * 
     */
    public List<VarListTextInfoType> getVarListTextInfo() {
        if (varListTextInfo == null) {
            varListTextInfo = new ArrayList<VarListTextInfoType>();
        }
        return this.varListTextInfo;
    }

    public VarListTextType withVarListTextInfo(VarListTextInfoType... values) {
        if (values!= null) {
            for (VarListTextInfoType value: values) {
                getVarListTextInfo().add(value);
            }
        }
        return this;
    }

    public VarListTextType withVarListTextInfo(Collection<VarListTextInfoType> values) {
        if (values!= null) {
            getVarListTextInfo().addAll(values);
        }
        return this;
    }

    public void setVarListTextInfo(List<VarListTextInfoType> value) {
        this.varListTextInfo = null;
        if (value!= null) {
            List<VarListTextInfoType> draftl = this.getVarListTextInfo();
            draftl.addAll(value);
        }
    }

}
