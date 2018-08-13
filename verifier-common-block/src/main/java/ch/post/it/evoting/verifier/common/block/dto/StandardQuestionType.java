
package ch.post.it.evoting.verifier.common.block.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour standardQuestionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="standardQuestionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="questionIdentification">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Q1"/>
 *               &lt;enumeration value="Q2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="questionPosition">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="answerType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ballotQuestion" type="{http://www.evoting.ch/xmlns/config/3}ballotQuestionType"/>
 *         &lt;element name="answer" type="{http://www.evoting.ch/xmlns/config/3}answerType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "standardQuestionType", namespace = "http://www.evoting.ch/xmlns/config/3", propOrder = {
    "questionIdentificationOrQuestionPositionOrAnswerType"
})
public class StandardQuestionType {

    @XmlElementRefs({
        @XmlElementRef(name = "answerType", namespace = "http://www.evoting.ch/xmlns/config/3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "questionPosition", namespace = "http://www.evoting.ch/xmlns/config/3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "questionIdentification", namespace = "http://www.evoting.ch/xmlns/config/3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "answer", namespace = "http://www.evoting.ch/xmlns/config/3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ballotQuestion", namespace = "http://www.evoting.ch/xmlns/config/3", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> questionIdentificationOrQuestionPositionOrAnswerType;

    /**
     * Gets the value of the questionIdentificationOrQuestionPositionOrAnswerType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the questionIdentificationOrQuestionPositionOrAnswerType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuestionIdentificationOrQuestionPositionOrAnswerType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link AnswerType }{@code >}
     * {@link JAXBElement }{@code <}{@link BallotQuestionType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getQuestionIdentificationOrQuestionPositionOrAnswerType() {
        if (questionIdentificationOrQuestionPositionOrAnswerType == null) {
            questionIdentificationOrQuestionPositionOrAnswerType = new ArrayList<JAXBElement<?>>();
        }
        return this.questionIdentificationOrQuestionPositionOrAnswerType;
    }

}
