package org.metadatacenter.server.jsonld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.config.LinkedDataConfig;
import org.metadatacenter.model.CedarResourceType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The identifiers the server assigns to the element occurrences inside an instance.
 *
 * <p>An occurrence exists in a document before anything can name it: one element, filled twice, is two
 * occurrences, and neither can be identified until the instance is uploaded. So the server assigns
 * them, and a client says which ones it is asking for. Two spellings mean "not yet": the older shape
 * leaves the key out, and the shape both model libraries now write states {@code "@id": null}.
 *
 * <p>Only the absent key was answered here, so a null counted as an identifier already in hand and the
 * occurrence was passed over — the null reaching storage, where nothing would fill it later.
 */
public class LinkedDataUtilElementInstanceIdTest {

  private static final String BASE = "https://repo.metadatacenter.orgx/";
  private static final String OCCURRENCE_PREFIX = BASE + "template-element-instances/";
  private static final String ASSIGNED = OCCURRENCE_PREFIX + "11111111-2222-3333-4444-555555555555";

  private ObjectMapper mapper;
  private LinkedDataUtil linkedDataUtil;

  @BeforeEach public void setUp() throws Exception {
    mapper = new ObjectMapper();
    LinkedDataConfig config = mapper.readValue(
      "{\"base\":\"" + BASE + "\",\"usersBase\":\"" + BASE + "users/\"}", LinkedDataConfig.class);
    linkedDataUtil = new LinkedDataUtil(config);
  }

  /** An instance holding one element occurrence, whose `@id` the caller decides. */
  private ObjectNode instanceWithOccurrence(String occurrenceId, boolean stateTheKey) throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"schema:name\":\"n\",\"An Element\":{\"@context\":{},\"A Field\":{\"@value\":null}}}");
    ObjectNode occurrence = (ObjectNode) instance.get("An Element");
    if (stateTheKey) {
      if (occurrenceId == null) occurrence.putNull("@id");
      else occurrence.put("@id", occurrenceId);
    }
    return instance;
  }

  private ObjectNode fill(ObjectNode instance) {
    linkedDataUtil.addElementInstanceIds(instance, CedarResourceType.INSTANCE);
    return (ObjectNode) instance.get("An Element");
  }

  @Test public void anOccurrenceStatingNullIsAssignedOne() throws Exception {
    ObjectNode occurrence = fill(instanceWithOccurrence(null, true));

    assertTrue(occurrence.get("@id").isTextual(), "a null identifier is what a client writes to ask for one");
    assertTrue(occurrence.get("@id").asText().startsWith(OCCURRENCE_PREFIX),
      "and the one assigned is the repository's: " + occurrence.get("@id").asText());
  }

  @Test public void anOccurrenceOmittingTheKeyIsAssignedOne() throws Exception {
    ObjectNode occurrence = fill(instanceWithOccurrence(null, false));

    assertTrue(occurrence.get("@id").asText().startsWith(OCCURRENCE_PREFIX));
  }

  @Test public void anOccurrenceThatAlreadyHasOneKeepsIt() throws Exception {
    ObjectNode occurrence = fill(instanceWithOccurrence(ASSIGNED, true));

    assertEquals(ASSIGNED, occurrence.get("@id").asText(), "an assigned identifier is never reassigned");
  }

  @Test public void anEmptyStringIsLeftAsItIs() throws Exception {
    ObjectNode occurrence = fill(instanceWithOccurrence("", true));

    assertEquals("", occurrence.get("@id").asText(),
      "an empty string is not an absent identifier, and minting over it would hide whoever wrote it");
  }

  @Test public void everyOccurrenceInAMultiInstanceElementIsAnswered() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"An Element\":[{\"@context\":{},\"@id\":null},{\"@context\":{}},"
        + "{\"@context\":{},\"@id\":\"" + ASSIGNED + "\"}]}");

    linkedDataUtil.addElementInstanceIds(instance, CedarResourceType.INSTANCE);

    assertTrue(instance.get("An Element").get(0).get("@id").asText().startsWith(OCCURRENCE_PREFIX));
    assertTrue(instance.get("An Element").get(1).get("@id").asText().startsWith(OCCURRENCE_PREFIX));
    assertEquals(ASSIGNED, instance.get("An Element").get(2).get("@id").asText());
  }

  @Test public void anOccurrenceNestedInAnotherIsAnswered() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"Outer\":{\"@context\":{},\"@id\":null,\"Inner\":{\"@context\":{},\"@id\":null}}}");

    linkedDataUtil.addElementInstanceIds(instance, CedarResourceType.INSTANCE);

    assertTrue(instance.get("Outer").get("@id").asText().startsWith(OCCURRENCE_PREFIX));
    assertTrue(instance.get("Outer").get("Inner").get("@id").asText().startsWith(OCCURRENCE_PREFIX),
      "a nested occurrence is an occurrence");
  }

  @Test public void theContextIsNotAnOccurrence() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree("{\"@context\":{\"An Element\":\"" + BASE + "p/1\"}}");

    linkedDataUtil.addElementInstanceIds(instance, CedarResourceType.INSTANCE);

    assertFalse(instance.get("@context").has("@id"), "the context is a mapping, not a node to identify");
  }
}
