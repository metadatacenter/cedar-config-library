package org.metadatacenter.config;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public class CedarEnvironmentVariableSubstitutor extends StringSubstitutor {

  public CedarEnvironmentVariableSubstitutor(Map<String, String> environment) {
    super(new CedarEnvironmentVariableLookup(environment, false));
    this.setEnableSubstitutionInVariables(false);
  }
}
