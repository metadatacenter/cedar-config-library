package org.metadatacenter.config.environment;

public enum CedarEnvironmentVariable {

  CEDAR_VERSION("CEDAR_VERSION"),
  CEDAR_VERSION_MODIFIER("CEDAR_VERSION_MODIFIER"),

  CEDAR_HOME("CEDAR_HOME"),
  CEDAR_KEYCLOAK_HOME("CEDAR_KEYCLOAK_HOME"),
  CEDAR_KEYCLOAK_ALLOW_INSECURE_TLS("CEDAR_KEYCLOAK_ALLOW_INSECURE_TLS", CedarEnvironmentVariableType.BOOLEAN),

  CEDAR_FRONTEND_BEHAVIOR("CEDAR_FRONTEND_BEHAVIOR"),
  CEDAR_FRONTEND_TARGET("CEDAR_FRONTEND_TARGET"),
  CEDAR_HOST("CEDAR_HOST"),
  CEDAR_NET_GATEWAY("CEDAR_NET_GATEWAY"),

  CEDAR_BIOPORTAL_API_KEY("CEDAR_BIOPORTAL_API_KEY", CedarEnvironmentVariableSecure.YES),
  CEDAR_BIOPORTAL_REST_BASE("CEDAR_BIOPORTAL_REST_BASE"),

  CEDAR_ANALYTICS_KEY("CEDAR_ANALYTICS_KEY", CedarEnvironmentVariableSecure.YES),
  CEDAR_GA4_TRACKING_ID("CEDAR_GA4_TRACKING_ID", CedarEnvironmentVariableSecure.YES),

  CEDAR_NCBI_SRA_FTP_HOST("CEDAR_NCBI_SRA_FTP_HOST"),
  CEDAR_NCBI_SRA_FTP_USER("CEDAR_NCBI_SRA_FTP_USER"),
  CEDAR_NCBI_SRA_FTP_PASSWORD("CEDAR_NCBI_SRA_FTP_PASSWORD", CedarEnvironmentVariableSecure.YES),
  CEDAR_NCBI_SRA_FTP_DIRECTORY("CEDAR_NCBI_SRA_FTP_DIRECTORY"),

  CEDAR_IMMPORT_SUBMISSION_USER("CEDAR_IMMPORT_SUBMISSION_USER"),
  CEDAR_IMMPORT_SUBMISSION_PASSWORD("CEDAR_IMMPORT_SUBMISSION_PASSWORD", CedarEnvironmentVariableSecure.YES),

  CEDAR_ADMIN_USER_PASSWORD("CEDAR_ADMIN_USER_PASSWORD", CedarEnvironmentVariableSecure.YES),
  CEDAR_ADMIN_USER_API_KEY("CEDAR_ADMIN_USER_API_KEY", CedarEnvironmentVariableSecure.YES),

  CEDAR_CADSR_ADMIN_USER_API_KEY("CEDAR_CADSR_ADMIN_USER_API_KEY", CedarEnvironmentVariableSecure.YES),
  CEDAR_CADSR_ONTOLOGIES_FOLDER("CEDAR_CADSR_ONTOLOGIES_FOLDER", CedarEnvironmentVariableSecure.YES),

  CEDAR_NEO4J_HOST("CEDAR_NEO4J_HOST"),
  CEDAR_NEO4J_BOLT_PORT("CEDAR_NEO4J_BOLT_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_NEO4J_USER_NAME("CEDAR_NEO4J_USER_NAME"),
  CEDAR_NEO4J_USER_PASSWORD("CEDAR_NEO4J_USER_PASSWORD", CedarEnvironmentVariableSecure.YES),

  CEDAR_MONGO_APP_USER_NAME("CEDAR_MONGO_APP_USER_NAME"),
  CEDAR_MONGO_APP_USER_PASSWORD("CEDAR_MONGO_APP_USER_PASSWORD", CedarEnvironmentVariableSecure.YES),
  CEDAR_MONGO_HOST("CEDAR_MONGO_HOST"),
  CEDAR_MONGO_PORT("CEDAR_MONGO_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_OPENSEARCH_HOST("CEDAR_OPENSEARCH_HOST"),
  CEDAR_OPENSEARCH_REST_PORT("CEDAR_OPENSEARCH_REST_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_OPENSEARCH_TRANSPORT_PORT("CEDAR_OPENSEARCH_TRANSPORT_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_MESSAGING_MYSQL_HOST("CEDAR_MESSAGING_MYSQL_HOST"),
  CEDAR_MESSAGING_MYSQL_PORT("CEDAR_MESSAGING_MYSQL_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_MESSAGING_MYSQL_DB("CEDAR_MESSAGING_MYSQL_DB"),
  CEDAR_MESSAGING_MYSQL_USER("CEDAR_MESSAGING_MYSQL_USER"),
  CEDAR_MESSAGING_MYSQL_PASSWORD("CEDAR_MESSAGING_MYSQL_PASSWORD", CedarEnvironmentVariableSecure.YES),

  CEDAR_LOG_MYSQL_HOST("CEDAR_LOG_MYSQL_HOST"),
  CEDAR_LOG_MYSQL_PORT("CEDAR_LOG_MYSQL_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_LOG_MYSQL_DB("CEDAR_LOG_MYSQL_DB"),
  CEDAR_LOG_MYSQL_USER("CEDAR_LOG_MYSQL_USER"),
  CEDAR_LOG_MYSQL_PASSWORD("CEDAR_LOG_MYSQL_PASSWORD", CedarEnvironmentVariableSecure.YES),

  // The log aggregation jobs in the worker: LiveAggregatorJob, HistoricalBackfillJob, LogPruneJob.
  // Every one is optional and carries a default in the job that reads it, which is why they were read
  // with System.getenv straight past this model — declaring a variable makes it mandatory, and a batch
  // size that stops a service from booting is worse than one that is merely unset. Declared here as
  // optional so the environment report can show them, and the jobs read them from the sandbox.
  CEDAR_LOG_LIVE_AGG_ENABLED("CEDAR_LOG_LIVE_AGG_ENABLED", CedarEnvironmentVariableType.BOOLEAN,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_LIVE_AGG_BATCH("CEDAR_LOG_LIVE_AGG_BATCH", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_LIVE_AGG_PAUSE_MS("CEDAR_LOG_LIVE_AGG_PAUSE_MS", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_LIVE_AGG_POLL_MS("CEDAR_LOG_LIVE_AGG_POLL_MS", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_LIVE_AGG_MARGIN_HOURS("CEDAR_LOG_LIVE_AGG_MARGIN_HOURS", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),

  CEDAR_LOG_BACKFILL_ENABLED("CEDAR_LOG_BACKFILL_ENABLED", CedarEnvironmentVariableType.BOOLEAN,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_BACKFILL_BATCH("CEDAR_LOG_BACKFILL_BATCH", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_BACKFILL_PAUSE_MS("CEDAR_LOG_BACKFILL_PAUSE_MS", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  // A UTC hour range such as "2-6", so it stays a string rather than a number.
  CEDAR_LOG_BACKFILL_WINDOW_UTC("CEDAR_LOG_BACKFILL_WINDOW_UTC", CedarEnvironmentVariableOptional.YES),

  CEDAR_LOG_PRUNE_ENABLED("CEDAR_LOG_PRUNE_ENABLED", CedarEnvironmentVariableType.BOOLEAN,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_PRUNE_RETENTION_DAYS("CEDAR_LOG_PRUNE_RETENTION_DAYS", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_PRUNE_BATCH("CEDAR_LOG_PRUNE_BATCH", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_PRUNE_PAUSE_MS("CEDAR_LOG_PRUNE_PAUSE_MS", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_LOG_PRUNE_IDLE_MS("CEDAR_LOG_PRUNE_IDLE_MS", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),

  // Keycloak the server, as distinct from the event listener CEDAR ships into it. It reads these from
  // standalone.xml, so nothing in a JVM resolves them and they appear as declarations only.
  CEDAR_KEYCLOAK_HOST("CEDAR_KEYCLOAK_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_HTTP_PORT("CEDAR_KEYCLOAK_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_HTTPS_PORT("CEDAR_KEYCLOAK_HTTPS_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_ADMIN_USER("CEDAR_KEYCLOAK_ADMIN_USER", CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_ADMIN_PASSWORD("CEDAR_KEYCLOAK_ADMIN_PASSWORD", CedarEnvironmentVariableSecure.YES,
      CedarEnvironmentVariableType.STRING, CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_MYSQL_HOST("CEDAR_KEYCLOAK_MYSQL_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_MYSQL_PORT("CEDAR_KEYCLOAK_MYSQL_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_MYSQL_DB("CEDAR_KEYCLOAK_MYSQL_DB", CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_MYSQL_USER("CEDAR_KEYCLOAK_MYSQL_USER", CedarEnvironmentVariableOptional.YES),
  CEDAR_KEYCLOAK_MYSQL_PASSWORD("CEDAR_KEYCLOAK_MYSQL_PASSWORD", CedarEnvironmentVariableSecure.YES,
      CedarEnvironmentVariableType.STRING, CedarEnvironmentVariableOptional.YES),
  CEDAR_CA("CEDAR_CA", CedarEnvironmentVariableOptional.YES),

  // The infrastructure layer. Every one is optional: no JVM resolves any of them, so requiring one
  // would fail a boot over a variable that boot never reads. They are declared so the environment page
  // can account for them, which is the only thing that was missing.
  //
  // nginx: the reverse proxy in front of everything, whose config is generated from these.
  CEDAR_NGINX_HOST("CEDAR_NGINX_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_NGINX_HTTP_PORT("CEDAR_NGINX_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_NGINX_HTTPS_PORT("CEDAR_NGINX_HTTPS_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_MICROSERVICE_HOST("CEDAR_MICROSERVICE_HOST", CedarEnvironmentVariableOptional.YES),

  // Where nginx proxies each frontend. Distinct from CEDAR_FRONTEND_TARGET and from the
  // CEDAR_FRONTEND_<target>_* family the gulp build generates, which cannot be enumerated here because
  // their names are built from the target at build time.
  CEDAR_FRONTEND_EDITOR_HOST("CEDAR_FRONTEND_EDITOR_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_EDITOR_PORT("CEDAR_FRONTEND_EDITOR_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_WORKSPACE_HOST("CEDAR_FRONTEND_WORKSPACE_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_WORKSPACE_PORT("CEDAR_FRONTEND_WORKSPACE_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_DESIGNER_HOST("CEDAR_FRONTEND_DESIGNER_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_DESIGNER_PORT("CEDAR_FRONTEND_DESIGNER_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_OPENVIEW_HOST("CEDAR_FRONTEND_OPENVIEW_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_OPENVIEW_PORT("CEDAR_FRONTEND_OPENVIEW_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_CONTENT_HOST("CEDAR_FRONTEND_CONTENT_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_CONTENT_PORT("CEDAR_FRONTEND_CONTENT_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_MONITORING_HOST("CEDAR_FRONTEND_MONITORING_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_MONITORING_PORT("CEDAR_FRONTEND_MONITORING_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_BRIDGING_HOST("CEDAR_FRONTEND_BRIDGING_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_FRONTEND_BRIDGING_PORT("CEDAR_FRONTEND_BRIDGING_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),

  // Mongo's container init: the root account provisions the application account.
  CEDAR_MONGO_ROOT_USER_NAME("CEDAR_MONGO_ROOT_USER_NAME", CedarEnvironmentVariableOptional.YES),
  CEDAR_MONGO_ROOT_USER_PASSWORD("CEDAR_MONGO_ROOT_USER_PASSWORD", CedarEnvironmentVariableSecure.YES,
      CedarEnvironmentVariableType.STRING, CedarEnvironmentVariableOptional.YES),
  CEDAR_MONGO_APP_DATABASE_NAME("CEDAR_MONGO_APP_DATABASE_NAME", CedarEnvironmentVariableOptional.YES),

  CEDAR_MYSQL_ROOT_PASSWORD("CEDAR_MYSQL_ROOT_PASSWORD", CedarEnvironmentVariableSecure.YES,
      CedarEnvironmentVariableType.STRING, CedarEnvironmentVariableOptional.YES),

  CEDAR_NEO4J_HOME("CEDAR_NEO4J_HOME", CedarEnvironmentVariableOptional.YES),
  CEDAR_NEO4J_REST_PORT("CEDAR_NEO4J_REST_PORT", CedarEnvironmentVariableType.NUMERIC,
      CedarEnvironmentVariableOptional.YES),

  // The local certificate authority the development and server profiles generate TLS material from.
  CEDAR_CA_HOME("CEDAR_CA_HOME", CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_PASSWORD("CEDAR_CA_PASSWORD", CedarEnvironmentVariableSecure.YES,
      CedarEnvironmentVariableType.STRING, CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_COMMON_NAME("CEDAR_CA_COMMON_NAME", CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_COUNTRY("CEDAR_CA_COUNTRY", CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_STATE("CEDAR_CA_STATE", CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_LOC("CEDAR_CA_LOC", CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_ORG("CEDAR_CA_ORG", CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_ORG_UNIT("CEDAR_CA_ORG_UNIT", CedarEnvironmentVariableOptional.YES),
  CEDAR_CA_EMAIL("CEDAR_CA_EMAIL", CedarEnvironmentVariableOptional.YES),

  CEDAR_NET_SUBNET("CEDAR_NET_SUBNET", CedarEnvironmentVariableOptional.YES),

  // Read by CedarMicroserviceApplication through System.getenv, with "*" as its default - the same
  // shape as the log aggregation settings, and modelled for the same reason.
  CEDAR_CORS_ALLOWED_ORIGINS("CEDAR_CORS_ALLOWED_ORIGINS", CedarEnvironmentVariableOptional.YES),

  // The terminology server's local ontology store. cedar-services.sh turns these into -D properties
  // only when CEDAR_TERMINOLOGY_STORE_CATALOG is set, so each one is optional.
  CEDAR_TERMINOLOGY_LOCAL_ONTOLOGIES("CEDAR_TERMINOLOGY_LOCAL_ONTOLOGIES", CedarEnvironmentVariableOptional.YES),
  CEDAR_TERMINOLOGY_LOCAL_ROOTS_ONTOLOGIES("CEDAR_TERMINOLOGY_LOCAL_ROOTS_ONTOLOGIES",
      CedarEnvironmentVariableOptional.YES),
  CEDAR_TERMINOLOGY_LOCAL_ONLY("CEDAR_TERMINOLOGY_LOCAL_ONLY", CedarEnvironmentVariableType.BOOLEAN,
      CedarEnvironmentVariableOptional.YES),

  // The caDSR importer and the production cron that drives it.
  CEDAR_NCI_CADSR_FTP_HOST("CEDAR_NCI_CADSR_FTP_HOST", CedarEnvironmentVariableOptional.YES),
  CEDAR_NCI_CADSR_FTP_USER("CEDAR_NCI_CADSR_FTP_USER", CedarEnvironmentVariableOptional.YES),
  CEDAR_NCI_CADSR_FTP_PASSWORD("CEDAR_NCI_CADSR_FTP_PASSWORD", CedarEnvironmentVariableSecure.YES,
      CedarEnvironmentVariableType.STRING, CedarEnvironmentVariableOptional.YES),
  CEDAR_NCI_CADSR_FTP_CDES_DIRECTORY("CEDAR_NCI_CADSR_FTP_CDES_DIRECTORY", CedarEnvironmentVariableOptional.YES),
  CEDAR_NCI_CADSR_FTP_CLASSIFICATIONS_DIRECTORY("CEDAR_NCI_CADSR_FTP_CLASSIFICATIONS_DIRECTORY",
      CedarEnvironmentVariableOptional.YES),
  CEDAR_CDE_FOLDER_ID("CEDAR_CDE_FOLDER_ID", CedarEnvironmentVariableOptional.YES),

  // cedarcli, which is Python and reads these directly.
  CEDAR_DEVELOP_HOME("CEDAR_DEVELOP_HOME", CedarEnvironmentVariableOptional.YES),
  CEDAR_UTIL_BIN("CEDAR_UTIL_BIN", CedarEnvironmentVariableOptional.YES),
  CEDAR_DEV_BUILD_FRONTENDS("CEDAR_DEV_BUILD_FRONTENDS", CedarEnvironmentVariableType.BOOLEAN,
      CedarEnvironmentVariableOptional.YES),
  CEDAR_DEV_USE_PRIVATE_REPOS("CEDAR_DEV_USE_PRIVATE_REPOS", CedarEnvironmentVariableType.BOOLEAN,
      CedarEnvironmentVariableOptional.YES),


  CEDAR_SUBMISSION_TEMPLATE_ID_1("CEDAR_SUBMISSION_TEMPLATE_ID_1"),
  CEDAR_SUBMISSION_TEMPLATE_ID_2("CEDAR_SUBMISSION_TEMPLATE_ID_2"),

  CEDAR_SALT_API_KEY("CEDAR_SALT_API_KEY", CedarEnvironmentVariableSecure.YES),

  CEDAR_TEST_USER1_ID("CEDAR_TEST_USER1_ID"),
  CEDAR_TEST_USER2_ID("CEDAR_TEST_USER2_ID"),

  CEDAR_TRUSTED_FOLDERS("CEDAR_TRUSTED_FOLDERS"),

  CEDAR_ROR_API_PREFIX("CEDAR_ROR_API_PREFIX"),
  CEDAR_ORCID_TOKEN_PREFIX("CEDAR_ORCID_TOKEN_PREFIX"),
  CEDAR_ORCID_API_PREFIX("CEDAR_ORCID_API_PREFIX"),
  CEDAR_ORCID_API_CLIENT_ID("CEDAR_ORCID_API_CLIENT_ID"),
  CEDAR_ORCID_API_CLIENT_SECRET("CEDAR_ORCID_API_CLIENT_SECRET", CedarEnvironmentVariableSecure.YES),
  CEDAR_COMP_TOX_API_PREFIX("CEDAR_COMP_TOX_API_PREFIX"),
  CEDAR_COMP_TOX_API_KEY("CEDAR_COMP_TOX_API_KEY", CedarEnvironmentVariableSecure.YES),
  CEDAR_RRID_API_KEY("CEDAR_RRID_API_KEY", CedarEnvironmentVariableSecure.YES),
  CEDAR_PUBMED_API_KEY("CEDAR_PUBMED_API_KEY", CedarEnvironmentVariableSecure.YES),

  CEDAR_DATACITE_REPOSITORY_ID("CEDAR_DATACITE_REPOSITORY_ID"),
  CEDAR_DATACITE_REPOSITORY_PASSWORD("CEDAR_DATACITE_REPOSITORY_PASSWORD", CedarEnvironmentVariableSecure.YES),
  CEDAR_DATACITE_REPOSITORY_PREFIX("CEDAR_DATACITE_REPOSITORY_PREFIX"),
  CEDAR_DATACITE_API_ENDPOINT_URL("CEDAR_DATACITE_API_ENDPOINT_URL"),
  CEDAR_DATACITE_TEMPLATE_ID("CEDAR_DATACITE_TEMPLATE_ID"),
  CEDAR_DATACITE_ENABLED("CEDAR_DATACITE_ENABLED", CedarEnvironmentVariableType.BOOLEAN),

  CEDAR_REDIS_PERSISTENT_HOST("CEDAR_REDIS_PERSISTENT_HOST"),
  CEDAR_REDIS_PERSISTENT_PORT("CEDAR_REDIS_PERSISTENT_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_GROUP_SERVER_HOST("CEDAR_GROUP_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_GROUP_HTTP_PORT("CEDAR_GROUP_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_GROUP_ADMIN_PORT("CEDAR_GROUP_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_GROUP_STOP_PORT("CEDAR_GROUP_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_MESSAGING_SERVER_HOST("CEDAR_MESSAGING_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_MESSAGING_HTTP_PORT("CEDAR_MESSAGING_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_MESSAGING_ADMIN_PORT("CEDAR_MESSAGING_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_MESSAGING_STOP_PORT("CEDAR_MESSAGING_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_REPO_SERVER_HOST("CEDAR_REPO_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_REPO_HTTP_PORT("CEDAR_REPO_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_REPO_ADMIN_PORT("CEDAR_REPO_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_REPO_STOP_PORT("CEDAR_REPO_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_RESOURCE_SERVER_HOST("CEDAR_RESOURCE_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_RESOURCE_HTTP_PORT("CEDAR_RESOURCE_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_RESOURCE_ADMIN_PORT("CEDAR_RESOURCE_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_RESOURCE_STOP_PORT("CEDAR_RESOURCE_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_SCHEMA_SERVER_HOST("CEDAR_SCHEMA_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_SCHEMA_HTTP_PORT("CEDAR_SCHEMA_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_SCHEMA_ADMIN_PORT("CEDAR_SCHEMA_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_SCHEMA_STOP_PORT("CEDAR_SCHEMA_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_SUBMISSION_SERVER_HOST("CEDAR_SUBMISSION_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_SUBMISSION_HTTP_PORT("CEDAR_SUBMISSION_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_SUBMISSION_ADMIN_PORT("CEDAR_SUBMISSION_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_SUBMISSION_STOP_PORT("CEDAR_SUBMISSION_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_ARTIFACT_SERVER_HOST("CEDAR_ARTIFACT_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_ARTIFACT_HTTP_PORT("CEDAR_ARTIFACT_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_ARTIFACT_ADMIN_PORT("CEDAR_ARTIFACT_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_ARTIFACT_STOP_PORT("CEDAR_ARTIFACT_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_TERMINOLOGY_SERVER_HOST("CEDAR_TERMINOLOGY_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_TERMINOLOGY_HTTP_PORT("CEDAR_TERMINOLOGY_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_TERMINOLOGY_ADMIN_PORT("CEDAR_TERMINOLOGY_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_TERMINOLOGY_STOP_PORT("CEDAR_TERMINOLOGY_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_USER_SERVER_HOST("CEDAR_USER_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_USER_HTTP_PORT("CEDAR_USER_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_USER_ADMIN_PORT("CEDAR_USER_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_USER_STOP_PORT("CEDAR_USER_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_VALUERECOMMENDER_SERVER_HOST("CEDAR_VALUERECOMMENDER_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_VALUERECOMMENDER_HTTP_PORT("CEDAR_VALUERECOMMENDER_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_VALUERECOMMENDER_ADMIN_PORT("CEDAR_VALUERECOMMENDER_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_VALUERECOMMENDER_STOP_PORT("CEDAR_VALUERECOMMENDER_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_WORKER_SERVER_HOST("CEDAR_WORKER_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_WORKER_HTTP_PORT("CEDAR_WORKER_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_WORKER_ADMIN_PORT("CEDAR_WORKER_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_WORKER_STOP_PORT("CEDAR_WORKER_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_OPENVIEW_SERVER_HOST("CEDAR_OPENVIEW_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_OPENVIEW_HTTP_PORT("CEDAR_OPENVIEW_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_OPENVIEW_ADMIN_PORT("CEDAR_OPENVIEW_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_OPENVIEW_STOP_PORT("CEDAR_OPENVIEW_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_MONITOR_SERVER_HOST("CEDAR_MONITOR_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_MONITOR_HTTP_PORT("CEDAR_MONITOR_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_MONITOR_ADMIN_PORT("CEDAR_MONITOR_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_MONITOR_STOP_PORT("CEDAR_MONITOR_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_IMPEX_SERVER_HOST("CEDAR_IMPEX_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_IMPEX_HTTP_PORT("CEDAR_IMPEX_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_IMPEX_ADMIN_PORT("CEDAR_IMPEX_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_IMPEX_STOP_PORT("CEDAR_IMPEX_STOP_PORT", CedarEnvironmentVariableType.NUMERIC),

  CEDAR_BRIDGE_SERVER_HOST("CEDAR_BRIDGE_SERVER_HOST", CedarEnvironmentVariableType.STRING),
  CEDAR_BRIDGE_HTTP_PORT("CEDAR_BRIDGE_HTTP_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_BRIDGE_ADMIN_PORT("CEDAR_BRIDGE_ADMIN_PORT", CedarEnvironmentVariableType.NUMERIC),
  CEDAR_BRIDGE_STOP_PORT("CEDAR_BRIDGE_STOP_PORT", CedarEnvironmentVariableType.NUMERIC);

  private final String name;
  private final CedarEnvironmentVariableSecure secure;
  private final CedarEnvironmentVariableType type;
  private final CedarEnvironmentVariableOptional optional;

  CedarEnvironmentVariable(String name) {
    this(name, CedarEnvironmentVariableSecure.NO, CedarEnvironmentVariableType.STRING,
        CedarEnvironmentVariableOptional.NO);
  }

  CedarEnvironmentVariable(String name, CedarEnvironmentVariableType type) {
    this(name, CedarEnvironmentVariableSecure.NO, type, CedarEnvironmentVariableOptional.NO);
  }

  CedarEnvironmentVariable(String name, CedarEnvironmentVariableSecure secure) {
    this(name, secure, CedarEnvironmentVariableType.STRING, CedarEnvironmentVariableOptional.NO);
  }

  CedarEnvironmentVariable(String name, CedarEnvironmentVariableOptional optional) {
    this(name, CedarEnvironmentVariableSecure.NO, CedarEnvironmentVariableType.STRING, optional);
  }

  CedarEnvironmentVariable(String name, CedarEnvironmentVariableType type,
                           CedarEnvironmentVariableOptional optional) {
    this(name, CedarEnvironmentVariableSecure.NO, type, optional);
  }

  CedarEnvironmentVariable(String name, CedarEnvironmentVariableSecure secure,
                           CedarEnvironmentVariableType type,
                           CedarEnvironmentVariableOptional optional) {
    this.name = name;
    this.secure = secure;
    this.type = type;
    this.optional = optional;
  }

  public String getName() {
    return name;
  }

  public boolean isNumeric() {
    return type == CedarEnvironmentVariableType.NUMERIC;
  }

  public boolean isBoolean() {
    return type == CedarEnvironmentVariableType.BOOLEAN;
  }

  /**
   * Whether the component that declares it can run without it, falling back to its own default.
   * An optional variable is reported like any other and simply may be absent.
   */
  public boolean isOptional() {
    return optional == CedarEnvironmentVariableOptional.YES;
  }

  public boolean isSecure() {
    return secure == CedarEnvironmentVariableSecure.YES;
  }

  public static CedarEnvironmentVariable forName(String name) {
    for (CedarEnvironmentVariable ev : values()) {
      if (ev.getName().equals(name)) {
        return ev;
      }
    }
    return null;
  }
}
