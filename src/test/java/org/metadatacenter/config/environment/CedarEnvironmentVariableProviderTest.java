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

  @Test
  void artifactServerReceivesTheAddressesOfTheServicesItVerifiesValuesWith() {
    var needed = CedarConfigEnvironmentDescriptor.getVariableNamesFor(SystemComponent.SERVER_ARTIFACT);
    for (var variable : java.util.List.of(CedarEnvironmentVariable.CEDAR_TERMINOLOGY_SERVER_HOST,
        CedarEnvironmentVariable.CEDAR_TERMINOLOGY_HTTP_PORT, CedarEnvironmentVariable.CEDAR_BRIDGE_SERVER_HOST,
        CedarEnvironmentVariable.CEDAR_BRIDGE_HTTP_PORT)) {
      assertTrue(needed.contains(variable), variable.getName());
    }
    CedarEnvironmentSource.setOverride(Map.of("CEDAR_TERMINOLOGY_SERVER_HOST", "terminology.example",
        "CEDAR_TERMINOLOGY_HTTP_PORT", "9004", "CEDAR_BRIDGE_SERVER_HOST", "bridge.example",
        "CEDAR_BRIDGE_HTTP_PORT", "9015"));
    var environment = CedarEnvironmentVariableProvider.getFor(SystemComponent.SERVER_ARTIFACT);
    // An undeclared port takes the placeholder 0 and an undeclared host is held back, so these four
    // assertions are what keeps the artifact server's two outbound addresses requestable.
    assertEquals("terminology.example", environment.get("CEDAR_TERMINOLOGY_SERVER_HOST"));
    assertEquals("9004", environment.get("CEDAR_TERMINOLOGY_HTTP_PORT"));
    assertEquals("bridge.example", environment.get("CEDAR_BRIDGE_SERVER_HOST"));
    assertEquals("9015", environment.get("CEDAR_BRIDGE_HTTP_PORT"));
  }

  @Test
  void onlyArtifactAndItsTrustedCallersReceiveTheServiceCredential() {
    for (var component : java.util.List.of(SystemComponent.SERVER_ARTIFACT, SystemComponent.SERVER_RESOURCE,
        SystemComponent.SERVER_WORKER, SystemComponent.SERVER_BRIDGE, SystemComponent.SERVER_REPO,
        SystemComponent.SERVER_OPENVIEW, SystemComponent.SERVER_MONITOR)) {
      var needed = CedarConfigEnvironmentDescriptor.getVariableNamesFor(component);
      boolean trusted = component == SystemComponent.SERVER_ARTIFACT || component == SystemComponent.SERVER_RESOURCE
          || component == SystemComponent.SERVER_WORKER;
      assertEquals(trusted, needed.contains(CedarEnvironmentVariable.CEDAR_ARTIFACT_SERVICE_API_KEY), component.name());
      assertEquals(component == SystemComponent.SERVER_ARTIFACT,
          needed.contains(CedarEnvironmentVariable.CEDAR_ARTIFACT_SERVICE_PREVIOUS_API_KEY), component.name());
    }
    assertTrue(CedarEnvironmentVariable.CEDAR_ARTIFACT_SERVICE_API_KEY.isSecure());
    assertTrue(CedarEnvironmentVariable.CEDAR_ARTIFACT_SERVICE_PREVIOUS_API_KEY.isSecure());
  }
}
