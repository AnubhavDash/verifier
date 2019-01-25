package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.dto.Metadata;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.Store;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.Signature;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Collectors;

public class SignatureChecker {

    private static final Logger LOGGER = Logger.getLogger(SignatureChecker.class);

    private static final String CRLF = "\r\n";
    private static final String LF = "\n";

    private SignatureChecker() {
        //private ctor, use static
    }

    public static boolean verifyPKCS7(byte[] sourceData, byte[] signatureData, byte[] rootCert) {
        boolean result = processPKCS7(sourceData, signatureData, rootCert);
        if (!result) {
            String s = TypeConverter.byteToString(sourceData);
            if (s.contains(CRLF)) {
                result = processPKCS7(TypeConverter.stringToByte(s.replaceAll(CRLF, LF)), signatureData, rootCert);
            } else if (s.contains(LF)) {
                result = processPKCS7(TypeConverter.stringToByte(s.replaceAll(LF, CRLF)), signatureData, rootCert);
            }
        }
        return result;
    }

    public static boolean verifyMetdata(byte[] sourceData, byte[] metadataData, byte[] signerCert, byte[] rootCert) {
        boolean result = processMetdata(sourceData, metadataData, signerCert, rootCert);
        if (!result) {
            String s = TypeConverter.byteToString(sourceData);
            if (s.contains(CRLF)) {
                result = processMetdata(TypeConverter.stringToByte(s.replaceAll(CRLF, LF)), metadataData, signerCert, rootCert);
            } else if (s.contains(LF)) {
                result = processMetdata(TypeConverter.stringToByte(s.replaceAll(LF, CRLF)), metadataData, signerCert, rootCert);
            }
        }
        return result;
    }

    private static boolean processPKCS7(byte[] sourceData, byte[] signatureData, byte[] rootCert) {
        try {
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            CMSProcessable signedContent = new CMSProcessableByteArray(sourceData);
            CMSSignedData cms = new CMSSignedData(signedContent, signatureData);

            Store store = cms.getCertificates();
            SignerInformationStore signers = cms.getSignerInfos();
            Iterator signersIt = signers.getSigners().iterator();
            while (signersIt.hasNext()) {
                SignerInformation signer = (SignerInformation) signersIt.next();
                Iterator certIt = store.getMatches(signer.getSID()).iterator();
                X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
                X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                    //signature is valid, checking certificate chain validity
                    X509Certificate root = loadCertificate(rootCert);
                    List<X509Certificate> intermediates = new ArrayList<X509CertificateHolder>(store.getMatches(null)).stream().map(holder -> {
                        try {
                            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
                        } catch (CertificateException e) {
                            throw new RuntimeException("Unable to convert the certificate", e);
                        }
                    }).collect(Collectors.toList());

                    verifyCertificateChain(cert, intermediates, root);

                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.info("Error during signature check", e);
        }
        return false;
    }

    private static boolean processMetdata(byte[] sourceData, byte[] metadataData, byte[] signerCert, byte[] rootCert) {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        try {
            Metadata metadata = Deserializer.fromJson(metadataData, Metadata.class);
            final X509Certificate sCert = loadCertificate(signerCert);

            if (!metadata.getVersion().equals("1.0")) {
                throw new UnsupportedOperationException("metadata version not supported : " + metadata.getVersion());
            }

            final String algoName = StringUtils.isNotEmpty(metadata.getAlg()) ? metadata.getAlg() : "SHA256withRSAandMGF1";

            //signature
            byte[] signature = TypeConverter.base64ToByte(metadata.getSignature());

            //take fields to be added to the content
            StringBuilder sb = new StringBuilder();
            metadata.getSigned().stream().forEach(s -> sb.append(s.getValue()));
            byte[] fields = sb.toString().getBytes(StandardCharsets.UTF_8);

            //concatenate sourceData & fields
            byte[] source = new byte[sourceData.length + fields.length];
            System.arraycopy(sourceData, 0, source, 0, sourceData.length);
            System.arraycopy(fields, 0, source, sourceData.length, fields.length);

            Signature signatureAlgorithm = Signature.getInstance(algoName);
            signatureAlgorithm.initVerify(sCert.getPublicKey());
            signatureAlgorithm.update(source);

            if (signatureAlgorithm.verify(signature)) {
                //signature is valid, checking certificate chain validity
                verifyCertificateChain(sCert, Collections.singletonList(sCert), loadCertificate(rootCert));
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("signature check failed", e);
        }
        return false;
    }

    private static X509Certificate loadCertificate(byte[] certificate) throws IOException, CertificateException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        PEMParser parser = new PEMParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(certificate))));
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) parser.readObject());
    }


    private static PKIXCertPathBuilderResult verifyCertificateChain(X509Certificate cert, List<X509Certificate> intermediateCerts, X509Certificate rootCA) throws GeneralSecurityException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        Set<TrustAnchor> trustAnchors = new HashSet<>();
        trustAnchors.add(new TrustAnchor(rootCA, null));

        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(cert);

        PKIXBuilderParameters params = new PKIXBuilderParameters(trustAnchors, selector);

        //disable CLR check because we are not online
        params.setRevocationEnabled(false);

        params.addCertStore(CertStore.getInstance("Collection",
                new CollectionCertStoreParameters(intermediateCerts), "BC"));

        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");

        PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder.build(params);
        return result;
    }

    public static boolean verifySignature(byte[] source, byte[] signature, byte[] certificate) {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        try {
            final X509Certificate sCert = loadCertificate(certificate);
            final String algoName = "SHA256withRSAandMGF1";

            Signature signatureAlgorithm = Signature.getInstance(algoName);
            signatureAlgorithm.initVerify(sCert.getPublicKey());
            signatureAlgorithm.update(source);

            if (signatureAlgorithm.verify(signature)) {
                //TODO check if we have to check the chain or not
                //signature is valid, checking certificate chain validity
                //verifyCertificateChain(sCert, Collections.singletonList(sCert), loadCertificate(rootCert));
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("signature check failed", e);
        }
        return false;
    }
}
