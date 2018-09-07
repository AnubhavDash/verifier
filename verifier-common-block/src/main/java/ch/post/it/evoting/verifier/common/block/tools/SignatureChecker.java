package ch.post.it.evoting.verifier.common.block.tools;

import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.Store;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Collectors;

public class SignatureChecker {

    private static final Logger LOGGER = Logger.getLogger(SignatureChecker.class);

    private SignatureChecker() {
        //private ctor, use static
    }

    public static boolean verifyPKCS7(byte[] data, byte[] signature, byte[] rootCert) {
        try {
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            CMSProcessable signedContent = new CMSProcessableByteArray(data);
            CMSSignedData cms = new CMSSignedData(signedContent, signature);

            Store store = cms.getCertificates();
            SignerInformationStore signers = cms.getSignerInfos();
            Iterator signersIt = signers.getSigners().iterator();
            while (signersIt.hasNext()) {
                SignerInformation signer = (SignerInformation) signersIt.next();
                Collection certCollection = store.getMatches(signer.getSID());
                Iterator certIt = certCollection.iterator();
                X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
                X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {

                    X509Certificate root = loadCertificate(rootCert);

                    List<X509Certificate> intermediates = new ArrayList<X509CertificateHolder>(store.getMatches(null)).stream().map(holder -> {
                        try {
                            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
                        } catch (CertificateException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());

                    verifyCertificate(cert, intermediates, root);

                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error during signature check", e);
        }
        return false;
    }

    private static X509CertificateObject loadCertificate(byte[] certificate) throws IOException {
        PEMReader pemReader = new PEMReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(certificate))));
        X509CertificateObject cert = (X509CertificateObject) pemReader.readObject();
        return cert;
    }


    private static PKIXCertPathBuilderResult verifyCertificate(X509Certificate cert, List<X509Certificate> intermediateCerts, X509Certificate rootCA) throws GeneralSecurityException {

        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(cert);

        Set<TrustAnchor> trustAnchors = new HashSet<>();
        trustAnchors.add(new TrustAnchor(rootCA, null));

        PKIXBuilderParameters params = new PKIXBuilderParameters(trustAnchors, selector);
        params.setRevocationEnabled(false);

        CertStore intermediates = CertStore.getInstance("Collection", new CollectionCertStoreParameters(intermediateCerts), "BC");
        params.addCertStore(intermediates);

        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");
        PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder.build(params);
        return result;
    }
}
