package org.metadatacenter.server.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.config.LinkedDataConfig;
import org.metadatacenter.constant.LinkedData;
import org.metadatacenter.id.CedarResourceId;
import org.metadatacenter.model.CedarResourceType;
import org.metadatacenter.model.ModelNodeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;

public class LinkedDataUtil {

  private static final Logger logger = LoggerFactory.getLogger(LinkedDataUtil.class);

  protected static final String SEPARATOR = "/";

  /** The namespace every CEDAR property IRI lives in. */
  private static final String PROPERTY_IRI_PREFIX = "https://schema.metadatacenter.org/properties/";

  private final LinkedDataConfig ldConfig;
  private final List<String> knownPrefixes;

  public LinkedDataUtil(LinkedDataConfig ldConfig) {
    this.ldConfig = ldConfig;
    knownPrefixes = new ArrayList<>();
    for (CedarResourceType nt : CedarResourceType.values()) {
      knownPrefixes.add(getLinkedDataPrefix(nt));
    }
  }

  private String getLinkedDataPrefix(CedarResourceType resourceType) {
    if (resourceType == CedarResourceType.USER) {
      return ldConfig.getUsersBase();
    } else {
      return ldConfig.getBase() + resourceType.getPrefix() + SEPARATOR;
    }
  }

  public String getUserId(String uuid) {
    return getLinkedDataId(CedarResourceType.USER, uuid);
  }

  public String getLinkedDataId(CedarResourceType resourceType, String uuid) {
    return getLinkedDataPrefix(resourceType) + uuid;
  }

  //TODO: create wrapped object
  public String buildNewLinkedDataId(CedarResourceType resourceType) {
    return getLinkedDataId(resourceType, UUID.randomUUID().toString());
  }

  public <T extends CedarResourceId> T buildNewLinkedDataIdObject(Class<T> type) {
    CedarResourceType cedarResourceType = CedarResourceType.forResourceIdClass(type);
    String id = buildNewLinkedDataId(cedarResourceType);
    return (T) CedarResourceId.build(id, cedarResourceType);
  }

  public String getUUID(String resourceId, CedarResourceType resourceType) {
    if (resourceId != null) {
      int pos = resourceId.lastIndexOf(SEPARATOR);
      return resourceId.substring(pos + 1);
    } else {
      return null;
    }
  }

  /**
   * Adds template element instance @id's to the instance if necessary
   */
  public void addElementInstanceIds(JsonNode nodeContent, CedarResourceType resourceType) {
    if (resourceType.equals(CedarResourceType.INSTANCE)) { // this is just a check to avoid running it for other node
      // types
      Iterator<Map.Entry<String, JsonNode>> fieldsIterator = nodeContent.fields();
      while (fieldsIterator.hasNext()) {
        Map.Entry<String, JsonNode> field = fieldsIterator.next();
        if (field.getValue().isContainerNode()) {
          if (!field.getKey().equals(LinkedData.CONTEXT)) {
            addElementInstanceIdsToPotentialElementInstance(field.getValue());
          }
        }
      }
    }
  }

  private void addElementInstanceIdsToPotentialElementInstance(JsonNode fieldContent) {
    // Single value
    if (fieldContent.isObject()) {
      // Check that it is an element instance
      if (isElementInstance(fieldContent)) {
        //  and has no id
        if (needsId(fieldContent)) {
          String id = buildNewLinkedDataId(CedarResourceType.ELEMENT_INSTANCE);
          ((ObjectNode) fieldContent).put(LinkedData.ID, id);
          addElementInstanceIds(fieldContent, CedarResourceType.INSTANCE);
        }
        // iterate over possible children of element instance type
        Iterator<Map.Entry<String, JsonNode>> childrenIterator = fieldContent.fields();
        while (childrenIterator.hasNext()) {
          Map.Entry<String, JsonNode> child = childrenIterator.next();
          if (!child.getKey().equals(LinkedData.CONTEXT) && !child.getKey().equals(LinkedData.ID)) {
            addElementInstanceIdsToPotentialElementInstance(child.getValue());
          }
        }
      }
    }
    // it is an Array (Multi-instance value)
    else if (fieldContent.isArray()) {
      for (int i = 0; i < fieldContent.size(); i++) {
        addElementInstanceIdsToPotentialElementInstance(fieldContent.get(i));
      }
    }
  }

  private boolean isElementInstance(JsonNode fieldContent) {
    return fieldContent != null && fieldContent.has(LinkedData.CONTEXT);
  }

  /**
   * Assigns a property IRI to every attribute an instance names and has none for.
   *
   * <p>A user names an attribute while filling a form, so nothing could have minted an IRI for it
   * earlier: the name did not exist until then. A draft therefore carries the attribute's value at the
   * instance root and no {@code @context} term, which the model permits — an instance's context
   * requires the standard prefixes and the system keys, and no attribute name. This fills the term, so
   * that what is stored says which property the value is of.
   *
   * <p>An attribute-value field's own value is the list of attribute names it holds, so the document
   * enumerates the work rather than hiding it. The term goes in the {@code @context} of the node
   * holding the field, which is the root for a field at the top level and the occurrence's own context
   * for one inside an element.
   *
   * <p>A term already present is left alone, whoever assigned it: an identifier is worth having
   * because it is stable, and re-minting on every save would take that away. A blank name is skipped
   * rather than named — an attribute with no name is a defect in whatever wrote it, and minting for it
   * would create a property IRI nothing can be said about.
   */
  public void addAttributeValuePropertyIris(JsonNode nodeContent, CedarResourceType resourceType) {
    if (resourceType.equals(CedarResourceType.INSTANCE)) {
      addAttributeValuePropertyIrisToNode(nodeContent);
    }
  }

  private void addAttributeValuePropertyIrisToNode(JsonNode node) {
    if (node.isArray()) {
      node.forEach(this::addAttributeValuePropertyIrisToNode);
      return;
    }
    if (!node.isObject()) {
      return;
    }
    JsonNode context = node.get(LinkedData.CONTEXT);
    if (context != null && context.isObject()) {
      nameAttributesOf(node, (ObjectNode) context);
    }
    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
    while (fieldsIterator.hasNext()) {
      Map.Entry<String, JsonNode> field = fieldsIterator.next();
      if (!field.getKey().equals(LinkedData.CONTEXT)) {
        addAttributeValuePropertyIrisToNode(field.getValue());
      }
    }
  }

  private void nameAttributesOf(JsonNode holder, ObjectNode context) {
    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = holder.fields();
    while (fieldsIterator.hasNext()) {
      Map.Entry<String, JsonNode> field = fieldsIterator.next();
      if (!isAttributeValueField(field.getValue())) {
        continue;
      }
      ArrayNode attributeNames = (ArrayNode) field.getValue();
      // Blank rows exist while an editor is waiting for a user to name an
      // attribute. They are draft UI state and cannot name a stored property.
      for (int index = attributeNames.size() - 1; index >= 0; index--) {
        if (attributeNames.get(index).asText().isBlank()) {
          attributeNames.remove(index);
        }
      }
      for (JsonNode attributeName : attributeNames) {
        String attribute = attributeName.asText();
        if (!context.has(attribute)) {
          context.put(attribute, PROPERTY_IRI_PREFIX + UUID.randomUUID());
        }
      }
    }
  }

  /** An attribute-value field is written as the list of attribute names it holds. */
  private boolean isAttributeValueField(JsonNode value) {
    if (!value.isArray() || value.isEmpty()) {
      return false;
    }
    for (JsonNode entry : value) {
      if (!entry.isTextual()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Removes the {@code @context} terms of attributes nothing names any more.
   *
   * <p>The server assigns a property IRI to every attribute a user names, and leaves an assigned one
   * alone. Nothing removed one, so a context kept a definition for every attribute a user ever renamed
   * or deleted — a term for a word the document no longer uses.
   *
   * <p>Three questions decide each term, and two of them need the template, which is why this is given
   * one rather than working on the document alone:
   *
   * <ul>
   *   <li>Does the template declare a child of that name? Then it is structure. It stays whether the
   *       instance carries the child or not — an unfilled child is absent from the body and its
   *       definition still belongs there. {@code instances/005} in the shared corpus carries two.</li>
   *   <li>Does an attribute-value field name it? Then it is in use, and stays.</li>
   *   <li>Otherwise it is the orphan, and goes — but only if the IRI is one the repository assigned.
   *       An author who mapped a child to a term from a real vocabulary chose that IRI deliberately,
   *       and it is the point of the key.</li>
   * </ul>
   *
   * <p>An orphan and a structural term are indistinguishable inside the document: both are a name in
   * the context, mapped to a {@code .../properties/...} address, used nowhere in the body. Only the
   * template tells them apart, which is why the obvious rule — drop what the body does not use —
   * deletes an author's work.
   */
  public void pruneOrphanPropertyIris(JsonNode instance, JsonNode template, CedarResourceType resourceType) {
    if (resourceType.equals(CedarResourceType.INSTANCE) && instance != null && template != null) {
      pruneNode(instance, template);
    }
  }

  private void pruneNode(JsonNode instanceNode, JsonNode schemaNode) {
    if (instanceNode == null || !instanceNode.isObject() || schemaNode == null || !schemaNode.isObject()) {
      return;
    }
    Set<String> declared = declaredChildNames(schemaNode);
    ObjectNode context = contextOf(instanceNode);
    if (context != null) {
      Set<String> inUse = attributeNamesIn(instanceNode);
      List<String> orphans = new ArrayList<>();
      Iterator<Map.Entry<String, JsonNode>> terms = context.fields();
      while (terms.hasNext()) {
        Map.Entry<String, JsonNode> term = terms.next();
        JsonNode iri = term.getValue();
        boolean assignedHere = iri.isTextual() && iri.asText().startsWith(PROPERTY_IRI_PREFIX);
        if (assignedHere && !declared.contains(term.getKey()) && !inUse.contains(term.getKey())) {
          orphans.add(term.getKey());
        }
      }
      orphans.forEach(context::remove);
    }
    JsonNode schemaProperties = schemaNode.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
    if (schemaProperties == null || !schemaProperties.isObject()) {
      return;
    }
    for (String childName : declared) {
      JsonNode childSchema = childDefinition(schemaProperties.get(childName));
      JsonNode childInstance = instanceNode.get(childName);
      if (childSchema == null || childInstance == null) {
        continue;
      }
      if (childInstance.isArray()) {
        childInstance.forEach(occurrence -> pruneNode(occurrence, childSchema));
      } else {
        pruneNode(childInstance, childSchema);
      }
    }
  }

  /** Every attribute name the attribute-value fields of this node currently hold. */
  private Set<String> attributeNamesIn(JsonNode node) {
    Set<String> names = new LinkedHashSet<>();
    Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
    while (fieldsIterator.hasNext()) {
      Map.Entry<String, JsonNode> field = fieldsIterator.next();
      if (isAttributeValueField(field.getValue())) {
        field.getValue().forEach(name -> names.add(name.asText()));
      }
    }
    return names;
  }

  private Set<String> declaredChildNames(JsonNode schemaNode) {
    return new LinkedHashSet<>(childNames(schemaNode));
  }

  private ObjectNode contextOf(JsonNode node) {
    JsonNode context = node.get(LinkedData.CONTEXT);
    return context != null && context.isObject() ? (ObjectNode) context : null;
  }

  /** Field kinds a container gives no property IRI: they carry no value a property could name. */
  private static final Set<String> UNMAPPED_INPUT_TYPES =
      Set.of("page-break", "section-break", "richtext", "image", "youtube", "attribute-value");

  private static final Set<String> RESERVED_ATTRIBUTE_VALUE_NAMES = Set.of(
      "@context", "@id", "@type", "@value", "@language",
      "schema:isBasedOn", "schema:name", "schema:description",
      "pav:derivedFrom", "pav:createdOn", "pav:createdBy", "pav:lastUpdatedOn",
      "oslc:modifiedBy", "rdfs:label", "skos:prefLabel", "skos:altLabel",
      "skos:notation", "_annotations");

  /** One defect carried unchanged from storage into an ordinary update. */
  public record LegacyArtifactRepair(String path, String issue, String previousValue) {}

  /**
   * Repairs only defects demonstrably inherited from the stored artifact. A
   * newly introduced malformed value is left in place for validation to
   * reject. This distinction lets an old production artifact survive an
   * ordinary edit without turning the compatibility path into a general
   * sanitizer for new requests.
   */
  public List<LegacyArtifactRepair> repairInheritedDefects(JsonNode submitted, JsonNode stored, JsonNode template,
                                                           CedarResourceType resourceType) {
    List<LegacyArtifactRepair> repairs = new ArrayList<>();
    if (submitted == null || stored == null) {
      return repairs;
    }
    repairInheritedDerivedFrom(submitted, stored, "", repairs);
    if (resourceType == CedarResourceType.TEMPLATE || resourceType == CedarResourceType.ELEMENT) {
      repairInheritedSchemaMappings(submitted, stored, "", repairs);
    } else if (resourceType == CedarResourceType.INSTANCE) {
      repairInheritedOccurrenceIds(submitted, stored, "", repairs, true);
      if (template != null) {
        repairInheritedAttributeNames(submitted, stored, template, "", repairs);
      }
    }
    return repairs;
  }

  /**
   * Removes only an unusable provenance value carried unchanged from storage.
   * The optional key is omitted when there is no source artifact. A new empty
   * or relative value remains in the request so persistence validation can
   * reject it.
   */
  private void repairInheritedDerivedFrom(JsonNode submitted, JsonNode stored, String path,
                                          List<LegacyArtifactRepair> repairs) {
    if (submitted == null) {
      return;
    }
    if (submitted.isArray()) {
      for (int index = 0; index < submitted.size(); index++) {
        JsonNode storedItem = stored != null && stored.isArray() && index < stored.size() ? stored.get(index) : null;
        repairInheritedDerivedFrom(submitted.get(index), storedItem, path + "/" + index, repairs);
      }
      return;
    }
    if (!submitted.isObject()) {
      return;
    }
    JsonNode submittedDerivedFrom = submitted.get(ModelNodeNames.PAV_DERIVED_FROM);
    JsonNode storedDerivedFrom = stored != null && stored.isObject()
        ? stored.get(ModelNodeNames.PAV_DERIVED_FROM) : null;
    if (submittedDerivedFrom != null && submittedDerivedFrom.equals(storedDerivedFrom)
        && submittedDerivedFrom.isTextual() && !isAbsoluteIri(submittedDerivedFrom.asText())) {
      ((ObjectNode) submitted).remove(ModelNodeNames.PAV_DERIVED_FROM);
      repairs.add(new LegacyArtifactRepair(path + "/" + ModelNodeNames.PAV_DERIVED_FROM,
          "unusable inherited pav:derivedFrom removed", submittedDerivedFrom.asText()));
    }
    Iterator<Map.Entry<String, JsonNode>> fields = submitted.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      JsonNode storedChild = stored != null && stored.isObject() ? stored.get(field.getKey()) : null;
      repairInheritedDerivedFrom(field.getValue(), storedChild,
          path + "/" + escapePointer(field.getKey()), repairs);
    }
  }

  private void repairInheritedSchemaMappings(JsonNode submitted, JsonNode stored, String path,
                                             List<LegacyArtifactRepair> repairs) {
    if (!submitted.isObject() || !stored.isObject()) {
      return;
    }
    JsonNode submittedProperties = submitted.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
    JsonNode storedProperties = stored.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
    if (submittedProperties == null || !submittedProperties.isObject()
        || storedProperties == null || !storedProperties.isObject()) {
      return;
    }
    ObjectNode submittedContext = contextPropertiesOf(submittedProperties);
    ObjectNode storedContext = contextPropertiesOf(storedProperties);
    for (String childName : childNames(submitted)) {
      JsonNode submittedMapping = submittedContext == null ? null : submittedContext.get(childName);
      JsonNode storedMapping = storedContext == null ? null : storedContext.get(childName);
      if (submittedMapping != null && submittedMapping.equals(storedMapping)
          && !hasUsablePropertyIri(submittedMapping)) {
        submittedContext.remove(childName);
        removeAllRequiredOccurrences(submittedProperties, childName);
        repairs.add(new LegacyArtifactRepair(path + "/properties/@context/properties/" + escapePointer(childName),
            "unusable child property IRI removed for server reminting", submittedMapping.toString()));
      }
      JsonNode submittedChild = childDefinition(submittedProperties.get(childName));
      JsonNode storedChild = childDefinition(storedProperties.get(childName));
      if (submittedChild != null && storedChild != null) {
        repairInheritedSchemaMappings(submittedChild, storedChild,
            path + "/properties/" + escapePointer(childName), repairs);
      }
    }
  }

  private void repairInheritedOccurrenceIds(JsonNode submitted, JsonNode stored, String path,
                                            List<LegacyArtifactRepair> repairs, boolean documentRoot) {
    if (submitted == null) {
      return;
    }
    if (submitted.isArray()) {
      for (int index = 0; index < submitted.size(); index++) {
        JsonNode storedItem = stored != null && stored.isArray() && index < stored.size() ? stored.get(index) : null;
        repairInheritedOccurrenceIds(submitted.get(index), storedItem, path + "/" + index, repairs, false);
      }
      return;
    }
    if (!submitted.isObject()) {
      return;
    }
    if (!documentRoot && submitted.has(LinkedData.CONTEXT)) {
      JsonNode submittedId = submitted.get(LinkedData.ID);
      JsonNode storedId = stored != null && stored.isObject() ? stored.get(LinkedData.ID) : null;
      if (submittedId != null && submittedId.equals(storedId) && submittedId.isTextual()
          && !isAbsoluteIri(submittedId.asText())) {
        ((ObjectNode) submitted).putNull(LinkedData.ID);
        repairs.add(new LegacyArtifactRepair(path + "/@id",
            "unusable element occurrence identifier reset for server reminting", submittedId.asText()));
      }
    }
    Iterator<Map.Entry<String, JsonNode>> fields = submitted.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      if (LinkedData.CONTEXT.equals(field.getKey()) || LinkedData.ID.equals(field.getKey())) {
        continue;
      }
      JsonNode storedChild = stored != null && stored.isObject() ? stored.get(field.getKey()) : null;
      repairInheritedOccurrenceIds(field.getValue(), storedChild,
          path + "/" + escapePointer(field.getKey()), repairs, false);
    }
  }

  private void repairInheritedAttributeNames(JsonNode submitted, JsonNode stored, JsonNode schema, String path,
                                             List<LegacyArtifactRepair> repairs) {
    if (!submitted.isObject() || !stored.isObject() || !schema.isObject()) {
      return;
    }
    JsonNode schemaProperties = schema.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
    if (schemaProperties == null || !schemaProperties.isObject()) {
      return;
    }
    Set<String> groups = new LinkedHashSet<>();
    Set<String> serializingChildren = new LinkedHashSet<>();
    for (String childName : childNames(schema)) {
      JsonNode child = childDefinition(schemaProperties.get(childName));
      String inputType = child.path(ModelNodeNames.UI).path(ModelNodeNames.UI_FIELD_INPUT_TYPE).asText();
      if ("attribute-value".equals(inputType)) {
        groups.add(childName);
      } else if (!UNMAPPED_INPUT_TYPES.contains(inputType)) {
        serializingChildren.add(childName);
      }
    }

    Set<String> seenNames = new LinkedHashSet<>();
    for (String group : groups) {
      JsonNode submittedNames = submitted.get(group);
      JsonNode storedNames = stored.get(group);
      if (submittedNames == null || !submittedNames.isArray()) {
        continue;
      }
      Map<String, Integer> inheritedNameCounts = new HashMap<>();
      if (storedNames != null && storedNames.isArray()) {
        storedNames.forEach(name -> {
          if (name.isTextual()) {
            inheritedNameCounts.merge(name.asText(), 1, Integer::sum);
          }
        });
      }
      ArrayNode kept = JsonNodeFactory.instance.arrayNode();
      boolean changed = false;
      for (JsonNode nameNode : submittedNames) {
        if (!nameNode.isTextual()) {
          kept.add(nameNode);
          continue;
        }
        String name = nameNode.asText();
        int inheritedCount = inheritedNameCounts.getOrDefault(name, 0);
        boolean inherited = inheritedCount > 0;
        if (inherited) {
          inheritedNameCounts.put(name, inheritedCount - 1);
        }
        boolean reserved = name.startsWith("@") || RESERVED_ATTRIBUTE_VALUE_NAMES.contains(name);
        boolean structuralCollision = groups.contains(name) || serializingChildren.contains(name);
        boolean duplicate = seenNames.contains(name);
        boolean repairable = name.isBlank() || reserved || structuralCollision || duplicate;
        if (inherited && repairable) {
          String issue = name.isBlank() ? "blank attribute name removed"
              : reserved ? "reserved attribute name removed"
              : structuralCollision ? "attribute name colliding with a template child removed"
              : "duplicate attribute name removed";
          repairs.add(new LegacyArtifactRepair(path + "/" + escapePointer(group), issue, name));
          changed = true;
        } else {
          kept.add(nameNode);
          if (!name.isBlank()) {
            seenNames.add(name);
          }
        }
      }
      if (changed) {
        ((ArrayNode) submittedNames).removeAll().addAll(kept);
      }
    }

    for (String childName : childNames(schema)) {
      if (groups.contains(childName)) {
        continue;
      }
      JsonNode childSchema = childDefinition(schemaProperties.get(childName));
      JsonNode submittedChild = submitted.get(childName);
      JsonNode storedChild = stored.get(childName);
      if (submittedChild == null || storedChild == null || childSchema == null) {
        continue;
      }
      String childPath = path + "/" + escapePointer(childName);
      if (submittedChild.isArray() && storedChild.isArray()) {
        for (int index = 0; index < submittedChild.size(); index++) {
          JsonNode storedItem = index < storedChild.size() ? storedChild.get(index) : null;
          if (storedItem != null) {
            repairInheritedAttributeNames(submittedChild.get(index), storedItem, childSchema,
                childPath + "/" + index, repairs);
          }
        }
      } else {
        repairInheritedAttributeNames(submittedChild, storedChild, childSchema, childPath, repairs);
      }
    }
  }

  private boolean hasUsablePropertyIri(JsonNode mapping) {
    JsonNode values = mapping == null ? null : mapping.get(ModelNodeNames.JSON_SCHEMA_ENUM);
    return values != null && values.isArray() && values.size() == 1 && values.get(0).isTextual()
        && isAbsoluteIri(values.get(0).asText());
  }

  private void removeAllRequiredOccurrences(JsonNode properties, String childName) {
    JsonNode context = properties.get(LinkedData.CONTEXT);
    JsonNode required = context == null ? null : context.get(ModelNodeNames.JSON_SCHEMA_REQUIRED);
    if (required == null || !required.isArray()) {
      return;
    }
    for (int index = required.size() - 1; index >= 0; index--) {
      if (required.get(index).isTextual() && childName.equals(required.get(index).asText())) {
        ((ArrayNode) required).remove(index);
      }
    }
  }

  private static boolean isAbsoluteIri(String value) {
    if (value == null || value.isBlank()) {
      return false;
    }
    try {
      return new URI(value).isAbsolute();
    } catch (URISyntaxException e) {
      return false;
    }
  }

  private static String escapePointer(String component) {
    return component.replace("~", "~0").replace("/", "~1");
  }

  /**
   * Assigns a property IRI to every child of a template or element that has none.
   *
   * <p>A container maps each child's name to the property the child's value is of, in the `@context`
   * block of the schema it renders. The name is the author's and can change; the property IRI is the
   * identity underneath it, so it is the repository's to assign, exactly as an attribute's is. Both
   * model libraries used to derive one from the child's name — reproducible, but an identity nothing
   * assigned, and one that would change the moment the author renamed the child.
   *
   * <p>Only a child that carries a value is mapped. A static field displays something and holds
   * nothing, and an attribute-value field's names are mapped in the instance rather than here, so
   * neither takes an entry — which is what both libraries have always done.
   *
   * <p>A mapping already present is left alone, whoever wrote it: most carry an IRI the author chose
   * from a real vocabulary, and that is the point of the key.
   */
  public void addChildPropertyIris(JsonNode nodeContent, CedarResourceType resourceType) {
    if (resourceType.equals(CedarResourceType.TEMPLATE) || resourceType.equals(CedarResourceType.ELEMENT)) {
      addChildPropertyIrisToContainer(nodeContent);
    }
  }

  private void addChildPropertyIrisToContainer(JsonNode container) {
    if (!container.isObject()) {
      return;
    }
    JsonNode properties = container.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
    if (properties == null || !properties.isObject()) {
      return;
    }
    ObjectNode contextProperties = contextPropertiesOf(properties);
    for (String childName : childNames(container)) {
      JsonNode child = childDefinition(properties.get(childName));
      if (child == null || isUnmapped(child)) {
        continue;
      }
      if (contextProperties != null) {
        if (!contextProperties.has(childName)) {
          ObjectNode mapping = contextProperties.putObject(childName);
          mapping.putArray(ModelNodeNames.JSON_SCHEMA_ENUM).add(PROPERTY_IRI_PREFIX + UUID.randomUUID());
        }
        requireChild(properties, childName);
      }
      addChildPropertyIrisToContainer(child); // an element maps its own children
    }
  }

  /**
   * The schema properties that are actual CEDAR children. `_ui.order` is a
   * presentation index over this structure, not the authority for whether a
   * property exists.
   */
  private List<String> childNames(JsonNode container) {
    List<String> names = new ArrayList<>();
    JsonNode properties = container.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
    if (properties != null && properties.isObject()) {
      properties.fields().forEachRemaining(entry -> {
        JsonNode child = childDefinition(entry.getValue());
        JsonNode ui = child == null ? null : child.get(ModelNodeNames.UI);
        if (ui != null && ui.isObject()) {
          names.add(entry.getKey());
        }
      });
    }
    return names;
  }

  /** A multi-instance child is an array; the child itself is what the array holds. */
  private JsonNode childDefinition(JsonNode declared) {
    if (declared == null || !declared.isObject()) {
      return null;
    }
    JsonNode items = declared.get(ModelNodeNames.JSON_SCHEMA_ITEMS);
    return items != null && items.isObject() ? items : declared;
  }

  private boolean isUnmapped(JsonNode child) {
    return UNMAPPED_INPUT_TYPES.contains(child.path(ModelNodeNames.UI).path(ModelNodeNames.UI_FIELD_INPUT_TYPE).asText());
  }

  private ObjectNode contextPropertiesOf(JsonNode properties) {
    JsonNode context = properties.get(LinkedData.CONTEXT);
    if (context == null || !context.isObject()) {
      return null;
    }
    JsonNode contextProperties = context.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
    return contextProperties != null && contextProperties.isObject() ? (ObjectNode) contextProperties : null;
  }

  /** A mapped child is required of the instance's context, as the renderer writes it. */
  private void requireChild(JsonNode properties, String childName) {
    JsonNode context = properties.get(LinkedData.CONTEXT);
    if (context == null || !context.isObject()) {
      return;
    }
    JsonNode required = context.get(ModelNodeNames.JSON_SCHEMA_REQUIRED);
    if (required == null) {
      required = ((ObjectNode) context).putArray(ModelNodeNames.JSON_SCHEMA_REQUIRED);
    }
    if (required != null && required.isArray() && !containsText(required, childName)) {
      ((ArrayNode) required).add(childName);
    }
  }

  private boolean containsText(JsonNode array, String value) {
    for (JsonNode entry : array) {
      if (entry.isTextual() && value.equals(entry.asText())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether an element occurrence is asking for an identifier.
   *
   * <p>Two spellings mean the same thing, and both have to be answered. A client that leaves the key
   * out is the older shape. A client that writes {@code "@id": null} is saying so explicitly, which is
   * what the model libraries now write for a node that has no identity yet: an occurrence exists in a
   * document before anything can name it, and null is the honest value until the server assigns one.
   * Only {@code has} was checked here, so a null-valued key counted as an identifier already present
   * and the occurrence was passed over — the null then survived into storage.
   *
   * <p>An empty string is deliberately not treated as absent. It is not an identifier and not an
   * honest absence of one, and both model libraries now refuse to read it, so quietly minting over it
   * would hide whoever writes it. Stored artifacts carrying one are patched rather than repaired in
   * passing; see the backend roadmap.
   */
  private boolean needsId(JsonNode fieldContent) {
    JsonNode id = fieldContent.get(LinkedData.ID);
    return id == null || id.isNull();
  }

  public boolean isValidId(String id) {
    String uuid = null;
    if (id != null) {
      for (String prefix : knownPrefixes) {
        if (uuid == null && id.startsWith(prefix)) {
          uuid = id.substring(prefix.length());
        }
      }
    }
    if (uuid != null) {
      return !uuid.trim().isEmpty();
    }
    return false;
  }

}
