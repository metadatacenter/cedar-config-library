package org.metadatacenter.server.url;

import org.metadatacenter.config.ServerConfig;
import org.metadatacenter.id.CedarTemplateId;
import org.metadatacenter.util.http.UrlUtil;

public class ValuerecommenderMicroserviceUrlProvider extends MicroserviceUrlProvider {

  private final org.metadatacenter.server.jsonld.LinkedDataUtil identifiers;

  public ValuerecommenderMicroserviceUrlProvider(ServerConfig server) {
    this(server, null);
  }

  public ValuerecommenderMicroserviceUrlProvider(ServerConfig server, org.metadatacenter.server.jsonld.LinkedDataUtil identifiers) {
    super(server.getBase(), server.getTimeouts());
    this.identifiers = identifiers;
  }

  public String getCommandGenerateRules(CedarTemplateId templateId) {
    return base + "command/generate-rules/" + UrlUtil.urlEncode(identifiers == null ? templateId.getId() : identifiers.resourcePathId(org.metadatacenter.model.CedarResourceType.TEMPLATE, templateId.getId()));
  }

  public String getCommandGenerateRulesStatus() {
    return base + "command/generate-rules/status";
  }
}
