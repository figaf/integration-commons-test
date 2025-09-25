package com.figaf.integration.common.data_provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.figaf.integration.common.entity.*;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.platform.commons.util.StringUtils;

import java.nio.file.Path;

/**
 * @author Ilya Nesterov
 */
public abstract class AbstractAgentTestDataProvider implements ArgumentsProvider {

    protected static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    protected static AgentTestData buildAgentTestData(Path pathToTestData) {
        final String agentTestDataTitle = pathToTestData.getName(pathToTestData.getNameCount() - 1).toString();

        final Platform platform;
        final CloudPlatformType cloudPlatformType;
        AuthenticationType authenticationType = null;
        if (agentTestDataTitle.startsWith("cpi-neo")) {
            platform = Platform.CPI;
            cloudPlatformType = CloudPlatformType.NEO;
        } else if (agentTestDataTitle.startsWith("cpi-cf")) {
            platform = Platform.CPI;
            cloudPlatformType = CloudPlatformType.CLOUD_FOUNDRY;
            authenticationType = agentTestDataTitle.startsWith("cpi-cf-oauth") ? AuthenticationType.OAUTH : AuthenticationType.BASIC;
        } else if (agentTestDataTitle.startsWith("apimgmt-neo")) {
            platform = Platform.API_MANAGEMENT;
            cloudPlatformType = CloudPlatformType.NEO;
        } else if (agentTestDataTitle.startsWith("apimgmt-cf")) {
            platform = Platform.API_MANAGEMENT;
            cloudPlatformType = CloudPlatformType.CLOUD_FOUNDRY;
            authenticationType = agentTestDataTitle.startsWith("apimgmt-cf-oauth") ? AuthenticationType.OAUTH : AuthenticationType.BASIC;
        } else if (agentTestDataTitle.startsWith("pro")) {
            platform = Platform.PRO;
            cloudPlatformType = null;
        } else {
            throw new IllegalArgumentException("Test Data folder name must start with one of the following prefixes: " +
                    "['cpi-neo', 'cpi-cf', 'apimgmt-neo', 'apimgmt-cf', 'pro'] to define the type of the platform");
        }

        final String authenticationTypePropertyName = getAuthenticationTypePropertyName(agentTestDataTitle);
        final String hostPropertyName = getHostPropertyName(agentTestDataTitle);
        final String usernamePropertyName = getUsernamePropertyName(agentTestDataTitle);
        final String passwordPropertyName = getPasswordPropertyName(agentTestDataTitle);

        final String clientIdPropertyName = getClientIdPropertyName(agentTestDataTitle);
        final String clientSecretPropertyName = getClientSecretPropertyName(agentTestDataTitle);
        final String tokenUrlPropertyName = getTokenUrlPropertyName(agentTestDataTitle);
        final String publicUrlPropertyName = getPublicUrlPropertyName(agentTestDataTitle);

        String authenticationTypePropertyValue = System.getProperty(authenticationTypePropertyName, null);
        //overwrite authenticationType if it's provided explicitly
        if (authenticationTypePropertyValue != null) {
            authenticationType = AuthenticationType.valueOf(authenticationTypePropertyValue);
        }
        final String host = System.getProperty(hostPropertyName);
        final String username = System.getProperty(usernamePropertyName);
        final String password = System.getProperty(passwordPropertyName);

        final String loginUrl = System.getProperty(getLoginUrlPropertyName(agentTestDataTitle));
        final String ssoUrl = System.getProperty(getSsoUrlPropertyName(agentTestDataTitle));

        final WebApiAccessMode webApiAccessMode = WebApiAccessMode.valueOf(System.getProperty(getWebApiAccessModePropertyName(agentTestDataTitle), "S_USER"));
        final String samlUrl = System.getProperty(getSamlUrlPropertyName(agentTestDataTitle));
        final String idpName = System.getProperty(getIdpNamePropertyName(agentTestDataTitle));
        final String idpApiClientId = System.getProperty(getIdpApiClientIdPropertyName(agentTestDataTitle));
        final String idpApiClientSecret = System.getProperty(getIdpApiClientSecretPropertyName(agentTestDataTitle));

        final String clientId = System.getProperty(clientIdPropertyName);
        final String clientSecret = System.getProperty(clientSecretPropertyName);
        final String tokenUrl = System.getProperty(tokenUrlPropertyName);
        final String publicUrl = System.getProperty(publicUrlPropertyName);

        final String certificatePath = System.getProperty(getCertificatePathPropertyName(agentTestDataTitle));
        final String certificatePassword = System.getProperty(getCertificatePasswordPropertyName(agentTestDataTitle));
        final boolean isIntegrationSuite = Boolean.parseBoolean(System.getProperty(getIntegrationSuitePropertyName(agentTestDataTitle)));
        if (AuthenticationType.BASIC.equals(authenticationType)) {
            if (StringUtils.isBlank(host))
                throw new IllegalArgumentException(String.format("Property %s is not defined", hostPropertyName));
            if (StringUtils.isBlank(username))
                throw new IllegalArgumentException(String.format("Property %s is not defined", usernamePropertyName));
            if (StringUtils.isBlank(password))
                throw new IllegalArgumentException(String.format("Property %s is not defined", passwordPropertyName));
        }

        if (AuthenticationType.OAUTH.equals(authenticationType)) {
            if (StringUtils.isBlank(clientId))
                throw new IllegalArgumentException(String.format("Property %s is not defined", clientIdPropertyName));
            if (StringUtils.isBlank(clientSecret))
                throw new IllegalArgumentException(String.format("Property %s is not defined", clientSecretPropertyName));
            if (StringUtils.isBlank(tokenUrl))
                throw new IllegalArgumentException(String.format("Property %s is not defined", tokenUrlPropertyName));
            if (Platform.API_MANAGEMENT.equals(platform)) {
                if (StringUtils.isBlank(publicUrl)) {
                    throw new IllegalArgumentException(String.format("Property %s is not defined", publicUrlPropertyName));
                }
            }
        }

        return new AgentTestData(
                agentTestDataTitle,
                platform,
                cloudPlatformType,
                loginUrl,
                ssoUrl,
                webApiAccessMode,
                samlUrl,
                idpName,
                idpApiClientId,
                idpApiClientSecret,
                clientId,
                clientSecret,
                tokenUrl,
                authenticationType,
                certificatePath,
                certificatePassword,
                publicUrl,
                host,
                443,
                "https",
                username,
                password,
                isIntegrationSuite
        );
    }

    private static String getAuthenticationTypePropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.authenticationType", agentTestDataTitle);
    }

    private static String getHostPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.host", agentTestDataTitle);
    }

    private static String getUsernamePropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.username", agentTestDataTitle);
    }

    private static String getPasswordPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.password", agentTestDataTitle);
    }

    private static String getClientIdPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.clientId", agentTestDataTitle);
    }

    private static String getClientSecretPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.clientSecret", agentTestDataTitle);
    }

    private static String getTokenUrlPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.tokenUrl", agentTestDataTitle);
    }

    private static String getPublicUrlPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.publicUrl", agentTestDataTitle);
    }

    private static String getLoginUrlPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.loginUrl", agentTestDataTitle);
    }

    private static String getSsoUrlPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.ssoUrl", agentTestDataTitle);
    }

    private static String getWebApiAccessModePropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.webApiAccessMode", agentTestDataTitle);
    }

    private static String getSamlUrlPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.samlUrl", agentTestDataTitle);
    }

    private static String getIdpNamePropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.idpName", agentTestDataTitle);
    }

    private static String getIdpApiClientIdPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.idpApiClientId", agentTestDataTitle);
    }

    private static String getIdpApiClientSecretPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.idpApiClientSecret", agentTestDataTitle);
    }

    private static String getCertificatePathPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.certificatePath", agentTestDataTitle);
    }

    private static String getCertificatePasswordPropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.certificatePassword", agentTestDataTitle);
    }

    private static String getIntegrationSuitePropertyName(String agentTestDataTitle) {
        return String.format("agent-test-data.%s.integrationSuite", agentTestDataTitle);
    }
}
