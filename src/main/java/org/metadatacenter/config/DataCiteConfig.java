package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataCiteConfig {
  private String repositoryId;

  private String password;

  private String prefix;

  private String endpointUrl;

  private String templateId;

  @JsonProperty("enabled")
  private boolean enabled;

  public String getRepositoryId() {
    return repositoryId;
  }

  public String getPassword() {
    return password;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getEndpointUrl() {
    return endpointUrl;
  }

  public String getTemplateId() {
    return templateId;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
