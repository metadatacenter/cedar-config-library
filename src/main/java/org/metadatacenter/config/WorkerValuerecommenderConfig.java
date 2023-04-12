package org.metadatacenter.config;

public class WorkerValuerecommenderConfig extends ServerConfig {

  private int maxReindexingThreadCount;
  private int sleepMillisAfterTooManyProcessing;
  private int sleepMillisAfterCurrentIdProcessing;

  public int getMaxReindexingThreadCount() {
    return maxReindexingThreadCount;
  }

  public int getSleepMillisAfterTooManyProcessing() {
    return sleepMillisAfterTooManyProcessing;
  }

  public int getSleepMillisAfterCurrentIdProcessing() {
    return sleepMillisAfterCurrentIdProcessing;
  }
}
