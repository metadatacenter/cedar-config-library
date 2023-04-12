package org.metadatacenter.config;

import org.metadatacenter.model.CedarResourceType;

import java.util.Map;

public class MongoConfig {

  private MongoConnection mongoConnection;

  private Map<String, String> collections;

  public MongoConnection getMongoConnection() {
    return mongoConnection;
  }

  public String getDatabaseName() {
    return mongoConnection.getDatabaseName();
  }

  public Map<String, String> getCollections() {
    return collections;
  }

  public String getMongoCollectionName(CedarResourceType nt) {
    return collections.get(nt.getValue());
  }

}
