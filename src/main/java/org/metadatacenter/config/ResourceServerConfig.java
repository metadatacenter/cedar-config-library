package org.metadatacenter.config;

public class ResourceServerConfig extends ServerConfig {

  private String regenerateSearchIndex;
  private String generateEmptySearchIndex;

  public String getRegenerateSearchIndex() {
    return regenerateSearchIndex;
  }

  public String getGenerateEmptySearchIndex() {
    return generateEmptySearchIndex;
  }
}
