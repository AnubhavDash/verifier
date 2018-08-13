
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour electionDescriptionInfoType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="electionDescriptionInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="language">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="de"/>
 *               &lt;enumeration value="fr"/>
 *               &lt;enumeration value="it"/>
 *               &lt;enumeration value="rm"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="electionDescriptionShort">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Nationalrat"/>
 *               &lt;enumeration value="Conseil national"/>
 *               &lt;enumeration value="Demo Consiglio nationale"/>
 *               &lt;enumeration value="Demo Cussegl natiunal"/>
 *               &lt;enumeration value="Ständerat"/>
 *               &lt;enumeration value="Conseil des Etats"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="electionDescription">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Nationalratswahl"/>
 *               &lt;enumeration value="Election du Conseil national"/>
 *               &lt;enumeration value="Elezione del Consiglio nationale"/>
 *               &lt;enumeration value="Elecziun dal Cussegl natiunal"/>
 *               &lt;enumeration value="Ständeratswahl"/>
 *               &lt;enumeration value="Election du Conseil des Etats"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "electionDescriptionInfoType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "language",
    "electionDescriptionShort",
    "electionDescription"
})
public class ElectionDescriptionInfoType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String language;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String electionDescriptionShort;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String electionDescription;

    /**
     * Obtient la valeur de la propriété language.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Définit la valeur de la propriété language.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Obtient la valeur de la propriété electionDescriptionShort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElectionDescriptionShort() {
        return electionDescriptionShort;
    }

    /**
     * Définit la valeur de la propriété electionDescriptionShort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElectionDescriptionShort(String value) {
        this.electionDescriptionShort = value;
    }

    /**
     * Obtient la valeur de la propriété electionDescription.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElectionDescription() {
        return electionDescription;
    }

    /**
     * Définit la valeur de la propriété electionDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElectionDescription(String value) {
        this.electionDescription = value;
    }

}
