package org.metadatacenter.server.url;

import org.metadatacenter.config.UserServerConfig;

public class UserMicroserviceUrlProvider extends MicroserviceUrlProvider {

  protected final String usersBase;

  public UserMicroserviceUrlProvider(UserServerConfig server) {
    super(server.getBase());
    usersBase = server.getUsersBase();
  }

  public String UuidSummary(String uuid) {
    return usersBase + uuid + "/" + "summary";
  }
}
