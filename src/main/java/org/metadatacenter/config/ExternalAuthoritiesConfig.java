package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ExternalAuthoritiesConfig {

  /**
   * What the registries change about the external class of call, if anything.
   *
   * <p>One override covers all of them. They differ in how fast they answer, which is what a
   * response timeout should follow, and nothing yet measures that per registry; a single value is
   * the honest shape until something does.
   */
  @JsonProperty @Valid
  private OutboundTimeoutOverride timeouts = new OutboundTimeoutOverride();

  private ExternalAuthorityROR ror;

  private ExternalAuthorityORCID orcid;

  private ExternalAuthorityEPACompTox epaCompTox;

  private ExternalAuthorityRRID rrid;

  private ExternalAuthorityPubMed pubMed;

  public OutboundTimeoutOverride getTimeouts() {
    return timeouts;
  }

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

  public ExternalAuthorityPubMed getPubMed() {
    return pubMed;
  }
}
