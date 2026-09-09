package org.metadatacenter.config;

import java.util.Map;

public class HibernateConfig {

  private String url;

  private String user;

  private String password;

  private String driverClass;

  private Map<String, String> properties;

  private int minSize;

  private int initialSize;

  private int maxSize;

  private long maxWaitForConnectionMillis;

  private String validationQuery;

  private boolean checkConnectionWhileIdle;

  private boolean checkConnectionOnConnect;

  private long validationIntervalMillis;

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

  public int getMinSize() {
    return minSize;
  }

  public int getInitialSize() {
    return initialSize;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public long getMaxWaitForConnectionMillis() {
    return maxWaitForConnectionMillis;
  }

  public String getValidationQuery() {
    return validationQuery;
  }

  public boolean isCheckConnectionWhileIdle() {
    return checkConnectionWhileIdle;
  }

  public boolean isCheckConnectionOnConnect() {
    return checkConnectionOnConnect;
  }

  public long getValidationIntervalMillis() {
    return validationIntervalMillis;
  }
}
