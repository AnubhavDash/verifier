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
 * <p>Classe Java pour authorizationsType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="authorizationsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="authorization" type="{http://www.evoting.ch/xmlns/config/4}authorizationType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "authorizationsType", propOrder = {
    "authorization"
})
public class AuthorizationsType {

    @XmlElement(required = true)
    protected List<AuthorizationType> authorization;

    /**
     * Gets the value of the authorization property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authorization property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthorization().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthorizationType }
     * 
     * 
     */
    public List<AuthorizationType> getAuthorization() {
        if (authorization == null) {
            authorization = new ArrayList<AuthorizationType>();
        }
        return this.authorization;
    }

    public AuthorizationsType withAuthorization(AuthorizationType... values) {
        if (values!= null) {
            for (AuthorizationType value: values) {
                getAuthorization().add(value);
            }
        }
        return this;
    }

    public AuthorizationsType withAuthorization(Collection<AuthorizationType> values) {
        if (values!= null) {
            getAuthorization().addAll(values);
        }
        return this;
    }

    public void setAuthorization(List<AuthorizationType> value) {
        this.authorization = null;
        if (value!= null) {
            List<AuthorizationType> draftl = this.getAuthorization();
            draftl.addAll(value);
        }
    }

}
