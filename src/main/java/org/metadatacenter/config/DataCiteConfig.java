package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataCiteConfig {
  private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
  private static final int DEFAULT_REQUEST_TIMEOUT = 20000;

  private String repositoryId;

  private String password;

  private String prefix;

  private String endpointUrl;

  private String templateId;

  private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

  private int requestTimeout = DEFAULT_REQUEST_TIMEOUT;

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

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public int getRequestTimeout() {
    return requestTimeout;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
