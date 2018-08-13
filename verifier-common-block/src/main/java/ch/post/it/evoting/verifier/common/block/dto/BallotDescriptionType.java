
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ballotDescriptionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ballotDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ballotDescriptionInfo" type="{http://www.evoting.ch/xmlns/config/3}ballotDescriptionInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ballotDescriptionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "ballotDescriptionInfo"
})
public class BallotDescriptionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<BallotDescriptionInfoType> ballotDescriptionInfo;

    /**
     * Gets the value of the ballotDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ballotDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBallotDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BallotDescriptionInfoType }
     * 
     * 
     */
    public List<BallotDescriptionInfoType> getBallotDescriptionInfo() {
        if (ballotDescriptionInfo == null) {
            ballotDescriptionInfo = new ArrayList<BallotDescriptionInfoType>();
        }
        return this.ballotDescriptionInfo;
    }

}
