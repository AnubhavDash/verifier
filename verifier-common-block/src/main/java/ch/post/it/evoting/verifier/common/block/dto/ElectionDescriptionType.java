
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour electionDescriptionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electionDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="electionDescriptionInfo" type="{http://www.evoting.ch/xmlns/config/3}electionDescriptionInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "electionDescriptionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "electionDescriptionInfo"
})
public class ElectionDescriptionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<ElectionDescriptionInfoType> electionDescriptionInfo;

    /**
     * Gets the value of the electionDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the electionDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElectionDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElectionDescriptionInfoType }
     * 
     * 
     */
    public List<ElectionDescriptionInfoType> getElectionDescriptionInfo() {
        if (electionDescriptionInfo == null) {
            electionDescriptionInfo = new ArrayList<ElectionDescriptionInfoType>();
        }
        return this.electionDescriptionInfo;
    }

}
