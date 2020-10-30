package com.figaf.integration.common.data_provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.figaf.integration.common.entity.CloudPlatformType;
import com.figaf.integration.common.entity.ConnectionProperties;
import com.figaf.integration.common.entity.Platform;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.platform.commons.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Ilya Nesterov
 */
public abstract class AbstractAgentTestDataProvider implements ArgumentsProvider {

    protected static final ObjectMapper jsonMapper = new ObjectMapper();
    static {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    protected AgentTestData buildAgentTestData(Path pathToTestData) {
        final String agentTestDataTitle = pathToTestData.getName(pathToTestData.getNameCount() - 1).toString();

        final Platform platform;
        final CloudPlatformType cloudPlatformType;
        if (agentTestDataTitle.startsWith("cpi-neo")) {
            platform = Platform.CPI;
            cloudPlatformType = CloudPlatformType.NEO;
        } else if (agentTestDataTitle.startsWith("cpi-cf")) {
            platform = Platform.CPI;
            cloudPlatformType = CloudPlatformType.CLOUD_FOUNDRY;
        } else if (agentTestDataTitle.startsWith("apimgmt-neo")) {
            platform = Platform.API_MANAGEMENT;
            cloudPlatformType = CloudPlatformType.NEO;
        } else if (agentTestDataTitle.startsWith("apimgmt-cf")) {
            platform = Platform.API_MANAGEMENT;
            cloudPlatformType = CloudPlatformType.CLOUD_FOUNDRY;
        } else if (agentTestDataTitle.startsWith("pro")) {
            platform = Platform.PRO;
            cloudPlatformType = null;
        } else {
            throw new IllegalArgumentException("Test Data folder name must start with one of the following prefixes: " +
                    "['cpi-neo', 'cpi-cf', 'apimgmt-neo', 'apimgmt-cf', 'pro'] to define the type of the platform");
        }

        final String hostPropertyName = getHostPropertyName(agentTestDataTitle);
        final String usernamePropertyName = getUsernamePropertyName(agentTestDataTitle);
        final String passwordPropertyName = getPasswordPropertyName(agentTestDataTitle);

        final String host = System.getProperty(hostPropertyName);
        final String username = System.getProperty(usernamePropertyName);
        final String password = System.getProperty(passwordPropertyName);

        if (StringUtils.isBlank(host)) throw new IllegalArgumentException(String.format("Property %s is not defined", hostPropertyName));
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException(String.format("Property %s is not defined", usernamePropertyName));
        if (StringUtils.isBlank(password)) throw new IllegalArgumentException(String.format("Property %s is not defined", passwordPropertyName));

        ConnectionProperties connectionProperties = new ConnectionProperties(
                username,
                password,
                host,
                "443",
                "https"
        );

        return new AgentTestData(agentTestDataTitle, platform, cloudPlatformType, connectionProperties);
    }

    private String getHostPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.host", agentTestDataTitle);
    }

    private String getUsernamePropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.username", agentTestDataTitle);
    }

    private String getPasswordPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.password", agentTestDataTitle);
    }
}
