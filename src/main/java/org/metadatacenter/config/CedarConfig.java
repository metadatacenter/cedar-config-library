package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.metadatacenter.server.jsonld.LinkedDataUtil;
import org.metadatacenter.server.url.MicroserviceUrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CedarConfig extends Configuration {

  @JsonProperty("home")
  private String home;

  @JsonProperty("host")
  private String host;

  @JsonProperty("adminUser")
  private AdminUserConfig adminUserConfig;

  @JsonProperty("caDSRAdminUser")
  private CaDSRAdminUserConfig caDSRAdminUserConfig;

  @JsonProperty("keycloak")
  private KeycloakConfig keycloakConfig;

  @JsonProperty("artifactServer")
  private MongoConfig artifactServerConfig;

  @JsonProperty("userServer")
  private MongoConfig userServerConfig;

  @JsonProperty("messagingServer")
  private HibernateConfig messagingServerConfig;

  @JsonProperty("dbLogging")
  private HibernateConfig dbLoggingConfig;

  @JsonProperty("neo4j")
  private Neo4JConfig neo4jConfig;

  @JsonProperty("folderStructure")
  private FolderStructureConfig folderStructureConfig;

  @JsonProperty("linkedData")
  private LinkedDataConfig linkedDataConfig;

  @JsonProperty("blueprintUserProfile")
  private BlueprintUserProfile blueprintUserProfile;

  @JsonProperty("elasticsearch")
  private OpensearchConfig opensearchConfig;

  // This is read from a different config file
  private OpensearchSettingsMappingsConfig searchSettingsMappingsConfig;
  private OpensearchSettingsMappingsConfig rulesSettingsMappingsConfig;

  @JsonProperty("servers")
  private ServersConfig servers;

  @JsonProperty("searchSettings")
  private SearchSettings searchSettings;

  @JsonProperty("importExport")
  private ImportExportConfig importExportConfig;

  @JsonProperty("resourceRESTAPI")
  private ResourceRESTAPI resourceRESTAPI;

  @JsonProperty("artifactRESTAPI")
  private ArtifactRESTAPI artifactRESTAPI;

  @JsonProperty("categoryRESTAPI")
  private CategoryRESTAPI categoryRESTAPI;

  @JsonProperty("submission")
  private SubmissionConfig submissionConfig;

  @JsonProperty("trustedFolders")
  private TrustedFoldersConfig trustedFolders;

  @JsonProperty("externalAuthorities")
  private ExternalAuthoritiesConfig externalAuthorities;

  @JsonProperty("testUsers")
  private TestUsers testUsers;

  @JsonProperty("terminology")
  private TerminologyConfig terminologyConfig;

  @JsonProperty("worker")
  private WorkerConfig workerConfig;

  @JsonProperty("cache")
  private CacheConfig cacheConfig;

  @JsonProperty("bridge")
  private BridgeConfig bridgeConfig;

  protected static final Logger log = LoggerFactory.getLogger(CedarConfig.class);

  private static CedarConfig instance;
  private static Map<String, String> instanceEnvironment;

  private LinkedDataUtil linkedDataUtil;
  private MicroserviceUrlUtil microserviceUrlUtil;

  private static CedarConfig buildInstance(Map<String, String> environment) {

    final CedarConfig config;

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    final SubstitutingSourceProvider substitutingSourceProvider = new SubstitutingSourceProvider(
        new ClasspathConfigurationSourceProvider(), new CedarEnvironmentVariableSubstitutor(environment));

    // Read main config
    final String mainConfigFileName = "cedar-main.yml";

    final ConfigurationFactory<CedarConfig> mainConfigurationFactory = new YamlConfigurationFactory<>(
        CedarConfig.class, validator, Jackson.newObjectMapper(), "cedar");

    try {
      config = mainConfigurationFactory.build(substitutingSourceProvider, mainConfigFileName);
    } catch (IOException | ConfigurationException e) {
      throw new CedarConfigurationException(mainConfigFileName, e);
    }

    // Read search config
    final String searchSettingsMappingsConfigFileName = "cedar-search.json";
    final String rulesSettingsMappingsConfigFileName = "cedar-rules.json";

    config.searchSettingsMappingsConfig =
        getSettingsMappingsConfigFromFile(searchSettingsMappingsConfigFileName, validator,
            substitutingSourceProvider);
    config.rulesSettingsMappingsConfig =
        getSettingsMappingsConfigFromFile(rulesSettingsMappingsConfigFileName, validator,
            substitutingSourceProvider);

    config.linkedDataUtil = new LinkedDataUtil(config.getLinkedDataConfig());
    config.microserviceUrlUtil = new MicroserviceUrlUtil(config.getServers());

    return config;
  }

  private static OpensearchSettingsMappingsConfig getSettingsMappingsConfigFromFile(String configFileName,
                                                                                    Validator validator,
                                                                                    SubstitutingSourceProvider substitutingSourceProvider) {
    final ConfigurationFactory<OpensearchSettingsMappingsConfig> configurationFactory = new
        YamlConfigurationFactory<>(
        OpensearchSettingsMappingsConfig.class, validator, Jackson.newObjectMapper(), "cedar");

    try {
      return configurationFactory.build(substitutingSourceProvider, configFileName);
    } catch (IOException | ConfigurationException e) {
      throw new CedarConfigurationException(configFileName, e);
    }
  }

  /**
   * Builds a fresh configuration from the given environment map. The result is not cached and
   * does not touch the instance {@link #getInstance(Map)} manages; every call constructs a new,
   * independent {@link CedarConfig}.
   */
  public static CedarConfig buildForEnvironment(Map<String, String> environment) {
    return buildInstance(environment);
  }

  /**
   * Returns the shared configuration for the given environment map.
   *
   * <p>The instance is cached together with a snapshot of the environment it was built from.
   * A call whose environment matches the snapshot returns the cached instance. A call whose
   * environment differs materially rebuilds the configuration from the new environment and
   * replaces the cached instance; earlier callers keep their old instance, so anything holding
   * a reference across such a rebuild sees the configuration it was built with. Two
   * environments differ materially when their non-null entries differ; entries whose value is
   * {@code null} count the same as absent entries.
   *
   * <p>For a configuration that is private to the caller, use
   * {@link #buildForEnvironment(Map)} instead.
   */
  public static synchronized CedarConfig getInstance(Map<String, String> environment) {
    Map<String, String> snapshot = nonNullEntries(environment);
    if (instance == null) {
      instance = buildInstance(environment);
      instanceEnvironment = snapshot;
    } else if (!instanceEnvironment.equals(snapshot)) {
      log.info("CedarConfig requested for an environment that differs from the cached one; rebuilding");
      instance = buildInstance(environment);
      instanceEnvironment = snapshot;
    }
    return instance;
  }

  private static Map<String, String> nonNullEntries(Map<String, String> environment) {
    Map<String, String> nonNull = new HashMap<>();
    if (environment != null) {
      for (Map.Entry<String, String> entry : environment.entrySet()) {
        if (entry.getValue() != null) {
          nonNull.put(entry.getKey(), entry.getValue());
        }
      }
    }
    return nonNull;
  }

  public String getHome() {
    return home;
  }

  public String getHost() {
    return host;
  }

  public AdminUserConfig getAdminUserConfig() {
    return adminUserConfig;
  }

  public CaDSRAdminUserConfig getCaDSRAdminUserConfig() {
    return caDSRAdminUserConfig;
  }

  public KeycloakConfig getKeycloakConfig() {
    return keycloakConfig;
  }

  public MongoConfig getArtifactServerConfig() {
    return artifactServerConfig;
  }

  public MongoConfig getUserServerConfig() {
    return userServerConfig;
  }

  public HibernateConfig getMessagingServerConfig() {
    return messagingServerConfig;
  }

  public HibernateConfig getDBLoggingConfig() {
    return dbLoggingConfig;
  }

  public Neo4JConfig getNeo4jConfig() {
    return neo4jConfig;
  }

  public FolderStructureConfig getFolderStructureConfig() {
    return folderStructureConfig;
  }

  public LinkedDataConfig getLinkedDataConfig() {
    return linkedDataConfig;
  }

  public BlueprintUserProfile getBlueprintUserProfile() {
    return blueprintUserProfile;
  }

  public OpensearchConfig getElasticsearchConfig() {
    return opensearchConfig;
  }

  public OpensearchSettingsMappingsConfig getSearchSettingsMappingsConfig() {
    return searchSettingsMappingsConfig;
  }

  public OpensearchSettingsMappingsConfig getRulesSettingsMappingsConfig() {
    return rulesSettingsMappingsConfig;
  }

  public ServersConfig getServers() {
    return servers;
  }

  public SearchSettings getSearchSettings() {
    return searchSettings;
  }

  public ImportExportConfig getImportExportConfig() {
    return importExportConfig;
  }

  public ResourceRESTAPI getResourceRESTAPI() {
    return resourceRESTAPI;
  }

  public ArtifactRESTAPI getArtifactRESTAPI() {
    return artifactRESTAPI;
  }

  public CategoryRESTAPI getCategoryRESTAPI() {
    return categoryRESTAPI;
  }

  public SubmissionConfig getSubmissionConfig() { return submissionConfig; }

  public TrustedFoldersConfig getTrustedFolders() { return trustedFolders; }

  public ExternalAuthoritiesConfig getExternalAuthorities() { return externalAuthorities; }

  public TestUsers getTestUsers() {
    return testUsers;
  }

  public TerminologyConfig getTerminologyConfig() {
    return terminologyConfig;
  }

  public WorkerConfig getWorkerConfig() {
    return workerConfig;
  }

  public CacheConfig getCacheConfig() {
    return cacheConfig;
  }

  public BridgeConfig getBridgeConfig() { return bridgeConfig; }

  // Utility methods

  public LinkedDataUtil getLinkedDataUtil() {
    return linkedDataUtil;
  }

  public MicroserviceUrlUtil getMicroserviceUrlUtil() {
    return microserviceUrlUtil;
  }

}
