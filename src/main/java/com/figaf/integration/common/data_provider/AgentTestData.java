package com.figaf.integration.common.data_provider;

import com.figaf.integration.common.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

/**
 * @author Ilya Nesterov
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class AgentTestData {

    private final String title;
    private final Platform platform;
    private final CloudPlatformType cloudPlatformType;

    private String loginPageUrl;
    private String ssoUrl;
    private WebApiAccessMode webApiAccessMode;
    private String samlUrl;
    private String idpName;
    private String idpApiClientId;
    private String idpApiClientSecret;

    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private AuthenticationType authenticationType;

    private String certificatePath;
    private String certificatePassword;
    private String publicApiUrl;
    private String host;
    private Integer port;
    private String protocol;
    private String username;
    private String password;
    private boolean isIntegrationSuite;

    public RequestContext createRequestContext() {
        return createRequestContext("");
    }

    public RequestContext createRequestContext(String restTemplateWrapperKey) {
        RequestContext requestContext;
        switch (platform) {
            case API_MANAGEMENT: {
                requestContext = cloudPlatformType.equals(CloudPlatformType.NEO)
                        ? RequestContext.apiMgmtNeo()
                        : RequestContext.apiMgmtCloudFoundry(restTemplateWrapperKey);
                break;
            }
            case CPI: {
                requestContext = cloudPlatformType.equals(CloudPlatformType.NEO)
                        ? RequestContext.cpiNeo()
                        : RequestContext.cpiCloudFoundry(restTemplateWrapperKey);
                break;
            }
            case PRO: {
                requestContext = RequestContext.pro();
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }

        requestContext.setClientId(clientId);
        requestContext.setClientSecret(clientSecret);
        requestContext.setRestTemplateWrapperKey(restTemplateWrapperKey);
        requestContext.setOauthUrl(tokenUrl);
        requestContext.setAuthenticationType(authenticationType);
        requestContext.setSsoUrl(ssoUrl);
        requestContext.setWebApiAccessMode(webApiAccessMode);
        requestContext.setIdpApiClientId(idpApiClientId);
        requestContext.setIdpApiClientSecret(idpApiClientSecret);
        requestContext.setSamlUrl(samlUrl);
        requestContext.setIdpName(idpName);
        requestContext.setLoginPageUrl(loginPageUrl);
        requestContext.setPublicApiUrl(publicApiUrl);
        requestContext.setHost(host);
        requestContext.setPort(port);
        requestContext.setProtocol(protocol);
        requestContext.setUsername(username);
        requestContext.setPassword(password);
        requestContext.setIntegrationSuite(isIntegrationSuite);
        if (certificatePath != null) {
            try {
                requestContext.setCertificate(FileUtils.readFileToByteArray(new File(certificatePath)));
            } catch (IOException e) {
                throw new RuntimeException(format("Can't read certificate file from %s", certificatePath));
            }
        }
        requestContext.setCertificatePassword(certificatePassword);

        return requestContext;

    }
}
