package org.metadatacenter.config;

/** Internal artifact credential, separate from the end-user Authorization header. */
public class ArtifactServiceConfig {
  public static final String HEADER = "X-CEDAR-Artifact-Service-Key";
  private String apiKey;
  private String previousApiKey;

  public String getApiKey() { return apiKey; }
  public String getPreviousApiKey() { return previousApiKey; }

  public String requireApiKey() {
    if (!isValidKey(apiKey)) {
      throw new IllegalStateException("A 256-bit artifact service API key is required; run cedarcli env artifact-key init");
    }
    return apiKey;
  }

  public static boolean isValidKey(String key) {
    return key != null && key.matches("[A-Za-z0-9_-]{43}");
  }
}
