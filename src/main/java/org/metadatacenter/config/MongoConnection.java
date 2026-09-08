package org.metadatacenter.config;

public class MongoConnection {

  private String host;

  private int port;

  private String user;

  private String password;

  private String databaseName;

  private int serverSelectionTimeoutMillis;

  private int connectTimeoutMillis;

  private int readTimeoutMillis;

  private int poolWaitTimeoutMillis;

  private int maxPoolSize;

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

  public int getServerSelectionTimeoutMillis() {
    return serverSelectionTimeoutMillis;
  }

  public int getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  public int getReadTimeoutMillis() {
    return readTimeoutMillis;
  }

  public int getPoolWaitTimeoutMillis() {
    return poolWaitTimeoutMillis;
  }

  public int getMaxPoolSize() {
    return maxPoolSize;
  }
}
