
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour voteDescriptionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="voteDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="voteDescriptionInfo" type="{http://www.evoting.ch/xmlns/config/3}voteDescriptionInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voteDescriptionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "voteDescriptionInfo"
})
public class VoteDescriptionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<VoteDescriptionInfoType> voteDescriptionInfo;

    /**
     * Gets the value of the voteDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the voteDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVoteDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VoteDescriptionInfoType }
     * 
     * 
     */
    public List<VoteDescriptionInfoType> getVoteDescriptionInfo() {
        if (voteDescriptionInfo == null) {
            voteDescriptionInfo = new ArrayList<VoteDescriptionInfoType>();
        }
        return this.voteDescriptionInfo;
    }

}
