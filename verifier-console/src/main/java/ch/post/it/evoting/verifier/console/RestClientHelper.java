package ch.post.it.evoting.verifier.console;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Base64;

public class RestClientHelper {

    /**
     * Adds a Authorization headers to the given HTTPHeaders
     *
     * @param username
     * @param password
     * @return the original HttpHeaders with the Authotrization added
     */

    /* Headers authentification */
    public static HttpHeaders fillAuthentication(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        StringBuilder sb = new StringBuilder();
        sb.append(username);
        sb.append(":");
        sb.append(password);

        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(sb.toString().getBytes()));

        return headers;
    }

    /* Initialize basic restTemplate  */
    public static RestTemplate initializeBasicRestTemplate(int waitTimeForResponseInMilliSeconds) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        //httpRequestFactory.setConnectionRequestTimeout(10000);
        httpRequestFactory.setConnectTimeout(waitTimeForResponseInMilliSeconds);
        //httpRequestFactory.setReadTimeout(10000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    /* Initialize SSL restTemplate  */
    public static RestTemplate getRestClientSSL(String truststoreLocation, String truststorePassword,
                                                String proxyLocation, Integer proxyPort,
                                                String proxyUsername, String proxyPassword, int waitTimeForResponseInMilliSeconds) throws Exception {

        SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();

        if (StringUtils.hasText(truststoreLocation)) {
            sslContextBuilder.loadTrustMaterial(RestClientHelper.class.getResource(truststoreLocation), truststorePassword.toCharArray());
        } else {
            sslContextBuilder.loadTrustMaterial((x509Certificates, s) -> true);
        }

        HttpClientBuilder clientBuilder = HttpClients.custom()
                .setSslcontext(sslContextBuilder.build());

        if (StringUtils.hasText(proxyLocation)) {
            clientBuilder.setProxy(new HttpHost(proxyLocation, proxyPort == null ? 3128 : proxyPort));
        }

        if (StringUtils.hasText(proxyUsername)) {
            BasicCredentialsProvider credentials = new BasicCredentialsProvider();
            credentials.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));

            clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy())
                    .setDefaultAuthSchemeRegistry(name -> {
                        if ("basic".equalsIgnoreCase(name)) {
                            return new BasicSchemeFactory(Charset.forName("UTF8"));
                        }
                        return null;
                    })
                    .setDefaultCredentialsProvider(credentials);
        }

        HttpComponentsClientHttpRequestFactory clientHttpReq = new HttpComponentsClientHttpRequestFactory();
        clientHttpReq.setHttpClient(clientBuilder.build());
        clientHttpReq.setConnectTimeout(waitTimeForResponseInMilliSeconds);

        RestTemplate template = new RestTemplate(clientHttpReq);
        template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return template;
    }
}
