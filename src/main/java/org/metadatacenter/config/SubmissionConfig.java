package org.metadatacenter.config;

import java.util.List;

public class SubmissionConfig {
  private NCBIConfig ncbi;

  private ImmPortConfig immPort;

  private List<String> submittableTemplateIds;

  public NCBIConfig getNcbi() {
    return ncbi;
  }

  public ImmPortConfig getImmPort() {
    return immPort;
  }

  public List<String> getSubmittableTemplateIds() {
    return submittableTemplateIds;
  }
}
