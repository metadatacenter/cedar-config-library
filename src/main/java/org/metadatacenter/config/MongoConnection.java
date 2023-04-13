package org.metadatacenter.config;

public class MongoConnection {

  private String host;

  private int port;

  private String user;

  private String password;
  
  private String databaseName;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getDatabaseName() {
    return databaseName;
  }
}
