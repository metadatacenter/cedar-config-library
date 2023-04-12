package org.metadatacenter.config;

import java.util.Map;

public class HibernateConfig {

  private String url;

  private String user;

  private String password;

  private String driverClass;

  private Map<String, String> properties;

  public String getUrl() {
    return url;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getDriverClass() {
    return driverClass;
  }

  public Map<String, String> getProperties() {
    return properties;
  }
}