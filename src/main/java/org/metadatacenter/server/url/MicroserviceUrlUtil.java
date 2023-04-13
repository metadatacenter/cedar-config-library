package org.metadatacenter.server.url;

import org.metadatacenter.config.ServersConfig;

public class MicroserviceUrlUtil {

  private final UserMicroserviceUrlProvider user;
  private final ArtifactMicroserviceUrlProvider artifact;
  private final MessagingMicroserviceUrlProvider messaging;
  private final ValuerecommenderMicroserviceUrlProvider valuerecommender;

  public MicroserviceUrlUtil(ServersConfig servers) {
    user = new UserMicroserviceUrlProvider(servers.getUser());
    artifact = new ArtifactMicroserviceUrlProvider(servers.getArtifact());
    messaging = new MessagingMicroserviceUrlProvider(servers.getMessaging());
    valuerecommender = new ValuerecommenderMicroserviceUrlProvider(servers.getValuerecommender());
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
}
