package com.figaf.integration.common.data_provider;

import com.figaf.integration.common.entity.CloudPlatformType;
import com.figaf.integration.common.entity.ConnectionProperties;
import com.figaf.integration.common.entity.Platform;
import com.figaf.integration.common.entity.RequestContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Ilya Nesterov
 */
@RequiredArgsConstructor
@Getter
public class AgentTestData {

    private final String title;
    private final Platform platform;
    private final CloudPlatformType cloudPlatformType;
    private final ConnectionProperties connectionProperties;

    public RequestContext createRequestContext() {
        return createRequestContext(null);
    }

    public RequestContext createRequestContext(String restTemplateWrapperKey) {
        switch (platform) {
            case API_MANAGEMENT: {
                return cloudPlatformType.equals(CloudPlatformType.NEO)
                        ? RequestContext.apiMgmtNeo(connectionProperties)
                        : RequestContext.apiMgmtCloudFoundry(connectionProperties, restTemplateWrapperKey);
            }
            case CPI: {
                return cloudPlatformType.equals(CloudPlatformType.NEO)
                        ? RequestContext.cpiNeo(connectionProperties)
                        : RequestContext.cpiCloudFoundry(connectionProperties, restTemplateWrapperKey);
            }
            case PRO: {
                return RequestContext.pro(connectionProperties);
            }
        }
        throw new IllegalArgumentException("Unsupported platform: " + platform);
    }
}
