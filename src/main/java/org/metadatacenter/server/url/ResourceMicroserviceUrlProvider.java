package org.metadatacenter.server.url;

import org.metadatacenter.config.ServerConfig;
import org.metadatacenter.id.CedarArtifactId;
import org.metadatacenter.model.CedarResourceType;
import org.metadatacenter.util.http.UrlUtil;

import java.util.Optional;

import static org.metadatacenter.constant.CedarQueryParameters.QP_FORMAT;
import static org.metadatacenter.constant.CedarQueryParameters.QP_RESOURCE_TYPE;

public class ResourceMicroserviceUrlProvider extends MicroserviceUrlProvider {

  private final org.metadatacenter.server.jsonld.LinkedDataUtil identifiers;

  public ResourceMicroserviceUrlProvider(ServerConfig server) {
    this(server, null);
  }

  public ResourceMicroserviceUrlProvider(ServerConfig server, org.metadatacenter.server.jsonld.LinkedDataUtil identifiers) {
    super(server.getBase(), server.getTimeouts());
    this.identifiers = identifiers;
  }

  public String getResourceType(CedarResourceType resourceType) {
    return base + resourceType.getPrefix();
  }

  public String getArtifactTypeWithId(CedarResourceType resourceType, String id, Optional<String> format) {
    String f = "";
    if (format.isPresent()) {
      f = "?" + QP_FORMAT + "=" + format.get();
    }
    return base + resourceType.getPrefix() + "/" + UrlUtil.urlEncode(identifiers == null ? id : identifiers.resourcePathId(resourceType, id)) + f;
  }

  public String getArtifactTypeWithId(CedarResourceType resourceType, CedarArtifactId id) {
    return getArtifactTypeWithId(resourceType, id.getId(), Optional.empty());
  }

  public String getOpenArtifact(CedarResourceType resourceType, String id) {
    return base + "open/" + resourceType.getPrefix() + "/" + UrlUtil.urlEncode(identifiers == null ? id : identifiers.resourcePathId(resourceType, id));
  }

  public String getCommandDOIUpdate() {
    return base + "/command/annotations/doi";
  }

  public String getValidateCommand(String resourceType) {
    return base + "command/validate?" + QP_RESOURCE_TYPE + "=" + UrlUtil.urlEncode(resourceType);
  }

}
