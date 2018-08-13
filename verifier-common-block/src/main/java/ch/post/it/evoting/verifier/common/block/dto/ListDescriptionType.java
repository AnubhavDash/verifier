
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour listDescriptionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="listDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listDescriptionInfo" type="{http://www.evoting.ch/xmlns/config/3}listDescriptionInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listDescriptionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "listDescriptionInfo"
})
public class ListDescriptionType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3")
    protected List<ListDescriptionInfoType> listDescriptionInfo;

    /**
     * Gets the value of the listDescriptionInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listDescriptionInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListDescriptionInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListDescriptionInfoType }
     * 
     * 
     */
    public List<ListDescriptionInfoType> getListDescriptionInfo() {
        if (listDescriptionInfo == null) {
            listDescriptionInfo = new ArrayList<ListDescriptionInfoType>();
        }
        return this.listDescriptionInfo;
    }

}
