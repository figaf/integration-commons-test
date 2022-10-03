package com.figaf.integration.common.data_provider;

import com.figaf.integration.common.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    private final ConnectionProperties connectionProperties;

    private String loginPageUrl;
    private String ssoUrl;

    private String clientId;
    private String clientSecret;
    private String tokenUrl;

    private String publicUrl;
    private AuthenticationType authenticationType;

    public RequestContext createRequestContext() {
        return createRequestContext("");
    }

    public RequestContext createRequestContext(String restTemplateWrapperKey) {
        RequestContext requestContext;
        switch (platform) {
            case API_MANAGEMENT: {
                requestContext = cloudPlatformType.equals(CloudPlatformType.NEO)
                        ? RequestContext.apiMgmtNeo(connectionProperties)
                        : RequestContext.apiMgmtCloudFoundry(connectionProperties, restTemplateWrapperKey);
                break;
            }
            case CPI: {
                requestContext = cloudPlatformType.equals(CloudPlatformType.NEO)
                        ? RequestContext.cpiNeo(connectionProperties)
                        : RequestContext.cpiCloudFoundry(connectionProperties, restTemplateWrapperKey);
                break;
            }
            case PRO: {
                requestContext = RequestContext.pro(connectionProperties);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }

        requestContext.setClientId(clientId);
        requestContext.setClientSecret(clientSecret);
        requestContext.setOauthUrl(tokenUrl);
        requestContext.setAuthenticationType(authenticationType);
        requestContext.setSsoUrl(ssoUrl);
        requestContext.setLoginPageUrl(loginPageUrl);

        return requestContext;

    }
}
