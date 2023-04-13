package org.metadatacenter.config;

public class KeycloakConfig {

  private String clientId;

  private String realm;

  private String authServerUrl;

  private String sslRequired;

  private String resource;

  private boolean publicClient;

  public String getClientId() {
    return clientId;
  }

  public String getRealm() {
    return realm;
  }

  public String getAuthServerUrl() {
    return authServerUrl;
  }

  public String getSslRequired() {
    return sslRequired;
  }

  public String getResource() {
    return resource;
  }

  public boolean isPublicClient() {
    return publicClient;
  }
}
