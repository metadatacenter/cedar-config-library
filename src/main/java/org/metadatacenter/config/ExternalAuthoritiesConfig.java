package org.metadatacenter.config;

public class ExternalAuthoritiesConfig {

  private ExternalAuthorityROR ror;

  private ExternalAuthorityORCID orcid;

  private ExternalAuthorityEPACompTox epaCompTox;

  private ExternalAuthorityRRID rrid;

  public ExternalAuthorityROR getRor() {
    return ror;
  }

  public ExternalAuthorityORCID getOrcid() {
    return orcid;
  }

  public ExternalAuthorityEPACompTox getEpaCompTox() { return epaCompTox; }

  public ExternalAuthorityRRID getRrid() {
    return rrid;
  }
}
