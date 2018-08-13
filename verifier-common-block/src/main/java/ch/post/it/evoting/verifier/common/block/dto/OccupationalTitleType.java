
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour occupationalTitleType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="occupationalTitleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="occupationalTitleInfo" type="{http://www.evoting.ch/xmlns/config/3}occupationalTitleInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "occupationalTitleType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "occupationalTitleInfo"
})
public class OccupationalTitleType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<OccupationalTitleInfoType> occupationalTitleInfo;

    /**
     * Gets the value of the occupationalTitleInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the occupationalTitleInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOccupationalTitleInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OccupationalTitleInfoType }
     * 
     * 
     */
    public List<OccupationalTitleInfoType> getOccupationalTitleInfo() {
        if (occupationalTitleInfo == null) {
            occupationalTitleInfo = new ArrayList<OccupationalTitleInfoType>();
        }
        return this.occupationalTitleInfo;
    }

}
