package org.metadatacenter.config;

public class OpensearchConfig {

  private OpensearchIndexes indexes;

  private String host;

  private int restPort;

  private int maxConnections;

  private int maxConnectionsPerRoute;

  private int connectionRequestTimeoutMillis;

  private int connectTimeoutMillis;

  private int socketTimeoutMillis;

  private int size;

  private int searchContextKeepAlive;

  private int maxResultWindow;

  public OpensearchIndexes getIndexes() {
    return indexes;
  }

  public String getHost() {
    return host;
  }

  public int getRestPort() {
    return restPort;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public int getMaxConnectionsPerRoute() {
    return maxConnectionsPerRoute;
  }

  public int getConnectionRequestTimeoutMillis() {
    return connectionRequestTimeoutMillis;
  }

  public int getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  public int getSocketTimeoutMillis() {
    return socketTimeoutMillis;
  }

  public int getSize() {
    return size;
  }

  public int getSearchContextKeepAlive() {
    return searchContextKeepAlive;
  }

  public int getMaxResultWindow() {
    return maxResultWindow;
  }
}
