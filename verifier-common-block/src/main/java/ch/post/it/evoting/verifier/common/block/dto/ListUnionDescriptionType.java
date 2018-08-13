
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour listUnionDescriptionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listUnionDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listUnionDescriptionInfo" type="{http://www.evoting.ch/xmlns/config/3}listUnionDescriptionInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listUnionDescriptionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "listUnionDescriptionInfo"
})
public class ListUnionDescriptionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<ListUnionDescriptionInfoType> listUnionDescriptionInfo;

    /**
     * Gets the value of the listUnionDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listUnionDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListUnionDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListUnionDescriptionInfoType }
     * 
     * 
     */
    public List<ListUnionDescriptionInfoType> getListUnionDescriptionInfo() {
        if (listUnionDescriptionInfo == null) {
            listUnionDescriptionInfo = new ArrayList<ListUnionDescriptionInfoType>();
        }
        return this.listUnionDescriptionInfo;
    }

}
