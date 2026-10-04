package org.metadatacenter.server.jsonld;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;
import org.metadatacenter.config.LinkedDataConfig;
import org.metadatacenter.model.CedarResourceType;
import static org.junit.jupiter.api.Assertions.*;

class ResourceAddressingTest {
  private LinkedDataUtil identifiers() throws Exception {
    return new LinkedDataUtil(new ObjectMapper().readValue(
        "{\"base\":\"https://repo.metadatacenter.orgx/\",\"usersBase\":\"https://user.metadatacenter.orgx/users/\"}",
        LinkedDataConfig.class));
  }

  @Test void allFiveTypesResolveToTheSameStoredIdentity() throws Exception {
    var ids = identifiers();
    for (var type : new CedarResourceType[]{CedarResourceType.FOLDER, CedarResourceType.TEMPLATE,
        CedarResourceType.ELEMENT, CedarResourceType.FIELD, CedarResourceType.INSTANCE}) {
      String selector = type.getPrefix() + "/12345678-abcd-1234-abcd-123456789012";
      String iri = "https://repo.metadatacenter.orgx/" + selector;
      assertEquals(iri, ids.resolveResourceId(selector));
      assertEquals(iri, ids.resolveResourceId(type, selector));
      assertEquals(iri, ids.resolveResourceId(type, iri));
      assertEquals(iri, ids.resolveResourceId(type, "12345678-abcd-1234-abcd-123456789012"));
      assertEquals(selector, ids.resourceRequestId(iri));
      assertEquals("12345678-abcd-1234-abcd-123456789012", ids.resourcePathId(type, iri));
    }
  }

  @Test void legacyHostsAreNeverSilentlyRetargeted() throws Exception {
    var ids = identifiers();
    for (String host : new String[]{"repo.metadatacenter.net", "repo.metadatacenter.org", "repo.example"}) {
      String iri = "https://" + host + "/templates/old-id";
      assertEquals(iri, ids.resolveResourceId(iri));
      assertEquals(iri, ids.resourceRequestId(iri));
      assertEquals(iri, ids.resourcePathId(CedarResourceType.TEMPLATE, iri));
    }
  }

  @Test void otherResourceFamiliesRetainTheirExistingSelectors() throws Exception {
    var ids = identifiers();
    for (String type : new String[]{"users", "groups", "categories", "template-element-instances"}) {
      String iri = "https://repo.metadatacenter.orgx/" + type + "/12345678-abcd-1234-abcd-123456789012";
      assertEquals(iri, ids.resourceRequestId(iri));
    }
  }

  @Test void rejectsTypeConfusionAndPathSyntax() throws Exception {
    var ids = identifiers();
    for (String bad : new String[]{"folders/id", "../id", "id/child", "id?x=1", "id#x", "%2Fid",
        "templates/id/child", "", " "}) {
      assertThrows(BadRequestException.class, () -> ids.resolveResourceId(CedarResourceType.TEMPLATE, bad), bad);
    }
    for (String bad : new String[]{"id", "users/id", "categories/id", "template-element-instances/id"}) {
      assertThrows(BadRequestException.class, () -> ids.resolveResourceId(bad), bad);
    }
  }
}
