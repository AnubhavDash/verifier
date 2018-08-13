
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
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
 * &lt;complexType name="registerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="voter" type="{http://www.evoting.ch/xmlns/config/3}voterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registerType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "voter"
})
public class RegisterType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
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

}
