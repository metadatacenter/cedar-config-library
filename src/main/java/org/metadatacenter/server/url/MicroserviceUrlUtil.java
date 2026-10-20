package org.metadatacenter.server.url;

import org.metadatacenter.config.ServersConfig;

public class MicroserviceUrlUtil {

  private final UserMicroserviceUrlProvider user;
  private final ArtifactMicroserviceUrlProvider artifact;
  private final ResourceMicroserviceUrlProvider resource;
  private final MessagingMicroserviceUrlProvider messaging;

  public MicroserviceUrlUtil(ServersConfig servers) {
    this(servers, null);
  }

  public MicroserviceUrlUtil(ServersConfig servers, org.metadatacenter.server.jsonld.LinkedDataUtil identifiers) {
    user = new UserMicroserviceUrlProvider(servers.getUser());
    artifact = new ArtifactMicroserviceUrlProvider(servers.getArtifact(), identifiers);
    resource = new ResourceMicroserviceUrlProvider(servers.getResource(), identifiers);
    messaging = new MessagingMicroserviceUrlProvider(servers.getMessaging());
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

  public ResourceMicroserviceUrlProvider getResource() {
    return resource;
  }
}
