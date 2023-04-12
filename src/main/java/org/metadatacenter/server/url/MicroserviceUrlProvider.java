package org.metadatacenter.server.url;

public abstract class MicroserviceUrlProvider {

  protected final String base;

  public MicroserviceUrlProvider(String base) {
    this.base = base;
  }
}
