package org.metadatacenter.config;

public class ImmPortConfig {

  private RESTEndpointAuthentication authentication;
  private SimpleRESTEndpoint tokenEndpoint;
  private SimpleRESTEndpoint workspaceEndpoint;
  private SimpleRESTEndpoint submissionEndpoint;
  private SimpleRESTEndpoint statusEndpoint;

  public RESTEndpointAuthentication getAuthentication() {
    return authentication;
  }

  public SimpleRESTEndpoint getTokenEndpoint() {
    return tokenEndpoint;
  }

  public SimpleRESTEndpoint getWorkspaceEndpoint() {
    return workspaceEndpoint;
  }

  public SimpleRESTEndpoint getSubmissionEndpoint() {
    return submissionEndpoint;
  }

  public SimpleRESTEndpoint getStatusEndpoint() {
    return statusEndpoint;
  }
}
