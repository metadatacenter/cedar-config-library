package org.metadatacenter.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.text.StringSubstitutor;
import org.metadatacenter.config.environment.CedarEnvironmentVariable;
import org.metadatacenter.config.environment.CedarSecretMasker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The configuration template as this service resolved it, with every secret masked.
 *
 * <p>The environment report next door says what went in. This says what came out, which is a
 * different question and the one that is usually being asked: a variable can be set correctly and
 * still land in the wrong place, or not be referenced by the template at all.
 *
 * <p>Two things are deliberate. Unresolved placeholders are left as the literal {@code ${NAME}}
 * rather than blanked, because a placeholder that survived substitution is the finding — blanking
 * it would hide it behind an empty string that looks like ordinary missing configuration. That holds
 * under a secret key too: masking there ran over the placeholder itself and returned
 * {@code ${**************************D}}, which protected nothing and destroyed the variable name,
 * so the key pass preserves placeholders and masks only the literal text around them.
 *
 * <p>And the masking is applied twice over: once on keys whose name says they hold a secret, and once
 * on values that match a secret this service holds, wherever they appear. The second pass is what
 * catches a password substituted into a key nobody thought to call a password.
 */
public final class CedarResolvedConfigurationReport {

  /** The template every service resolves at boot, on the classpath of the config library. */
  static final String MAIN_CONFIG_FILE_NAME = "cedar-main.yml";

  /**
   * Below this length a secret is not searched for in the resolved text. A short value —
   * a single-character password, a port reused as a token — occurs inside unrelated strings by
   * coincidence, and redacting every occurrence would corrupt the report it is meant to protect.
   * Such a value is still masked wherever its key names it a secret.
   */
  static final int MIN_SECRET_LENGTH_FOR_VALUE_SCAN = 6;

  private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

  private CedarResolvedConfigurationReport() {
  }

  /**
   * The masked configuration tree for the given environment.
   *
   * @throws IOException if the template cannot be read or parsed
   */
  public static JsonNode readMasked(Map<String, String> environment) throws IOException {
    String template = readTemplate();
    // Relaxed on purpose: a StringSubstitutor with no value for a key leaves the placeholder in
    // place, and seeing which placeholders survived is most of the value of this page.
    String resolved = new StringSubstitutor(environment == null ? Map.of() : environment)
        .setEnableSubstitutionInVariables(false)
        .replace(template);
    JsonNode tree = YAML.readTree(resolved);
    return mask(tree, secretValues(environment));
  }

  private static String readTemplate() throws IOException {
    try (InputStream in = CedarResolvedConfigurationReport.class.getClassLoader()
        .getResourceAsStream(MAIN_CONFIG_FILE_NAME)) {
      if (in == null) {
        throw new IOException("The configuration template " + MAIN_CONFIG_FILE_NAME
            + " is not on the classpath of this service");
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  /** Every secret this service holds that is long enough to be searched for safely. */
  static List<String> secretValues(Map<String, String> environment) {
    List<String> secrets = new ArrayList<>();
    if (environment == null) {
      return secrets;
    }
    for (CedarEnvironmentVariable variable : CedarEnvironmentVariable.values()) {
      if (!variable.isSecure()) {
        continue;
      }
      String value = environment.get(variable.getName());
      if (value != null && value.length() >= MIN_SECRET_LENGTH_FOR_VALUE_SCAN) {
        secrets.add(value);
      }
    }
    return secrets;
  }

  /** Returns a masked copy; the argument tree is not modified. */
  static JsonNode mask(JsonNode node, List<String> secrets) {
    if (node == null || node.isNull()) {
      return node;
    }
    if (node.isObject()) {
      ObjectNode masked = YAML.createObjectNode();
      node.fields().forEachRemaining(field -> {
        JsonNode value = field.getValue();
        if (CedarSecretMasker.isSecretKey(field.getKey()) && value.isValueNode()) {
          masked.put(field.getKey(), CedarSecretMasker.maskPreservingPlaceholders(value.asText()));
        } else {
          masked.set(field.getKey(), mask(value, secrets));
        }
      });
      return masked;
    }
    if (node.isArray()) {
      ArrayNode masked = YAML.createArrayNode();
      node.forEach(element -> masked.add(mask(element, secrets)));
      return masked;
    }
    if (node.isTextual()) {
      return YAML.getNodeFactory().textNode(redactSecrets(node.asText(), secrets));
    }
    return node;
  }

  /** Replaces any secret occurring inside {@code text} with its mask. */
  static String redactSecrets(String text, List<String> secrets) {
    String redacted = text;
    for (String secret : secrets) {
      if (redacted.contains(secret)) {
        redacted = redacted.replace(secret, CedarSecretMasker.mask(secret));
      }
    }
    return redacted;
  }
}
