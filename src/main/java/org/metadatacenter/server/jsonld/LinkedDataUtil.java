package org.metadatacenter.server.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.config.LinkedDataConfig;
import org.metadatacenter.constant.LinkedData;
import org.metadatacenter.id.CedarResourceId;
import org.metadatacenter.model.CedarResourceType;
import org.metadatacenter.model.ModelNodeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
      for (JsonNode attributeName : field.getValue()) {
        String attribute = attributeName.asText();
        if (!attribute.isBlank() && !context.has(attribute)) {
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

  /** Field kinds a container gives no property IRI: they carry no value a property could name. */
  private static final Set<String> UNMAPPED_INPUT_TYPES =
      Set.of("page-break", "section-break", "richtext", "image", "youtube", "attribute-value");

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
      if (contextProperties != null && !contextProperties.has(childName)) {
        ObjectNode mapping = contextProperties.putObject(childName);
        mapping.putArray(ModelNodeNames.JSON_SCHEMA_ENUM).add(PROPERTY_IRI_PREFIX + UUID.randomUUID());
        requireChild(properties, childName);
      }
      addChildPropertyIrisToContainer(child); // an element maps its own children
    }
  }

  /** The order a container declares is the list of its children. */
  private List<String> childNames(JsonNode container) {
    List<String> names = new ArrayList<>();
    JsonNode order = container.path(ModelNodeNames.UI).path(ModelNodeNames.UI_ORDER);
    if (order.isArray()) {
      order.forEach(name -> names.add(name.asText()));
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
    JsonNode required = context.get(ModelNodeNames.JSON_SCHEMA_REQUIRED);
    if (required != null && required.isArray()) {
      ((ArrayNode) required).add(childName);
    }
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
