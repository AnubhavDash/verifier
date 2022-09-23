//
// Ce fichier a été généré par Eclipse Implementation of JAXB, v2.3.6 
// Voir https://eclipse-ee4j.github.io/jaxb-ri 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2022.09.21 à 10:29:08 AM CEST 
//


package ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour frankingAreaType.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <pre>
 * &lt;simpleType name="frankingAreaType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SWITZERLAND"/&gt;
 *     &lt;enumeration value="GERMANY"/&gt;
 *     &lt;enumeration value="EUROPE"/&gt;
 *     &lt;enumeration value="OVERSEA"/&gt;
 *     &lt;enumeration value="OTHER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "frankingAreaType")
@XmlEnum
public enum FrankingAreaType {

    SWITZERLAND,
    GERMANY,
    EUROPE,
    OVERSEA,
    OTHER;

    public String value() {
        return name();
    }

    public static FrankingAreaType fromValue(String v) {
        return valueOf(v);
    }

}
