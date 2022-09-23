//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour incumbentType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="incumbentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="incumbent" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="incumbentText" type="{http://www.evoting.ch/xmlns/config/4}incumbentTextType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "incumbentType", propOrder = {
    "incumbent",
    "incumbentText"
})
public class IncumbentType {

    protected boolean incumbent;
    protected IncumbentTextType incumbentText;

    /**
     * Obtient la valeur de la propriété incumbent.
     * 
     */
    public boolean isIncumbent() {
        return incumbent;
    }

    /**
     * Définit la valeur de la propriété incumbent.
     * 
     */
    public void setIncumbent(boolean value) {
        this.incumbent = value;
    }

    /**
     * Obtient la valeur de la propriété incumbentText.
     * 
     * @return
     *     possible object is
     *     {@link IncumbentTextType }
     *     
     */
    public IncumbentTextType getIncumbentText() {
        return incumbentText;
    }

    /**
     * Définit la valeur de la propriété incumbentText.
     * 
     * @param value
     *     allowed object is
     *     {@link IncumbentTextType }
     *     
     */
    public void setIncumbentText(IncumbentTextType value) {
        this.incumbentText = value;
    }

    public IncumbentType withIncumbent(boolean value) {
        setIncumbent(value);
        return this;
    }

    public IncumbentType withIncumbentText(IncumbentTextType value) {
        setIncumbentText(value);
        return this;
    }

}
