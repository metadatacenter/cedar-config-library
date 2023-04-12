package org.metadatacenter.server.url;

import org.metadatacenter.config.ServerConfig;
import org.metadatacenter.id.CedarArtifactId;
import org.metadatacenter.model.CedarResourceType;
import org.metadatacenter.util.http.UrlUtil;

import java.util.Optional;

import static org.metadatacenter.constant.CedarQueryParameters.QP_FORMAT;
import static org.metadatacenter.constant.CedarQueryParameters.QP_RESOURCE_TYPE;

public class ArtifactMicroserviceUrlProvider extends MicroserviceUrlProvider {

  protected static final String VALIDATE_COMMAND = "command/validate";

  public ArtifactMicroserviceUrlProvider(ServerConfig server) {
    super(server.getBase());
  }

  public String getResourceType(CedarResourceType resourceType) {
    return base + resourceType.getPrefix();
  }

  public String getArtifactTypeWithId(CedarResourceType resourceType, String id, Optional<String> format) {
    String f = "";
    if (format.isPresent()) {
      f = "?" + QP_FORMAT + "=" + format.get();
    }
    return base + resourceType.getPrefix() + "/" + UrlUtil.urlEncode(id) + f;
  }

  public String getArtifactTypeWithId(CedarResourceType resourceType, CedarArtifactId id) {
    return getArtifactTypeWithId(resourceType, id.getId(), Optional.empty());
  }

  public String getValidateCommand(String resourceType) {
    return String.format("%s%s?%s=%s", base, VALIDATE_COMMAND, QP_RESOURCE_TYPE, resourceType);
  }
}
