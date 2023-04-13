package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import org.metadatacenter.util.json.JsonMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonDeserialize(converter = TrustedFoldersConfigConverter.class)
public class TrustedFoldersConfig {

  private String foldersStr;

  @JsonIgnore
  private Map<String, String> foldersMap = new HashMap<>();

  public String getFoldersStr() {
    return foldersStr;
  }

  public Map<String, String> getFoldersMap() {
    return foldersMap;
  }

  public void setFoldersMap(Map<String, String> foldersMap) {
    this.foldersMap = foldersMap;
  }

}

/**
 * This class transforms the input string to a map to be able to quickly check if a folderId is trusted
 */
class TrustedFoldersConfigConverter extends StdConverter<TrustedFoldersConfig, TrustedFoldersConfig> {
  @Override
  public TrustedFoldersConfig convert(TrustedFoldersConfig trustedFoldersConfig) {
    if (trustedFoldersConfig.getFoldersStr() != null &&
        trustedFoldersConfig.getFoldersStr().length() > 0 &&
        trustedFoldersConfig.getFoldersStr().charAt(0) != '$') {
      try {
        Map<String, List<String>> map = JsonMapper.MAPPER.readValue(trustedFoldersConfig.getFoldersStr(), Map.class);
        Map<String, String> folderToEntityMap = new HashMap<>();
        for (String entityName : map.keySet()) {
          for (String folderId : map.get(entityName)) {
            folderToEntityMap.put(folderId, entityName);
          }
        }
        trustedFoldersConfig.setFoldersMap(folderToEntityMap);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return trustedFoldersConfig;
  }
}
