package org.metadatacenter.config;

import java.util.Map;

public class CacheServerPersistent extends AbstractCacheServer {

  private Map<String, String> queueNames;

  public Map<String, String> getQueueNames() {
    return queueNames;
  }

  public String getQueueName(String id) {
    return queueNames.get(id);
  }
}
