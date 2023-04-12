package org.metadatacenter.config;

import io.dropwizard.configuration.ConfigurationSourceProvider;

import java.io.IOException;
import java.io.InputStream;

public class ClasspathConfigurationSourceProvider implements ConfigurationSourceProvider {
  @Override
  public InputStream open(String name) throws IOException {
    return this.getClass().getClassLoader().getResourceAsStream(name);
  }
}
