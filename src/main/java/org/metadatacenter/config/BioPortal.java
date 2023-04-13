package org.metadatacenter.config;

public class BioPortal extends ServerConfig {

  private String apiKey;
  private String basePath;
  private int connectTimeout;
  private int socketTimeout;
  private int defaultPageSize;

  public String getApiKey() {
    return apiKey;
  }

  public String getBasePath() {
    return basePath;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public int getSocketTimeout() {
    return socketTimeout;
  }

  public int getDefaultPageSize() {
    return defaultPageSize;
  }
}
