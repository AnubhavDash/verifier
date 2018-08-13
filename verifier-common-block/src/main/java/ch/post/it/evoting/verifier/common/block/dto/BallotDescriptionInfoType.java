
package ch.post.it.evoting.verifier.common.block.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ballotDescriptionInfoType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ballotDescriptionInfoType">
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
 *         &lt;element name="ballotDescriptionLong">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Volksinitiative «Volksinitiative A»"/>
 *               &lt;enumeration value="Initiative populaire «Initiative populaire A»"/>
 *               &lt;enumeration value="Iniziativa popolare «Iniziativa popolare A»"/>
 *               &lt;enumeration value="Iniziativa dal pievel «Iniziativa dal pievel A»"/>
 *               &lt;enumeration value="Demo Bundesbeschluss C"/>
 *               &lt;enumeration value="Démo Arrêté fédéral C"/>
 *               &lt;enumeration value="Demo Decreto federale C"/>
 *               &lt;enumeration value="Demo Conclus federal C"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ballotDescriptionShort">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Initiative A"/>
 *               &lt;enumeration value="Iniziativa A"/>
 *               &lt;enumeration value="Demo Bundesbeschluss C"/>
 *               &lt;enumeration value="Démo Arrêté fédéral C"/>
 *               &lt;enumeration value="Demo Decreto federale C"/>
 *               &lt;enumeration value="Demo Conclus federal C"/>
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
@XmlType(name = "ballotDescriptionInfoType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "language",
    "ballotDescriptionLong",
    "ballotDescriptionShort"
})
public class BallotDescriptionInfoType {

    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String language;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String ballotDescriptionLong;
    @XmlElement(namespace = "http://www.evoting.ch/xmlns/config/3", required = true)
    protected String ballotDescriptionShort;

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
     * Obtient la valeur de la propriété ballotDescriptionLong.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBallotDescriptionLong() {
        return ballotDescriptionLong;
    }

    /**
     * Définit la valeur de la propriété ballotDescriptionLong.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBallotDescriptionLong(String value) {
        this.ballotDescriptionLong = value;
    }

    /**
     * Obtient la valeur de la propriété ballotDescriptionShort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBallotDescriptionShort() {
        return ballotDescriptionShort;
    }

    /**
     * Définit la valeur de la propriété ballotDescriptionShort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBallotDescriptionShort(String value) {
        this.ballotDescriptionShort = value;
    }

}
