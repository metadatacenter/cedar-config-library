package org.metadatacenter.server.url;

import org.metadatacenter.config.ServersConfig;

public class MicroserviceUrlUtil {

  private final UserMicroserviceUrlProvider user;
  private final ArtifactMicroserviceUrlProvider artifact;
  private final ResourceMicroserviceUrlProvider resource;
  private final MessagingMicroserviceUrlProvider messaging;
  private final ValuerecommenderMicroserviceUrlProvider valuerecommender;

  public MicroserviceUrlUtil(ServersConfig servers) {
    this(servers, null);
  }

  public MicroserviceUrlUtil(ServersConfig servers, org.metadatacenter.server.jsonld.LinkedDataUtil identifiers) {
    user = new UserMicroserviceUrlProvider(servers.getUser());
    artifact = new ArtifactMicroserviceUrlProvider(servers.getArtifact(), identifiers);
    resource = new ResourceMicroserviceUrlProvider(servers.getResource(), identifiers);
    messaging = new MessagingMicroserviceUrlProvider(servers.getMessaging());
    valuerecommender = new ValuerecommenderMicroserviceUrlProvider(servers.getValuerecommender(), identifiers);
  }

  public UserMicroserviceUrlProvider getUser() {
    return user;
  }

  public ArtifactMicroserviceUrlProvider getArtifact() {
    return artifact;
  }

  public MessagingMicroserviceUrlProvider getMessaging() {
    return messaging;
  }

  public ValuerecommenderMicroserviceUrlProvider getValuerecommender() {
    return valuerecommender;
  }

  public ResourceMicroserviceUrlProvider getResource() {
    return resource;
  }
}
