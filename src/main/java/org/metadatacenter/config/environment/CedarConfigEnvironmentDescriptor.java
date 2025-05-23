package org.metadatacenter.config.environment;

import org.metadatacenter.model.SystemComponent;

import java.util.*;

public class CedarConfigEnvironmentDescriptor {

  private static final Map<CedarEnvironmentVariable, Set<SystemComponent>> variableToComponent;
  private static final Map<SystemComponent, Set<CedarEnvironmentVariable>> componentToVariable;

  static {
    List<SystemComponent> allMicroservices = new ArrayList<>();
    for (SystemComponent sc : SystemComponent.values()) {
      if (sc.getServerName() != null) {
        allMicroservices.add(sc);
      }
    }

    variableToComponent = new LinkedHashMap<>();
    for (CedarEnvironmentVariable env : CedarEnvironmentVariable.values()) {
      Set<SystemComponent> systemComponents = new LinkedHashSet<>();
      systemComponents.add(SystemComponent.ALL);
      variableToComponent.put(env, systemComponents);
    }

    Set<SystemComponent> cedarVersion = variableToComponent.get(CedarEnvironmentVariable.CEDAR_VERSION);
    // gulpfile.js replaces in version.js
    cedarVersion.add(SystemComponent.FRONTEND_DEVELOPMENT);
    cedarVersion.add(SystemComponent.FRONTEND_TEST);
    cedarVersion.add(SystemComponent.FRONTEND_PRODUCTION);

    Set<SystemComponent> cedarVersionModifier = variableToComponent.get(CedarEnvironmentVariable.CEDAR_VERSION_MODIFIER);
    // gulpfile.js replaces in version.js
    cedarVersionModifier.add(SystemComponent.FRONTEND_DEVELOPMENT);
    cedarVersionModifier.add(SystemComponent.FRONTEND_TEST);
    cedarVersionModifier.add(SystemComponent.FRONTEND_PRODUCTION);

    Set<SystemComponent> cedarHome = variableToComponent.get(CedarEnvironmentVariable.CEDAR_HOME);
    cedarHome.add(SystemComponent.ADMIN_TOOL); // generate-nginx-config, export dir
    cedarHome.add(SystemComponent.CADSR_TOOL);
    cedarHome.add(SystemComponent.UTIL_BIN); // utility shell scrips

    Set<SystemComponent> keycloakHome = variableToComponent.get(CedarEnvironmentVariable.CEDAR_KEYCLOAK_HOME);
    keycloakHome.add(SystemComponent.UTIL_BIN); // utility shell scrips

    Set<SystemComponent> cedarFrontendBeh = variableToComponent.get(CedarEnvironmentVariable.CEDAR_FRONTEND_BEHAVIOR);
    // gulpfile.js decides that it should or not start a server for the front end code
    cedarFrontendBeh.add(SystemComponent.FRONTEND_DEVELOPMENT);
    cedarFrontendBeh.add(SystemComponent.FRONTEND_TEST);
    cedarFrontendBeh.add(SystemComponent.FRONTEND_PRODUCTION);

    Set<SystemComponent> cedarFrontendTarget = variableToComponent.get(CedarEnvironmentVariable.CEDAR_FRONTEND_TARGET);
    // gulpfile.js replaces this in several files, frontend connects to this server
    cedarFrontendTarget.add(SystemComponent.FRONTEND_DEVELOPMENT);
    cedarFrontendTarget.add(SystemComponent.FRONTEND_TEST);
    cedarFrontendTarget.add(SystemComponent.FRONTEND_PRODUCTION);

    Set<SystemComponent> cedarHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_HOST);
    cedarHost.add(SystemComponent.ADMIN_TOOL); // generate-nginx-config, all Keycloak related tasks
    cedarHost.add(SystemComponent.CADSR_TOOL);
    // gulpfile.js replaces values in url-service.conf.json and test-env.js
    cedarHost.add(SystemComponent.FRONTEND_DEVELOPMENT);
    cedarHost.add(SystemComponent.FRONTEND_TEST);
    cedarHost.add(SystemComponent.FRONTEND_PRODUCTION);
    // all the microservices
    cedarHost.addAll(allMicroservices);

    Set<SystemComponent> cedarNetGateway = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NET_GATEWAY);
    cedarNetGateway.add(SystemComponent.ADMIN_TOOL);
    cedarNetGateway.add(SystemComponent.CADSR_TOOL);
    // all the microservices
    cedarNetGateway.addAll(allMicroservices);

    Set<SystemComponent> nciCADSRValueSetsOntologyFilePath = variableToComponent.get(CedarEnvironmentVariable.CEDAR_CADSR_ONTOLOGIES_FOLDER);
    nciCADSRValueSetsOntologyFilePath.add(SystemComponent.SERVER_RESOURCE);

    Set<SystemComponent> cedarBioportalApiKey = variableToComponent.get(CedarEnvironmentVariable.CEDAR_BIOPORTAL_API_KEY);
    cedarBioportalApiKey.add(SystemComponent.SERVER_TERMINOLOGY);

    Set<SystemComponent> cedarBioportalRestBase = variableToComponent.get(CedarEnvironmentVariable.CEDAR_BIOPORTAL_REST_BASE);
    cedarBioportalRestBase.add(SystemComponent.SERVER_TERMINOLOGY);

    Set<SystemComponent> cedarAnalyticsKey = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ANALYTICS_KEY);
    // gulpfile.js replaces int tracking-service.conf.json
    cedarAnalyticsKey.add(SystemComponent.FRONTEND_DEVELOPMENT);
    cedarAnalyticsKey.add(SystemComponent.FRONTEND_TEST);
    cedarAnalyticsKey.add(SystemComponent.FRONTEND_PRODUCTION);

    Set<SystemComponent> cedarGA4TrackingId = variableToComponent.get(CedarEnvironmentVariable.CEDAR_GA4_TRACKING_ID);
    // gulpfile.js replaces int version.js
    cedarGA4TrackingId.add(SystemComponent.FRONTEND_DEVELOPMENT);
    cedarGA4TrackingId.add(SystemComponent.FRONTEND_TEST);
    cedarGA4TrackingId.add(SystemComponent.FRONTEND_PRODUCTION);

    Set<SystemComponent> cedarNcbiSraFtpPassword = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NCBI_SRA_FTP_PASSWORD);
    cedarNcbiSraFtpPassword.add(SystemComponent.SERVER_SUBMISSION);

    Set<SystemComponent> cedarNcbiSraFtpUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NCBI_SRA_FTP_USER);
    cedarNcbiSraFtpUser.addAll(cedarNcbiSraFtpPassword);

    Set<SystemComponent> cedarNcbiSraFtpHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NCBI_SRA_FTP_HOST);
    cedarNcbiSraFtpHost.addAll(cedarNcbiSraFtpPassword);

    Set<SystemComponent> cedarNcbiSraFtpDirectory = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NCBI_SRA_FTP_DIRECTORY);
    cedarNcbiSraFtpDirectory.addAll(cedarNcbiSraFtpPassword);

    Set<SystemComponent> cedarImmPortSubmissionUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_IMMPORT_SUBMISSION_USER);
    cedarImmPortSubmissionUser.add(SystemComponent.SERVER_SUBMISSION);

    Set<SystemComponent> cedarImmPortSubmissionPassword = variableToComponent.get(CedarEnvironmentVariable.CEDAR_IMMPORT_SUBMISSION_PASSWORD);
    cedarImmPortSubmissionPassword.add(SystemComponent.SERVER_SUBMISSION);

    Set<SystemComponent> cedarNeo4jUserPassword = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NEO4J_USER_PASSWORD);
    cedarNeo4jUserPassword.add(SystemComponent.ADMIN_TOOL);
    cedarNeo4jUserPassword.add(SystemComponent.CADSR_TOOL);
    cedarNeo4jUserPassword.addAll(allMicroservices);

    Set<SystemComponent> cedarNeo4jUserName = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NEO4J_USER_NAME);
    cedarNeo4jUserName.addAll(cedarNeo4jUserPassword);

    Set<SystemComponent> cedarNeo4jHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NEO4J_HOST);
    cedarNeo4jHost.addAll(cedarNeo4jUserPassword);

    Set<SystemComponent> cedarNeo4jBoltPort = variableToComponent.get(CedarEnvironmentVariable.CEDAR_NEO4J_BOLT_PORT);
    cedarNeo4jBoltPort.addAll(cedarNeo4jUserPassword);

    Set<SystemComponent> cedarAdminUserApiKey = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ADMIN_USER_API_KEY);
    cedarAdminUserApiKey.add(SystemComponent.ADMIN_TOOL); // all neo4j, keycloak related tasks + search reindex
    cedarAdminUserApiKey.add(SystemComponent.CADSR_TOOL);
    cedarAdminUserApiKey.add(SystemComponent.KEYCLOAK_EVENT_LISTENER); // user login callback, auth with this
    cedarAdminUserApiKey.add(SystemComponent.SERVER_RESOURCE); // index regeneration
    cedarAdminUserApiKey.add(SystemComponent.SERVER_WORKER); // SearchPermissionExecutorService
    cedarAdminUserApiKey.add(SystemComponent.SERVER_MESSAGING); // messages from processes
    cedarAdminUserApiKey.add(SystemComponent.SERVER_SUBMISSION);
    cedarAdminUserApiKey.add(SystemComponent.SERVER_MONITOR);
    cedarAdminUserApiKey.add(SystemComponent.SERVER_OPENVIEW);
    cedarAdminUserApiKey.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> cedarAdminUserPasswd = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ADMIN_USER_PASSWORD);
    cedarAdminUserPasswd.add(SystemComponent.ADMIN_TOOL); // all keycloak related tasks
    cedarAdminUserPasswd.add(SystemComponent.CADSR_TOOL);
    cedarAdminUserPasswd.add(SystemComponent.SERVER_MONITOR);
    cedarAdminUserPasswd.add(SystemComponent.SERVER_USER); // Needed to get external identifiers for login providers, e.g., ORCID

    Set<SystemComponent> cedarCaDSRAdminUserApiKey = variableToComponent.get(CedarEnvironmentVariable.CEDAR_CADSR_ADMIN_USER_API_KEY);
    cedarCaDSRAdminUserApiKey.add(SystemComponent.ADMIN_TOOL);
    cedarCaDSRAdminUserApiKey.add(SystemComponent.CADSR_TOOL);
    cedarCaDSRAdminUserApiKey.add(SystemComponent.SERVER_IMPEX);

    Set<SystemComponent> cedarMongoUserName = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONGO_APP_USER_NAME);
    cedarMongoUserName.add(SystemComponent.ADMIN_TOOL);
    cedarMongoUserName.add(SystemComponent.CADSR_TOOL);
    // all the microservices for MongoDB initialization for user details
    cedarMongoUserName.addAll(allMicroservices);

    Set<SystemComponent> cedarMongoUserPasswd = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONGO_APP_USER_PASSWORD);
    cedarMongoUserPasswd.addAll(cedarMongoUserName);// used together with the username

    Set<SystemComponent> cedarMongoHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONGO_HOST);
    cedarMongoHost.addAll(cedarMongoUserName);// used together with the username

    Set<SystemComponent> cedarPortMongo = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONGO_PORT);
    cedarPortMongo.addAll(cedarMongoUserName);// used together with the username

    Set<SystemComponent> cedarOpensearchHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_OPENSEARCH_HOST);
    cedarOpensearchHost.add(SystemComponent.SERVER_WORKER);
    cedarOpensearchHost.add(SystemComponent.SERVER_VALUERECOMMENDER);
    cedarOpensearchHost.add(SystemComponent.SERVER_RESOURCE);
    cedarOpensearchHost.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarOpenSearchRestPort = variableToComponent.get(CedarEnvironmentVariable.CEDAR_OPENSEARCH_REST_PORT);
    cedarOpenSearchRestPort.addAll(cedarOpensearchHost);

    Set<SystemComponent> cedarOpenSearchTransportPort = variableToComponent.get(CedarEnvironmentVariable.CEDAR_OPENSEARCH_TRANSPORT_PORT);
    cedarOpenSearchTransportPort.addAll(cedarOpensearchHost);

    Set<SystemComponent> cedarMessagingMysqlHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_MYSQL_HOST);
    cedarMessagingMysqlHost.add(SystemComponent.SERVER_MESSAGING);
    cedarMessagingMysqlHost.add(SystemComponent.SERVER_WORKER);

    Set<SystemComponent> cedarMessagingMysqlPort = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_MYSQL_PORT);
    cedarMessagingMysqlPort.add(SystemComponent.SERVER_MESSAGING);
    cedarMessagingMysqlPort.add(SystemComponent.SERVER_WORKER);

    Set<SystemComponent> cedarMessagingMysqlDb = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_MYSQL_DB);
    cedarMessagingMysqlDb.add(SystemComponent.SERVER_MESSAGING);
    cedarMessagingMysqlDb.add(SystemComponent.SERVER_WORKER);

    Set<SystemComponent> cedarMessagingMysqlUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_MYSQL_USER);
    cedarMessagingMysqlUser.add(SystemComponent.SERVER_MESSAGING);
    cedarMessagingMysqlUser.add(SystemComponent.SERVER_WORKER);

    Set<SystemComponent> cedarMessagingMysqlPassword = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_MYSQL_PASSWORD);
    cedarMessagingMysqlPassword.add(SystemComponent.SERVER_MESSAGING);
    cedarMessagingMysqlPassword.add(SystemComponent.SERVER_WORKER);

    Set<SystemComponent> cedarLoggingMysqlHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_LOG_MYSQL_HOST);
    cedarLoggingMysqlHost.add(SystemComponent.SERVER_WORKER);
    cedarLoggingMysqlHost.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarLoggingMysqlPort = variableToComponent.get(CedarEnvironmentVariable.CEDAR_LOG_MYSQL_PORT);
    cedarLoggingMysqlPort.add(SystemComponent.SERVER_WORKER);
    cedarLoggingMysqlPort.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarLoggingMysqlDb = variableToComponent.get(CedarEnvironmentVariable.CEDAR_LOG_MYSQL_DB);
    cedarLoggingMysqlDb.add(SystemComponent.SERVER_WORKER);
    cedarLoggingMysqlDb.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarLoggingMysqlUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_LOG_MYSQL_USER);
    cedarLoggingMysqlUser.add(SystemComponent.SERVER_WORKER);
    cedarLoggingMysqlUser.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarLoggingMysqlPassword = variableToComponent.get(CedarEnvironmentVariable.CEDAR_LOG_MYSQL_PASSWORD);
    cedarLoggingMysqlPassword.add(SystemComponent.SERVER_WORKER);
    cedarLoggingMysqlPassword.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarValidationEnabled = variableToComponent.get(CedarEnvironmentVariable.CEDAR_VALIDATION_ENABLED);
    cedarValidationEnabled.add(SystemComponent.SERVER_ARTIFACT);

    Set<SystemComponent> submissionTemplateId1 = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SUBMISSION_TEMPLATE_ID_1);
    submissionTemplateId1.add(SystemComponent.SERVER_WORKER);
    submissionTemplateId1.add(SystemComponent.SERVER_RESOURCE);

    Set<SystemComponent> submissionTemplateId2 = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SUBMISSION_TEMPLATE_ID_2);
    submissionTemplateId2.add(SystemComponent.SERVER_WORKER);
    submissionTemplateId2.add(SystemComponent.SERVER_RESOURCE);

    Set<SystemComponent> cedarSaltApiKey = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SALT_API_KEY);
    cedarSaltApiKey.add(SystemComponent.ADMIN_TOOL); //profile creation
    cedarSaltApiKey.add(SystemComponent.CADSR_TOOL);
    cedarSaltApiKey.add(SystemComponent.SERVER_RESOURCE); //profile creation triggered by event listener

    Set<SystemComponent> dataCiteRepositoryId = variableToComponent.get(CedarEnvironmentVariable.CEDAR_DATACITE_REPOSITORY_ID);
    dataCiteRepositoryId.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> dataCiteRepositoryPassword = variableToComponent.get(CedarEnvironmentVariable.CEDAR_DATACITE_REPOSITORY_PASSWORD);
    dataCiteRepositoryPassword.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> dataCiteRepositoryPrefix = variableToComponent.get(CedarEnvironmentVariable.CEDAR_DATACITE_REPOSITORY_PREFIX);
    dataCiteRepositoryPrefix.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> dataCiteApiEndpointUrl = variableToComponent.get(CedarEnvironmentVariable.CEDAR_DATACITE_API_ENDPOINT_URL);
    dataCiteApiEndpointUrl.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> dataCiteTemplateId = variableToComponent.get(CedarEnvironmentVariable.CEDAR_DATACITE_TEMPLATE_ID);
    dataCiteTemplateId.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> dataCiteEnabled = variableToComponent.get(CedarEnvironmentVariable.CEDAR_DATACITE_ENABLED);
    dataCiteEnabled.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> redisPersistentHost = variableToComponent.get(CedarEnvironmentVariable.CEDAR_REDIS_PERSISTENT_HOST);
    redisPersistentHost.addAll(allMicroservices);
    redisPersistentHost.add(SystemComponent.ADMIN_TOOL);
    redisPersistentHost.add(SystemComponent.CADSR_TOOL);

    Set<SystemComponent> redisPersistentPort = variableToComponent.get(CedarEnvironmentVariable.CEDAR_REDIS_PERSISTENT_PORT);
    redisPersistentPort.addAll(allMicroservices);
    redisPersistentPort.add(SystemComponent.ADMIN_TOOL);
    redisPersistentPort.add(SystemComponent.CADSR_TOOL);

    Set<SystemComponent> cedarHttpPortGroup = variableToComponent.get(CedarEnvironmentVariable.CEDAR_GROUP_HTTP_PORT);
    cedarHttpPortGroup.add(SystemComponent.SERVER_GROUP);
    Set<SystemComponent> cedarAdminPortGroup = variableToComponent.get(CedarEnvironmentVariable.CEDAR_GROUP_ADMIN_PORT);
    cedarAdminPortGroup.add(SystemComponent.SERVER_GROUP);
    cedarAdminPortGroup.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortGroup = variableToComponent.get(CedarEnvironmentVariable.CEDAR_GROUP_STOP_PORT);
    cedarStopPortGroup.add(SystemComponent.SERVER_GROUP);
    Set<SystemComponent> cedarServerHostGroup = variableToComponent.get(CedarEnvironmentVariable.CEDAR_GROUP_SERVER_HOST);
    cedarServerHostGroup.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortMessaging = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_HTTP_PORT);
    cedarHttpPortMessaging.add(SystemComponent.SERVER_MESSAGING);
    cedarHttpPortMessaging.add(SystemComponent.SERVER_SUBMISSION);
    Set<SystemComponent> cedarAdminPortMessaging = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_ADMIN_PORT);
    cedarAdminPortMessaging.add(SystemComponent.SERVER_MESSAGING);
    cedarAdminPortMessaging.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortMessaging = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_STOP_PORT);
    cedarStopPortMessaging.add(SystemComponent.SERVER_MESSAGING);
    Set<SystemComponent> cedarServerHostMessaging = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MESSAGING_SERVER_HOST);
    cedarServerHostMessaging.add(SystemComponent.SERVER_SUBMISSION);
    cedarServerHostMessaging.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_USER_HTTP_PORT);
    cedarHttpPortUser.addAll(allMicroservices);
    Set<SystemComponent> cedarAdminPortUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_USER_ADMIN_PORT);
    cedarAdminPortUser.add(SystemComponent.SERVER_USER);
    cedarAdminPortUser.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_USER_STOP_PORT);
    cedarStopPortUser.add(SystemComponent.SERVER_USER);
    Set<SystemComponent> cedarServerHostUser = variableToComponent.get(CedarEnvironmentVariable.CEDAR_USER_SERVER_HOST);
    cedarServerHostUser.add(SystemComponent.SERVER_MESSAGING);
    cedarServerHostUser.add(SystemComponent.SERVER_OPENVIEW);
    cedarServerHostUser.add(SystemComponent.SERVER_RESOURCE);
    cedarServerHostUser.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortRepo = variableToComponent.get(CedarEnvironmentVariable.CEDAR_REPO_HTTP_PORT);
    cedarHttpPortRepo.add(SystemComponent.SERVER_REPO);
    Set<SystemComponent> cedarAdminPortRepo = variableToComponent.get(CedarEnvironmentVariable.CEDAR_REPO_ADMIN_PORT);
    cedarAdminPortRepo.add(SystemComponent.SERVER_REPO);
    cedarAdminPortRepo.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortRepo = variableToComponent.get(CedarEnvironmentVariable.CEDAR_REPO_STOP_PORT);
    cedarStopPortRepo.add(SystemComponent.SERVER_REPO);
    Set<SystemComponent> cedarServerHostRepo = variableToComponent.get(CedarEnvironmentVariable.CEDAR_REPO_SERVER_HOST);
    cedarServerHostRepo.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortResource = variableToComponent.get(CedarEnvironmentVariable.CEDAR_RESOURCE_HTTP_PORT);
    cedarHttpPortResource.add(SystemComponent.SERVER_RESOURCE);
    cedarHttpPortResource.add(SystemComponent.SERVER_BRIDGE);
    cedarHttpPortResource.add(SystemComponent.ADMIN_TOOL);
    cedarHttpPortResource.add(SystemComponent.CADSR_TOOL);
    Set<SystemComponent> cedarAdminPortResource = variableToComponent.get(CedarEnvironmentVariable.CEDAR_RESOURCE_ADMIN_PORT);
    cedarAdminPortResource.add(SystemComponent.SERVER_RESOURCE);
    cedarAdminPortResource.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortResource = variableToComponent.get(CedarEnvironmentVariable.CEDAR_RESOURCE_STOP_PORT);
    cedarStopPortResource.add(SystemComponent.SERVER_RESOURCE);
    Set<SystemComponent> cedarServerHostResource = variableToComponent.get(CedarEnvironmentVariable.CEDAR_RESOURCE_SERVER_HOST);
    cedarServerHostResource.add(SystemComponent.SERVER_BRIDGE);
    cedarServerHostResource.add(SystemComponent.SERVER_MONITOR);
    cedarServerHostResource.add(SystemComponent.KEYCLOAK_EVENT_LISTENER);
    cedarServerHostResource.add(SystemComponent.ADMIN_TOOL);

    Set<SystemComponent> cedarHttpPortSchema = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SCHEMA_HTTP_PORT);
    cedarHttpPortSchema.add(SystemComponent.SERVER_SCHEMA);
    Set<SystemComponent> cedarAdminPortSchema = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SCHEMA_ADMIN_PORT);
    cedarAdminPortSchema.add(SystemComponent.SERVER_SCHEMA);
    cedarAdminPortSchema.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortSchema = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SCHEMA_STOP_PORT);
    cedarStopPortSchema.add(SystemComponent.SERVER_SCHEMA);
    Set<SystemComponent> cedarServerHostSchema = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SCHEMA_SERVER_HOST);
    cedarServerHostSchema.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarPortArtifact = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ARTIFACT_HTTP_PORT);
    cedarPortArtifact.add(SystemComponent.SERVER_ARTIFACT);
    cedarPortArtifact.add(SystemComponent.SERVER_RESOURCE);
    cedarPortArtifact.add(SystemComponent.SERVER_WORKER);
    cedarPortArtifact.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> cedarAdminPortArtifact = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ARTIFACT_ADMIN_PORT);
    cedarAdminPortArtifact.add(SystemComponent.SERVER_ARTIFACT);
    cedarAdminPortArtifact.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortTemplate = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ARTIFACT_STOP_PORT);
    cedarStopPortTemplate.add(SystemComponent.SERVER_ARTIFACT);
    Set<SystemComponent> cedarServerHostArtifact = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ARTIFACT_SERVER_HOST);
    cedarServerHostArtifact.add(SystemComponent.SERVER_WORKER);
    cedarServerHostArtifact.add(SystemComponent.SERVER_RESOURCE);
    cedarServerHostArtifact.add(SystemComponent.SERVER_OPENVIEW);
    cedarServerHostArtifact.add(SystemComponent.SERVER_MONITOR);
    cedarServerHostArtifact.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> cedarHttpPortTerminology = variableToComponent.get(CedarEnvironmentVariable.CEDAR_TERMINOLOGY_HTTP_PORT);
    cedarHttpPortTerminology.add(SystemComponent.SERVER_TERMINOLOGY);
    Set<SystemComponent> cedarAdminPortTerminology = variableToComponent.get(CedarEnvironmentVariable.CEDAR_TERMINOLOGY_ADMIN_PORT);
    cedarAdminPortTerminology.add(SystemComponent.SERVER_TERMINOLOGY);
    cedarAdminPortTerminology.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortTerminology = variableToComponent.get(CedarEnvironmentVariable.CEDAR_TERMINOLOGY_STOP_PORT);
    cedarStopPortTerminology.add(SystemComponent.SERVER_TERMINOLOGY);
    Set<SystemComponent> cedarServerHostTerminology = variableToComponent.get(CedarEnvironmentVariable.CEDAR_TERMINOLOGY_SERVER_HOST);
    cedarServerHostTerminology.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortValuerecommender = variableToComponent.get(CedarEnvironmentVariable.CEDAR_VALUERECOMMENDER_HTTP_PORT);
    cedarHttpPortValuerecommender.add(SystemComponent.SERVER_VALUERECOMMENDER);
    cedarHttpPortValuerecommender.add(SystemComponent.SERVER_WORKER);
    Set<SystemComponent> cedarAdminPortValuerecommender = variableToComponent.get(CedarEnvironmentVariable.CEDAR_VALUERECOMMENDER_ADMIN_PORT);
    cedarAdminPortValuerecommender.add(SystemComponent.SERVER_VALUERECOMMENDER);
    cedarAdminPortValuerecommender.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortValuerecommender = variableToComponent.get(CedarEnvironmentVariable.CEDAR_VALUERECOMMENDER_STOP_PORT);
    cedarStopPortValuerecommender.add(SystemComponent.SERVER_VALUERECOMMENDER);
    Set<SystemComponent> cedarServerHostValuerecommender = variableToComponent.get(CedarEnvironmentVariable.CEDAR_VALUERECOMMENDER_SERVER_HOST);
    cedarServerHostValuerecommender.add(SystemComponent.SERVER_VALUERECOMMENDER);
    cedarServerHostValuerecommender.add(SystemComponent.SERVER_WORKER);
    cedarServerHostValuerecommender.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortSubmission = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SUBMISSION_HTTP_PORT);
    cedarHttpPortSubmission.add(SystemComponent.SERVER_SUBMISSION);
    Set<SystemComponent> cedarAdminPortSubmission = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SUBMISSION_ADMIN_PORT);
    cedarAdminPortSubmission.add(SystemComponent.SERVER_SUBMISSION);
    cedarAdminPortSubmission.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortSubmission = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SUBMISSION_STOP_PORT);
    cedarStopPortSubmission.add(SystemComponent.SERVER_SUBMISSION);
    Set<SystemComponent> cedarServerHostSubmission = variableToComponent.get(CedarEnvironmentVariable.CEDAR_SUBMISSION_SERVER_HOST);
    cedarServerHostSubmission.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortWorker = variableToComponent.get(CedarEnvironmentVariable.CEDAR_WORKER_HTTP_PORT);
    cedarHttpPortWorker.add(SystemComponent.SERVER_WORKER);
    Set<SystemComponent> cedarAdminPortWorker = variableToComponent.get(CedarEnvironmentVariable.CEDAR_WORKER_ADMIN_PORT);
    cedarAdminPortWorker.add(SystemComponent.SERVER_WORKER);
    cedarAdminPortWorker.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortWorker = variableToComponent.get(CedarEnvironmentVariable.CEDAR_WORKER_STOP_PORT);
    cedarStopPortWorker.add(SystemComponent.SERVER_WORKER);
    Set<SystemComponent> cedarServerHostWorker = variableToComponent.get(CedarEnvironmentVariable.CEDAR_WORKER_SERVER_HOST);
    cedarServerHostWorker.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarTestUser1Id = variableToComponent.get(CedarEnvironmentVariable.CEDAR_TEST_USER1_ID);
    cedarTestUser1Id.add(SystemComponent.SERVER_ARTIFACT);
    cedarTestUser1Id.add(SystemComponent.SERVER_TERMINOLOGY);
    cedarTestUser1Id.add(SystemComponent.SERVER_MESSAGING);

    Set<SystemComponent> cedarTestUser2Id = variableToComponent.get(CedarEnvironmentVariable.CEDAR_TEST_USER2_ID);
    cedarTestUser2Id.add(SystemComponent.SERVER_MESSAGING);

    Set<SystemComponent> cedarTrustedFolders = variableToComponent.get(CedarEnvironmentVariable.CEDAR_TRUSTED_FOLDERS);
    cedarTrustedFolders.add(SystemComponent.SERVER_WORKER);
    cedarTrustedFolders.add(SystemComponent.SERVER_RESOURCE);

    Set<SystemComponent> rorApiPrefix = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ROR_API_PREFIX);
    rorApiPrefix.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> orcidTokenPrefix = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ORCID_TOKEN_PREFIX);
    orcidTokenPrefix.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> orcidApiPrefix = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ORCID_API_PREFIX);
    orcidApiPrefix.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> orcidApiClientId = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ORCID_API_CLIENT_ID);
    orcidApiClientId.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> orcidApiClientSecret = variableToComponent.get(CedarEnvironmentVariable.CEDAR_ORCID_API_CLIENT_SECRET);
    orcidApiClientSecret.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> compToxApiPrefix = variableToComponent.get(CedarEnvironmentVariable.CEDAR_COMP_TOX_API_PREFIX);
    compToxApiPrefix.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> compToxApiKey = variableToComponent.get(CedarEnvironmentVariable.CEDAR_COMP_TOX_API_KEY);
    compToxApiKey.add(SystemComponent.SERVER_BRIDGE);

    Set<SystemComponent> cedarHttpPortOpenview = variableToComponent.get(CedarEnvironmentVariable.CEDAR_OPENVIEW_HTTP_PORT);
    cedarHttpPortOpenview.add(SystemComponent.SERVER_OPENVIEW);
    Set<SystemComponent> cedarAdminPortOpenview = variableToComponent.get(CedarEnvironmentVariable.CEDAR_OPENVIEW_ADMIN_PORT);
    cedarAdminPortOpenview.add(SystemComponent.SERVER_OPENVIEW);
    cedarAdminPortOpenview.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortOpenview = variableToComponent.get(CedarEnvironmentVariable.CEDAR_OPENVIEW_STOP_PORT);
    cedarStopPortOpenview.add(SystemComponent.SERVER_OPENVIEW);
    Set<SystemComponent> cedarServerHostOpenview = variableToComponent.get(CedarEnvironmentVariable.CEDAR_OPENVIEW_SERVER_HOST);
    cedarServerHostOpenview.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortMonitor = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONITOR_HTTP_PORT);
    cedarHttpPortMonitor.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarAdminPortMonitor = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONITOR_ADMIN_PORT);
    cedarAdminPortMonitor.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortMonitor = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONITOR_STOP_PORT);
    cedarStopPortMonitor.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarServerHostMonitor = variableToComponent.get(CedarEnvironmentVariable.CEDAR_MONITOR_SERVER_HOST);
    cedarServerHostMonitor.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortImpex = variableToComponent.get(CedarEnvironmentVariable.CEDAR_IMPEX_HTTP_PORT);
    cedarHttpPortImpex.add(SystemComponent.SERVER_IMPEX);
    Set<SystemComponent> cedarAdminPortImpex = variableToComponent.get(CedarEnvironmentVariable.CEDAR_IMPEX_ADMIN_PORT);
    cedarAdminPortImpex.add(SystemComponent.SERVER_IMPEX);
    cedarAdminPortImpex.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortImpex = variableToComponent.get(CedarEnvironmentVariable.CEDAR_IMPEX_STOP_PORT);
    cedarStopPortImpex.add(SystemComponent.SERVER_IMPEX);
    Set<SystemComponent> cedarServerHostImpex = variableToComponent.get(CedarEnvironmentVariable.CEDAR_IMPEX_SERVER_HOST);
    cedarServerHostImpex.add(SystemComponent.SERVER_MONITOR);

    Set<SystemComponent> cedarHttpPortBridge = variableToComponent.get(CedarEnvironmentVariable.CEDAR_BRIDGE_HTTP_PORT);
    cedarHttpPortBridge.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> cedarAdminPortBridge = variableToComponent.get(CedarEnvironmentVariable.CEDAR_BRIDGE_ADMIN_PORT);
    cedarAdminPortBridge.add(SystemComponent.SERVER_BRIDGE);
    cedarAdminPortBridge.add(SystemComponent.SERVER_MONITOR);
    Set<SystemComponent> cedarStopPortBridge = variableToComponent.get(CedarEnvironmentVariable.CEDAR_BRIDGE_STOP_PORT);
    cedarStopPortBridge.add(SystemComponent.SERVER_BRIDGE);
    Set<SystemComponent> cedarServerHostBridge = variableToComponent.get(CedarEnvironmentVariable.CEDAR_BRIDGE_SERVER_HOST);
    cedarServerHostBridge.add(SystemComponent.SERVER_MONITOR);

    // Compute the reverse map
    componentToVariable = new LinkedHashMap<>();
    for (SystemComponent component : SystemComponent.values()) {
      Set<CedarEnvironmentVariable> variables = new LinkedHashSet<>();
      componentToVariable.put(component, variables);
      for (CedarEnvironmentVariable variable : CedarEnvironmentVariable.values()) {
        Set<SystemComponent> componentsForVariable = variableToComponent.get(variable);
        if (componentsForVariable.contains(component)) {
          variables.add(variable);
        }
      }
    }
  }

  public static Set<CedarEnvironmentVariable> getVariableNamesFor(SystemComponent useCase) {
    return componentToVariable.get(useCase);
  }
}
