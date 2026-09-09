package org.metadatacenter.config.environment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.model.SystemComponent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  void bridgeUsesResourceWithoutReceivingArtifactConnectionSettings() {
    var needed = CedarConfigEnvironmentDescriptor.getVariableNamesFor(SystemComponent.SERVER_BRIDGE);
    assertTrue(needed.contains(CedarEnvironmentVariable.CEDAR_RESOURCE_SERVER_HOST));
    assertTrue(needed.contains(CedarEnvironmentVariable.CEDAR_RESOURCE_HTTP_PORT));
    assertFalse(needed.contains(CedarEnvironmentVariable.CEDAR_ARTIFACT_SERVER_HOST));
    assertFalse(needed.contains(CedarEnvironmentVariable.CEDAR_ARTIFACT_HTTP_PORT));
    CedarEnvironmentSource.setOverride(Map.of("CEDAR_ARTIFACT_SERVER_HOST", "must-not-be-used",
        "CEDAR_ARTIFACT_HTTP_PORT", "9001"));
    var environment = CedarEnvironmentVariableProvider.getFor(SystemComponent.SERVER_BRIDGE);
    assertFalse(environment.containsKey("CEDAR_ARTIFACT_SERVER_HOST"));
    assertEquals("0", environment.get("CEDAR_ARTIFACT_HTTP_PORT"));
  }
}
