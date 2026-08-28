package org.metadatacenter.config.environment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.model.SystemComponent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CedarEnvironmentVariableProviderTest {

  @AfterEach
  void clearEnvironmentOverride() {
    CedarEnvironmentSource.clearOverride();
  }

  @Test
  void neededBooleanDefaultsToFalseWhenEnvironmentVariableIsAbsent() {
    CedarEnvironmentSource.setOverride(Map.of());

    Map<String, String> environment = CedarEnvironmentVariableProvider.getFor(SystemComponent.SERVER_RESOURCE);

    assertEquals("false", environment.get(CedarEnvironmentVariable.CEDAR_KEYCLOAK_ALLOW_INSECURE_TLS.getName()));
  }

  @Test
  void explicitInsecureTlsOptInIsPreserved() {
    CedarEnvironmentSource.setOverride(
        Map.of(CedarEnvironmentVariable.CEDAR_KEYCLOAK_ALLOW_INSECURE_TLS.getName(), "true"));

    Map<String, String> environment = CedarEnvironmentVariableProvider.getFor(SystemComponent.SERVER_RESOURCE);

    assertEquals("true", environment.get(CedarEnvironmentVariable.CEDAR_KEYCLOAK_ALLOW_INSECURE_TLS.getName()));
  }
}
