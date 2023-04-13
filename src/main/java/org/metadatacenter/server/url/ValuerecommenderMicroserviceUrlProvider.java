package org.metadatacenter.server.url;

import org.metadatacenter.config.ServerConfig;
import org.metadatacenter.id.CedarTemplateId;
import org.metadatacenter.util.http.UrlUtil;

public class ValuerecommenderMicroserviceUrlProvider extends MicroserviceUrlProvider {

  public ValuerecommenderMicroserviceUrlProvider(ServerConfig server) {
    super(server.getBase());
  }

  public String getCommandGenerateRules(CedarTemplateId templateId) {
    return base + "command/generate-rules/" + UrlUtil.urlEncode(templateId.getId());
  }

  public String getCommandGenerateRulesStatus() {
    return base + "command/generate-rules/status";
  }
}
