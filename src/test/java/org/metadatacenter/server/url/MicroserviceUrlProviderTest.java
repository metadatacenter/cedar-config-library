package org.metadatacenter.server.url;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.config.LinkedDataConfig;
import org.metadatacenter.config.ServersConfig;
import org.metadatacenter.id.CedarArtifactId;
import org.metadatacenter.id.CedarTemplateId;
import org.metadatacenter.model.CedarResourceType;
import org.metadatacenter.server.jsonld.LinkedDataUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Every server base in cedar-main.yml ends in a slash, so a provider appends a relative path to it. A
 * path that starts with a slash of its own yields an empty segment, which Jetty refuses with a 400
 * before it routes the request.
 */
class MicroserviceUrlProviderTest {

  private static final String UUID = "12345678-abcd-1234-abcd-123456789012";

  private static final Map<Class<?>, List<?>> SAMPLES = Map.of(
      CedarResourceType.class, List.of(CedarResourceType.values()),
      String.class, List.of(UUID),
      Optional.class, List.of(Optional.empty(), Optional.of("yaml")),
      CedarArtifactId.class, List.of(
          CedarArtifactId.build(UUID, CedarResourceType.FIELD),
          CedarArtifactId.build(UUID, CedarResourceType.ELEMENT),
          CedarArtifactId.build(UUID, CedarResourceType.TEMPLATE),
          CedarArtifactId.build(UUID, CedarResourceType.INSTANCE)),
      CedarTemplateId.class, List.of(CedarTemplateId.build(UUID)));

  private static MicroserviceUrlUtil urls() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ServersConfig servers = mapper.readValue("""
        {
          "artifact": {"base": "http://127.0.0.1:9001/"},
          "messaging": {"base": "http://127.0.0.1:9012/"},
          "resource": {"base": "http://127.0.0.1:9007/"},
          "user": {"base": "http://127.0.0.1:9005/", "usersBase": "http://127.0.0.1:9005/users/"},
          "valuerecommender": {"base": "http://127.0.0.1:9006/"}
        }""", ServersConfig.class);
    LinkedDataUtil identifiers = new LinkedDataUtil(mapper.readValue(
        "{\"base\":\"https://repo.metadatacenter.orgx/\",\"usersBase\":\"https://user.metadatacenter.orgx/users/\"}",
        LinkedDataConfig.class));
    return new MicroserviceUrlUtil(servers, identifiers);
  }

  @Test
  void theDoiWriteBackAddressesTheResourceServerCommand() throws Exception {
    assertEquals("http://127.0.0.1:9007/command/annotations/doi", urls().getResource().getCommandDOIUpdate());
  }

  /**
   * The methods are found by reflection, so a URL that a provider gains later is checked without
   * anyone adding it here. A parameter type with no sample fails the test rather than skipping the
   * method.
   */
  @Test
  void noUrlHasAnEmptyPathSegment() throws Exception {
    MicroserviceUrlUtil urls = urls();
    List<String> built = new ArrayList<>();
    for (Method getter : MicroserviceUrlUtil.class.getMethods()) {
      if (!MicroserviceUrlProvider.class.isAssignableFrom(getter.getReturnType())) {
        continue;
      }
      Object provider = getter.invoke(urls);
      for (Method method : getter.getReturnType().getMethods()) {
        if (method.getReturnType() == String.class && method.getDeclaringClass() != Object.class
            && !Modifier.isStatic(method.getModifiers())) {
          for (Object[] arguments : argumentLists(method)) {
            built.add((String) method.invoke(provider, arguments));
          }
        }
      }
    }
    assertFalse(built.isEmpty());
    for (String url : built) {
      assertFalse(URI.create(url).getRawPath().contains("//"), url);
    }
  }

  private static List<Object[]> argumentLists(Method method) {
    List<Object[]> lists = Collections.singletonList(new Object[0]);
    for (Class<?> type : method.getParameterTypes()) {
      List<?> samples = SAMPLES.get(type);
      assertNotNull(samples, () -> method + " takes a " + type.getSimpleName() + ", for which there is no sample");
      List<Object[]> extended = new ArrayList<>();
      for (Object[] list : lists) {
        for (Object sample : samples) {
          Object[] next = Arrays.copyOf(list, list.length + 1);
          next[list.length] = sample;
          extended.add(next);
        }
      }
      lists = extended;
    }
    return lists;
  }
}
