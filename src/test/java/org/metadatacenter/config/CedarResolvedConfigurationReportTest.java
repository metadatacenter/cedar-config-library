package org.metadatacenter.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The resolved configuration is served by every service's server report, so each way a secret can
 * reach it is masked: under a key named as a secret, under a key named as anything else, and inside
 * an array.
 */
class CedarResolvedConfigurationReportTest {

  private static final String MONGO_PASSWORD = "mongo-password-1234";

  private static Map<String, String> environment() {
    Map<String, String> environment = new HashMap<>();
    environment.put("CEDAR_HOST", "metadatacenter.orgx");
    environment.put("CEDAR_ADMIN_USER_PASSWORD", "admin-password-5678");
    environment.put("CEDAR_MONGO_HOST", "127.0.0.1");
    environment.put("CEDAR_MONGO_APP_USER_PASSWORD", MONGO_PASSWORD);
    return environment;
  }

  @Test
  void aSecretUnderASecretKeyIsMasked() throws Exception {
    JsonNode report = CedarResolvedConfigurationReport.readMasked(environment());

    assertEquals("ad***************78", report.at("/adminUser/password").asText());
    assertEquals("mo***************34", report.at("/artifactServer/mongoConnection/password").asText());
  }

  @Test
  void aSecretUnderAnOrdinaryKeyIsMasked() throws Exception {
    // A password set as a host by mistake lands under a key nobody named as a secret.
    Map<String, String> environment = environment();
    environment.put("CEDAR_MONGO_HOST", MONGO_PASSWORD);

    JsonNode report = CedarResolvedConfigurationReport.readMasked(environment);

    assertEquals("mo***************34", report.at("/artifactServer/mongoConnection/host").asText());
    assertFalse(report.toString().contains(MONGO_PASSWORD), report.toString());
  }

  @Test
  void aSecretInsideAnArrayOrALongerValueIsMasked() throws Exception {
    JsonNode tree = new ObjectMapper().readTree(
        "{\"hosts\": [\"db.example\", \"mongodb://cedar:" + MONGO_PASSWORD + "@db.example\"]}");

    JsonNode masked = CedarResolvedConfigurationReport.mask(tree, List.of(MONGO_PASSWORD));

    assertEquals("db.example", masked.at("/hosts/0").asText());
    assertEquals("mongodb://cedar:mo***************34@db.example", masked.at("/hosts/1").asText());
  }

  @Test
  void aSecretShorterThanTheScanThresholdIsMaskedOnlyUnderItsKey() throws Exception {
    // A four-character secret occurs inside unrelated text by coincidence, here the host name, so the
    // value scan leaves it alone and only the key pass masks it.
    Map<String, String> environment = environment();
    environment.put("CEDAR_MONGO_APP_USER_PASSWORD", "orgx");

    JsonNode report = CedarResolvedConfigurationReport.readMasked(environment);

    assertEquals("********", report.at("/artifactServer/mongoConnection/password").asText());
    assertEquals("https://auth.metadatacenter.orgx", report.at("/keycloak/authServerUrl").asText());
  }

  @Test
  void anUnresolvedPlaceholderUnderASecretKeyKeepsItsName() throws Exception {
    Map<String, String> environment = environment();
    environment.remove("CEDAR_MONGO_APP_USER_PASSWORD");

    JsonNode report = CedarResolvedConfigurationReport.readMasked(environment);

    assertEquals("${CEDAR_MONGO_APP_USER_PASSWORD}",
        report.at("/artifactServer/mongoConnection/password").asText());
  }
}
