package org.metadatacenter.server.jsonld;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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

  @Test public void anAttributeWithNoNameIsRemovedFromTheStoredShape() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree("{\"@context\":{},\"Sizes\":[\"\"]}");

    linkedDataUtil.addAttributeValuePropertyIris(instance, CedarResourceType.INSTANCE);

    assertFalse(instance.get("@context").has(""),
      "an attribute with no name is a defect in whatever wrote it, not something to mint for");
    assertTrue(instance.get("Sizes").isEmpty(),
      "an empty UI slot is draft state, not an attribute the repository can store");
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
        + "\"A Field\":{\"$schema\":\"http://json-schema.org/draft-04/schema#\","
        + "\"_ui\":{\"inputType\":\"" + inputType + "\"}}}}");
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

  @Test public void aMappedChildIsRequiredInTheInstanceContextExactlyOnce() throws Exception {
    ObjectNode template = templateWithChild("textfield", true);

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);
    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    JsonNode required = template.get("properties").get("@context").get("required");
    assertEquals(1, required.size(), "normalization is idempotent");
    assertEquals("A Field", required.get(0).asText(),
      "a supplied mapping is still part of the context every instance must carry");
  }

  @Test public void aMissingContextRequiredArrayIsCreated() throws Exception {
    ObjectNode template = templateWithChild("textfield", true);
    ((ObjectNode) template.get("properties").get("@context")).remove("required");

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    JsonNode required = template.get("properties").get("@context").get("required");
    assertEquals(1, required.size());
    assertEquals("A Field", required.get(0).asText());
  }

  @Test public void aPropertyOmittedFromUiOrderIsStillMapped() throws Exception {
    ObjectNode template = templateWithChild("textfield", false);
    ((com.fasterxml.jackson.databind.node.ArrayNode) template.get("_ui").get("order")).removeAll();

    linkedDataUtil.addChildPropertyIris(template, CedarResourceType.TEMPLATE);

    assertTrue(contextProperties(template).get("A Field").get("enum").get(0).asText().startsWith(PROPERTY_PREFIX),
      "properties is the schema structure; _ui.order is only its presentation index");
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

  // ---- Compatibility repairs for production artifacts ----

  @Test public void inheritedMissingChildSchemasAreRestoredRecursively() throws Exception {
    ObjectNode stored = (ObjectNode) mapper.readTree("""
      {
        "_ui": { "order": ["An Element", "Many"] },
        "properties": {
          "@context": { "properties": {}, "required": [] },
          "An Element": {
            "_ui": { "order": ["Inner"] },
            "properties": {
              "@context": { "properties": {}, "required": [] },
              "Inner": { "_ui": { "inputType": "textfield" } }
            }
          },
          "Many": {
            "type": "array",
            "items": { "_ui": { "inputType": "textfield" } }
          }
        }
      }
      """);
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);

    assertEquals("http://json-schema.org/draft-04/schema#",
      submitted.at("/properties/An Element/$schema").asText());
    assertEquals("http://json-schema.org/draft-04/schema#",
      submitted.at("/properties/An Element/properties/Inner/$schema").asText());
    assertEquals("http://json-schema.org/draft-04/schema#",
      submitted.at("/properties/Many/items/$schema").asText());
    assertEquals(3, repairs.size());
  }

  @Test public void newlyIntroducedMissingChildSchemaIsLeftForValidation() throws Exception {
    ObjectNode stored = templateWithChild("textfield", true);
    ObjectNode submitted = stored.deepCopy();
    ((ObjectNode) submitted.at("/properties/A Field")).remove("$schema");

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);

    assertTrue(repairs.isEmpty());
    assertFalse(submitted.at("/properties/A Field").has("$schema"),
      "validation must see and reject a child declaration removed by this request");
  }

  @Test public void explicitBadInheritedChildSchemaIsLeftForValidation() throws Exception {
    ObjectNode stored = templateWithChild("textfield", true);
    ((ObjectNode) stored.at("/properties/A Field")).put("$schema", "not-a-schema");
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);

    assertTrue(repairs.isEmpty());
    assertEquals("not-a-schema", submitted.at("/properties/A Field/$schema").asText());
  }

  @Test public void aMissingRootSchemaIsNeverRepaired() throws Exception {
    ObjectNode stored = templateWithChild("textfield", true);
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);

    assertTrue(repairs.isEmpty());
    assertFalse(submitted.has("$schema"));
  }

  @Test public void anInheritedUnusableChildMappingIsReminted() throws Exception {
    ObjectNode stored = templateWithChild("textfield", false);
    contextProperties(stored).putObject("A Field").putArray("enum").add("");
    ((ObjectNode) stored.get("properties").get("@context")).putArray("required")
      .add("A Field").add("A Field");
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);
    linkedDataUtil.addChildPropertyIris(submitted, CedarResourceType.TEMPLATE);

    String repaired = contextProperties(submitted).get("A Field").get("enum").get(0).asText();
    assertEquals(1, repairs.size());
    assertTrue(repaired.startsWith(PROPERTY_PREFIX), "the inherited bad mapping is replaced server-side");
    assertEquals(1, submitted.get("properties").get("@context").get("required").size(),
      "reminting restores the required entry once");
  }

  @Test public void aNewlyIntroducedUnusableChildMappingIsNotSilentlyRepaired() throws Exception {
    ObjectNode stored = templateWithChild("textfield", true);
    ObjectNode submitted = stored.deepCopy();
    contextProperties(submitted).putObject("A Field").putArray("enum").add("");

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);
    linkedDataUtil.addChildPropertyIris(submitted, CedarResourceType.TEMPLATE);

    assertTrue(repairs.isEmpty());
    assertEquals("", contextProperties(submitted).get("A Field").get("enum").get(0).asText(),
      "validation must see and reject a defect introduced by this request");
  }

  @Test public void inheritedEmptyDerivedFromIsRemovedRecursively() throws Exception {
    ObjectNode stored = (ObjectNode) mapper.readTree(
      "{\"pav:derivedFrom\":\"\",\"properties\":{\"A Field\":{\"pav:derivedFrom\":\"\"}}}");
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);

    assertFalse(submitted.has("pav:derivedFrom"));
    assertFalse(submitted.path("properties").path("A Field").has("pav:derivedFrom"));
    assertEquals(2, repairs.size());
  }

  @Test public void newlyIntroducedEmptyDerivedFromIsLeftForValidation() throws Exception {
    ObjectNode stored = (ObjectNode) mapper.readTree("{\"properties\":{}}");
    ObjectNode submitted = stored.deepCopy();
    submitted.put("pav:derivedFrom", "");

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);

    assertTrue(repairs.isEmpty());
    assertEquals("", submitted.path("pav:derivedFrom").asText());
  }

  @Test public void absoluteDerivedFromIsPreserved() throws Exception {
    String source = "https://repo.metadatacenter.org/templates/source";
    ObjectNode stored = (ObjectNode) mapper.readTree("{\"pav:derivedFrom\":\"" + source + "\"}");
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.TEMPLATE);

    assertTrue(repairs.isEmpty());
    assertEquals(source, submitted.path("pav:derivedFrom").asText());
  }

  @Test public void anInheritedUnusableOccurrenceIdIsReminted() throws Exception {
    ObjectNode stored = instanceWithOccurrence("", true);
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.INSTANCE);
    linkedDataUtil.addElementInstanceIds(submitted, CedarResourceType.INSTANCE);

    assertEquals(1, repairs.size());
    assertTrue(submitted.get("An Element").get("@id").asText().startsWith(OCCURRENCE_PREFIX));
  }

  @Test public void aNewlyIntroducedUnusableOccurrenceIdIsNotSilentlyRepaired() throws Exception {
    ObjectNode stored = instanceWithOccurrence(ASSIGNED, true);
    ObjectNode submitted = stored.deepCopy();
    ((ObjectNode) submitted.get("An Element")).put("@id", "");

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, null, CedarResourceType.INSTANCE);
    linkedDataUtil.addElementInstanceIds(submitted, CedarResourceType.INSTANCE);

    assertTrue(repairs.isEmpty());
    assertEquals("", submitted.get("An Element").get("@id").asText(),
      "validation must see and reject a defect introduced by this request");
  }

  private ObjectNode templateWithAttributeGroup() throws Exception {
    return (ObjectNode) mapper.readTree(
      "{\"_ui\":{\"order\":[\"Attributes\",\"Name\"]},\"properties\":{" +
        "\"Attributes\":{\"_ui\":{\"inputType\":\"attribute-value\"}},"
        + "\"Name\":{\"_ui\":{\"inputType\":\"textfield\"}}}}" );
  }

  @Test public void inheritedUnsafeAttributeNamesAreRemoved() throws Exception {
    ObjectNode schema = templateWithAttributeGroup();
    ObjectNode stored = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"Attributes\":[\"\",\"@context\",\"Name\",\"dup\",\"dup\"]}");
    ObjectNode submitted = stored.deepCopy();

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, schema, CedarResourceType.INSTANCE);

    assertEquals("[\"dup\"]", submitted.get("Attributes").toString());
    assertEquals(4, repairs.size(), "blank, reserved, structural collision, and duplicate are repaired");
  }

  @Test public void anInheritedUnsafeAttributeNameIsStillRemovedWhenTheGroupWasEdited() throws Exception {
    ObjectNode schema = templateWithAttributeGroup();
    ObjectNode stored = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"Attributes\":[\"@context\",\"safe\"]}");
    ObjectNode submitted = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"Attributes\":[\"new\",\"@context\",\"safe\"]}");

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, schema, CedarResourceType.INSTANCE);

    assertEquals("[\"new\",\"safe\"]", submitted.get("Attributes").toString());
    assertEquals(1, repairs.size(), "editing the group must not strand an old production defect");
  }

  @Test public void newlyIntroducedUnsafeAttributeNamesAreNotSilentlyRepaired() throws Exception {
    ObjectNode schema = templateWithAttributeGroup();
    ObjectNode stored = (ObjectNode) mapper.readTree("{\"@context\":{},\"Attributes\":[\"safe\"]}");
    ObjectNode submitted = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"Attributes\":[\"@context\",\"Name\",\"dup\",\"dup\"]}");

    java.util.List<LinkedDataUtil.LegacyArtifactRepair> repairs = linkedDataUtil.repairInheritedDefects(
      submitted, stored, schema, CedarResourceType.INSTANCE);

    assertTrue(repairs.isEmpty());
    assertEquals("[\"@context\",\"Name\",\"dup\",\"dup\"]", submitted.get("Attributes").toString(),
      "validation must see and reject names introduced by this request");
  }

  // ---- The terms that go when nothing names them any more ----

  private static final String ASSIGNED_TERM = PROPERTY_PREFIX + "cccccccc-dddd-eeee-ffff-000000000000";

  /** A template declaring one text field and one element, and the instance's context alongside it. */
  private ObjectNode templateDeclaring(String... childNames) throws Exception {
    ObjectNode template = (ObjectNode) mapper.readTree("{\"_ui\":{\"order\":[]},\"properties\":{}}");
    ArrayNodeHelper.setOrder(template, childNames);
    ObjectNode properties = (ObjectNode) template.get("properties");
    for (String name : childNames) {
      properties.putObject(name).putObject("_ui").put("inputType", "textfield");
    }
    return template;
  }

  private ObjectNode instanceWithTerms(java.util.Map<String, String> terms) throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree("{\"@context\":{}}");
    ObjectNode context = (ObjectNode) instance.get("@context");
    terms.forEach(context::put);
    return instance;
  }

  @Test public void aTermNothingNamesIsRemoved() throws Exception {
    ObjectNode instance = instanceWithTerms(java.util.Map.of("Sex", ASSIGNED_TERM));
    ObjectNode template = templateDeclaring("Age");

    linkedDataUtil.pruneOrphanPropertyIris(instance, template, CedarResourceType.INSTANCE);

    assertFalse(instance.get("@context").has("Sex"),
      "an attribute the user renamed or deleted leaves a definition for a word nothing uses");
  }

  @Test public void aTermTheTemplateDeclaresStaysEvenWithNothingInTheBody() throws Exception {
    ObjectNode instance = instanceWithTerms(java.util.Map.of("Element", ASSIGNED_TERM));
    ObjectNode template = templateDeclaring("Element");

    linkedDataUtil.pruneOrphanPropertyIris(instance, template, CedarResourceType.INSTANCE);

    assertTrue(instance.get("@context").has("Element"),
      "an unfilled child is absent from the body and its definition still belongs there — the case "
        + "instances/005 carries, and the one the obvious rule deletes");
  }

  @Test public void aTermAnAttributeStillUsesStays() throws Exception {
    ObjectNode instance = instanceWithTerms(java.util.Map.of("Sex", ASSIGNED_TERM));
    ((ObjectNode) instance).putArray("Attributes").add("Sex");
    ObjectNode template = templateDeclaring("Attributes");

    linkedDataUtil.pruneOrphanPropertyIris(instance, template, CedarResourceType.INSTANCE);

    assertTrue(instance.get("@context").has("Sex"), "an attribute-value field names it, so it is in use");
  }

  @Test public void anAuthorsOwnIriIsNeverRemoved() throws Exception {
    ObjectNode instance = instanceWithTerms(java.util.Map.of("Sex", "http://purl.obolibrary.org/obo/PATO_0000047"));
    ObjectNode template = templateDeclaring("Age");

    linkedDataUtil.pruneOrphanPropertyIris(instance, template, CedarResourceType.INSTANCE);

    assertTrue(instance.get("@context").has("Sex"),
      "a term from a real vocabulary is the author's choice and the point of the key");
  }

  @Test public void thePrefixesAndSystemKeysAreUntouched() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{\"xsd\":\"http://www.w3.org/2001/XMLSchema#\","
        + "\"schema:name\":{\"@type\":\"xsd:string\"},\"Sex\":\"" + ASSIGNED_TERM + "\"}}");

    linkedDataUtil.pruneOrphanPropertyIris(instance, templateDeclaring("Age"), CedarResourceType.INSTANCE);

    ObjectNode context = (ObjectNode) instance.get("@context");
    assertTrue(context.has("xsd"), "a prefix is not a property IRI this server assigned");
    assertTrue(context.has("schema:name"), "nor is a system key's datatype mapping");
    assertFalse(context.has("Sex"));
  }

  @Test public void anOrphanInsideAnElementOccurrenceIsRemoved() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"An Element\":{\"@context\":{\"Sex\":\"" + ASSIGNED_TERM + "\"}}}");
    ObjectNode template = (ObjectNode) mapper.readTree(
      "{\"_ui\":{\"order\":[\"An Element\"]},\"properties\":{\"An Element\":"
        + "{\"_ui\":{\"order\":[\"Inner\"]},\"properties\":{\"Inner\":{\"_ui\":{\"inputType\":\"textfield\"}}}}}}");

    linkedDataUtil.pruneOrphanPropertyIris(instance, template, CedarResourceType.INSTANCE);

    assertFalse(instance.get("An Element").get("@context").has("Sex"),
      "a term belongs to the node holding it, and so does the question of whether anything names it");
  }

  @Test public void everyOccurrenceOfAMultiInstanceElementIsPruned() throws Exception {
    ObjectNode instance = (ObjectNode) mapper.readTree(
      "{\"@context\":{},\"Many\":[{\"@context\":{\"Sex\":\"" + ASSIGNED_TERM + "\"}},"
        + "{\"@context\":{\"Sex\":\"" + ASSIGNED_TERM + "\"}}]}");
    ObjectNode template = (ObjectNode) mapper.readTree(
      "{\"_ui\":{\"order\":[\"Many\"]},\"properties\":{\"Many\":{\"type\":\"array\",\"items\":"
        + "{\"_ui\":{\"order\":[]},\"properties\":{}}}}}");

    linkedDataUtil.pruneOrphanPropertyIris(instance, template, CedarResourceType.INSTANCE);

    instance.get("Many").forEach(occurrence ->
      assertFalse(occurrence.get("@context").has("Sex"), "each occurrence carries its own context"));
  }

  @Test public void withoutATemplateNothingIsRemoved() throws Exception {
    ObjectNode instance = instanceWithTerms(java.util.Map.of("Sex", ASSIGNED_TERM));

    linkedDataUtil.pruneOrphanPropertyIris(instance, null, CedarResourceType.INSTANCE);

    assertTrue(instance.get("@context").has("Sex"),
      "the template is what tells an orphan from a child, so without one nothing is decided");
  }
}
