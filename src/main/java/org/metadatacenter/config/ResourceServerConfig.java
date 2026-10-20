package org.metadatacenter.config;

public class ResourceServerConfig extends ServerConfig {

  private String regenerateSearchIndex;
  private String generateEmptySearchIndex;
  private String loadValueSetsOntology;
  private String loadValueSetsOntologyStatus;

  public String getRegenerateSearchIndex() {
    return regenerateSearchIndex;
  }

  public String getGenerateEmptySearchIndex() {
    return generateEmptySearchIndex;
  }

  public String getLoadValueSetsOntology() {
    return loadValueSetsOntology;
  }

  public String getLoadValueSetsOntologyStatus() {
    return loadValueSetsOntologyStatus;
  }
}
