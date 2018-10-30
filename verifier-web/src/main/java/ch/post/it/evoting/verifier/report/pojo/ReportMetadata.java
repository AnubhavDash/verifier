package ch.post.it.evoting.verifier.report.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportMetadata {
    private String title;
    private String headerTitleLabel;
    private String headerTitle;
    private String reportDateLabel;
    private String reportDate;
    private String reportTimeLabel;
    private String reportTime;

    private String commentLabel;
    private String signaturetLabel;
    private String placeDatetLabel;
    private String lastNameLabel;
    private String firstNameLabel;
    private String footerTitleLabel;
    private String footerTitle;
    private String footerDateLabel;
    private String footerDate;

}
