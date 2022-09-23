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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour extendedAuthenticationKeysDefinitionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="extendedAuthenticationKeysDefinitionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="keyName" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedAuthenticationKeysDefinitionType", propOrder = {
    "keyName"
})
public class ExtendedAuthenticationKeysDefinitionType {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> keyName;

    /**
     * Gets the value of the keyName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keyName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeyName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getKeyName() {
        if (keyName == null) {
            keyName = new ArrayList<String>();
        }
        return this.keyName;
    }

    public ExtendedAuthenticationKeysDefinitionType withKeyName(String... values) {
        if (values!= null) {
            for (String value: values) {
                getKeyName().add(value);
            }
        }
        return this;
    }

    public ExtendedAuthenticationKeysDefinitionType withKeyName(Collection<String> values) {
        if (values!= null) {
            getKeyName().addAll(values);
        }
        return this;
    }

    public void setKeyName(List<String> value) {
        this.keyName = null;
        if (value!= null) {
            List<String> draftl = this.getKeyName();
            draftl.addAll(value);
        }
    }

}
