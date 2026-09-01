package org.metadatacenter.config;

public class Neo4JBoltConfig {

  private String uri;

  private String userName;

  private String userPassword;

  /**
   * How many Bolt connections one server may hold open, or absent for the driver's own default.
   *
   * <p>The driver caps its pool at 100 unless told otherwise, and until this existed nothing could
   * tell it otherwise: the per-server Neo4j footprint was whatever the driver chose, with no way to
   * lower it for a deployment running many services against one database.
   */
  private Integer maxConnectionPoolSize;

  public String getUri() {
    return uri;
  }

  public String getUserName() {
    return userName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public Integer getMaxConnectionPoolSize() {
    return maxConnectionPoolSize;
  }
}
