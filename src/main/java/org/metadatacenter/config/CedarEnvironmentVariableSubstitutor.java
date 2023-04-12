package org.metadatacenter.config;

import org.apache.commons.text.StrSubstitutor;

import java.util.Map;

public class CedarEnvironmentVariableSubstitutor extends StrSubstitutor {

  public CedarEnvironmentVariableSubstitutor(Map<String, String> environment) {
    super(new CedarEnvironmentVariableLookup(environment, false));
    this.setEnableSubstitutionInVariables(false);
  }
}
