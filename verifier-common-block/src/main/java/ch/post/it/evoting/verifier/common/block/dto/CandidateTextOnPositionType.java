
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour candidateTextOnPositionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="candidateTextOnPositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="candidateTextInfo" type="{http://www.evoting.ch/xmlns/config/3}candidateTextInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "candidateTextOnPositionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "candidateTextInfo"
})
public class CandidateTextOnPositionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<CandidateTextInfoType> candidateTextInfo;

    /**
     * Gets the value of the candidateTextInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the candidateTextInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCandidateTextInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CandidateTextInfoType }
     * 
     * 
     */
    public List<CandidateTextInfoType> getCandidateTextInfo() {
        if (candidateTextInfo == null) {
            candidateTextInfo = new ArrayList<CandidateTextInfoType>();
        }
        return this.candidateTextInfo;
    }

}
