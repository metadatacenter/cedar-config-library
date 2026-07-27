package org.metadatacenter.config;

/**
 * Configuration for the terminology server's local, version-aware SQLite store.
 *
 * When {@code catalogPath} is set and {@code localOntologies} names at least one ontology, the
 * terminology server serves those ontologies' hierarchy operations from local snapshots (falling
 * back to BioPortal for everything else). Left empty (the default), the local store is disabled and
 * all requests go to BioPortal.
 */
public class LocalStoreConfig {

  private String catalogPath;
  /** Comma-separated ontology acronyms to serve locally (e.g. {@code "DOID,NCIT"}). */
  private String localOntologies;

  public String getCatalogPath() {
    return catalogPath;
  }

  public String getLocalOntologies() {
    return localOntologies;
  }
}
