package org.metadatacenter.server.jsonld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.config.LinkedDataConfig;
import org.metadatacenter.model.CedarResourceType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The IRIs an instance can only get from the server, and the two shapes that ask for them.
 *
 * <p>Both exist in a document before anything can name them. An element occurrence is one element
 * filled once, so filling it twice makes two, and neither is identifiable until the instance is
 * uploaded. An attribute is named by the user while filling a form, so nothing could have minted a
 * property IRI for it earlier — the name did not exist. The server assigns both, and a client says
 * which it is asking for: an occurrence writes {@code "@id": null} or leaves the key out, and an
 * attribute simply carries no {@code @context} term.
 *
 * <p>Only the absent key was answered for an occurrence, so a null counted as an identifier already in
 * hand and the null reached storage with nothing to fill it later. Attributes were not answered at
 * all.
 *
 * <p>What both share: an IRI already there is left alone, whoever assigned it, because an identifier
 * is worth having only if it is stable.
 */
public class LinkedDataUtilServerAssignedIrisTest {

  private static final String BASE = "https://repo.metadatacenter.orgx/";
  private static final String OCCURRENCE_PREFIX = BASE + "template-element-instances/";
  private static final String ASSIGNED = OCCURRENCE_PREFIX + "11111111-2222-3333-4444-555555555555";
  private static final String PROPERTY_PREFIX = "https://schema.metadatacenter.org/properties/";

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

  // ---- The property IRI an attribute is given ----

  @Test public void anAttributeWithNoTermIsGivenOne() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"Sizes\":[\"Height\",\"Width\"],\"Height\":{\"@value\":\"2\"},\"Width\":{\"@value\":\"3\"}}");

    linkedDataUtil.addAttributeValuePropertyIris(instance, CedarResourceType.INSTANCE);

    ObjectNode context = (ObjectNode) instance.get("@context");
    assertTrue(context.get("Height").asText().startsWith(PROPERTY_PREFIX), "an attribute is a property, and gets an IRI");
    assertTrue(context.get("Width").asText().startsWith(PROPERTY_PREFIX));
    assertNotEquals(context.get("Height").asText(), context.get("Width").asText(),
      "two attributes are two properties");
  }

  @Test public void anAttributeThatAlreadyHasOneKeepsIt() throws Exception {
    String assigned = PROPERTY_PREFIX + "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{\"Height\":\"" + assigned + "\"},\"Sizes\":[\"Height\"],\"Height\":{\"@value\":\"2\"}}");

    linkedDataUtil.addAttributeValuePropertyIris(instance, CedarResourceType.INSTANCE);

    assertEquals(assigned, instance.get("@context").get("Height").asText(),
      "an assigned IRI is never reassigned, whoever assigned it");
  }

  @Test public void anAttributeInsideAnElementIsNamedInThatElementsContext() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"An Element\":{\"@context\":{},\"Sizes\":[\"Height\"],\"Height\":{\"@value\":\"2\"}}}");

    linkedDataUtil.addAttributeValuePropertyIris(instance, CedarResourceType.INSTANCE);

    assertTrue(((ObjectNode) instance.get("An Element").get("@context")).get("Height").asText().startsWith(PROPERTY_PREFIX),
      "the term belongs to the node holding the field");
    assertFalse(instance.get("@context").has("Height"), "and not to the root, which does not hold it");
  }

  @Test public void anAttributeWithNoNameIsNotNamed() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree("{\"@context\":{},\"Sizes\":[\"\"]}");

    linkedDataUtil.addAttributeValuePropertyIris(instance, CedarResourceType.INSTANCE);

    assertFalse(instance.get("@context").has(""),
      "an attribute with no name is a defect in whatever wrote it, not something to mint for");
  }

  @Test public void aListOfValuesIsNotAListOfAttributeNames() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"A Multi Field\":[{\"@value\":\"one\"},{\"@value\":\"two\"}]}");

    linkedDataUtil.addAttributeValuePropertyIris(instance, CedarResourceType.INSTANCE);

    assertEquals(0, instance.get("@context").size(), "only a list of names names attributes");
  }

  // ---- The property IRI a template child is given ----

  /** A template declaring one child of the given input type, with the context mapping the caller decides. */
  private ObjectNode templateWithChild(String inputType, boolean mapped) throws Exception {
    ObjectNode template = (ObjectNode) mapper.readTree(
      "{\"_ui\":{\"order\":[\"A Field\"]},\"properties\":{\"@context\":{\"properties\":{},\"required\":[]},"
        + "\"A Field\":{\"_ui\":{\"inputType\":\"" + inputType + "\"}}}}");
    if (mapped) {
      ObjectNode contextProperties = (ObjectNode) template.get("properties").get("@context").get("properties");
      contextProperties.putObject("A Field").putArray("enum").add("http://example.org/chosen-by-the-author");
    }
    return template;
  }

  private ObjectNode contextProperties(ObjectNode template) {
    return (ObjectNode) template.get("properties").get("@context").get("properties");
  }

  @Test public void aChildWithNoMappingIsGivenOne() throws Exception {
    ObjectNode template = templateWithChild("textfield", false);

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    String iri = contextProperties(template).get("A Field").get("enum").get(0).asText();
    assertTrue(iri.startsWith(PROPERTY_PREFIX), "a child's name is mapped to a property the repository assigns");
    assertTrue(template.get("properties").get("@context").get("required").toString().contains("A Field"),
      "and the instance is required to carry the mapping");
  }

  @Test public void aChildThatAlreadyHasOneKeepsIt() throws Exception {
    ObjectNode template = templateWithChild("textfield", true);

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    assertEquals("http://example.org/chosen-by-the-author",
      contextProperties(template).get("A Field").get("enum").get(0).asText(),
      "an author's own IRI is the point of the key, and is never replaced");
  }

  @Test public void aStaticFieldIsNotMapped() throws Exception {
    ObjectNode template = templateWithChild("section-break", false);

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    assertFalse(contextProperties(template).has("A Field"),
      "a static field displays something and holds nothing, so no property names its value");
  }

  @Test public void anAttributeValueFieldIsNotMappedHere() throws Exception {
    ObjectNode template = templateWithChild("attribute-value", false);

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    assertFalse(contextProperties(template).has("A Field"),
      "its attributes are named in the instance, where the user names them");
  }

  @Test public void aChildOfAChildElementIsMappedToo() throws Exception {
    ObjectNode template = (ObjectNode) mapper.readTree(
      "{\"_ui\":{\"order\":[\"An Element\"]},\"properties\":{\"@context\":{\"properties\":{},\"required\":[]},"
        + "\"An Element\":{\"_ui\":{\"order\":[\"Inner\"]},\"properties\":{\"@context\":{\"properties\":{},\"required\":[]},"
        + "\"Inner\":{\"_ui\":{\"inputType\":\"textfield\"}}}}}}");

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    ObjectNode inner = (ObjectNode) template.get("properties").get("An Element").get("properties").get("@context").get("properties");
    assertTrue(inner.get("Inner").get("enum").get(0).asText().startsWith(PROPERTY_PREFIX),
      "an element maps its own children");
  }

  @Test public void aMultiInstanceChildIsReadThroughItsItems() throws Exception {
    ObjectNode template = (ObjectNode) mapper.readTree(
      "{\"_ui\":{\"order\":[\"Many\"]},\"properties\":{\"@context\":{\"properties\":{},\"required\":[]},"
        + "\"Many\":{\"type\":\"array\",\"items\":{\"_ui\":{\"inputType\":\"section-break\"}}}}}");

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    assertFalse(contextProperties(template).has("Many"),
      "what the array holds decides, so a repeated static field is still static");
  }

  @Test public void anInstanceIsNotAContainerOfChildren() throws Exception {
    ObjectNode template = templateWithChild("textfield", false);

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.INSTANCE);

    assertFalse(contextProperties(template).has("A Field"), "only a template or an element maps children");
  }
}
