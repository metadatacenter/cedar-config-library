package org.metadatacenter.config;

import java.util.List;

public class ArtifactRESTAPI {

  private PaginationConfig pagination;

  private List<String> excludedFields;

  private ArtifactRESTAPISummaries summaries;

  public PaginationConfig getPagination() {
    return pagination;
  }

  public List<String> getExcludedFields() {
    return excludedFields;
  }

  public ArtifactRESTAPISummaries getSummaries() {
    return summaries;
  }
}
