
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour headerType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="headerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="voterTotal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "headerType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "fileDate",
    "voterTotal"
})
public class HeaderType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String fileDate;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String voterTotal;

    /**
     * Obtient la valeur de la propriété fileDate.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileDate() {
        return fileDate;
    }

    /**
     * Définit la valeur de la propriété fileDate.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileDate(String value) {
        this.fileDate = value;
    }

    /**
     * Obtient la valeur de la propriété voterTotal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoterTotal() {
        return voterTotal;
    }

    /**
     * Définit la valeur de la propriété voterTotal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoterTotal(String value) {
        this.voterTotal = value;
    }

}
