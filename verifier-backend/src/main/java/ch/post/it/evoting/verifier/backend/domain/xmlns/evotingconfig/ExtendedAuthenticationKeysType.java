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
 * <p>Classe Java pour extendedAuthenticationKeysType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="extendedAuthenticationKeysType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="extendedAuthenticationKey" type="{http://www.evoting.ch/xmlns/config/4}extendedAuthenticationKeyType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedAuthenticationKeysType", propOrder = {
    "extendedAuthenticationKey"
})
public class ExtendedAuthenticationKeysType {

    @XmlElement(required = true)
    protected List<ExtendedAuthenticationKeyType> extendedAuthenticationKey;

    /**
     * Gets the value of the extendedAuthenticationKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extendedAuthenticationKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtendedAuthenticationKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtendedAuthenticationKeyType }
     * 
     * 
     */
    public List<ExtendedAuthenticationKeyType> getExtendedAuthenticationKey() {
        if (extendedAuthenticationKey == null) {
            extendedAuthenticationKey = new ArrayList<ExtendedAuthenticationKeyType>();
        }
        return this.extendedAuthenticationKey;
    }

    public ExtendedAuthenticationKeysType withExtendedAuthenticationKey(ExtendedAuthenticationKeyType... values) {
        if (values!= null) {
            for (ExtendedAuthenticationKeyType value: values) {
                getExtendedAuthenticationKey().add(value);
            }
        }
        return this;
    }

    public ExtendedAuthenticationKeysType withExtendedAuthenticationKey(Collection<ExtendedAuthenticationKeyType> values) {
        if (values!= null) {
            getExtendedAuthenticationKey().addAll(values);
        }
        return this;
    }

    public void setExtendedAuthenticationKey(List<ExtendedAuthenticationKeyType> value) {
        this.extendedAuthenticationKey = null;
        if (value!= null) {
            List<ExtendedAuthenticationKeyType> draftl = this.getExtendedAuthenticationKey();
            draftl.addAll(value);
        }
    }

}
