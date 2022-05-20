package com.indico.graphql.enums

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonEnumDefaultValue

/**
 * An enumeration.
 */
@Generated
public enum class SubmissionResultVersion {
  LATEST,
  OLDEST_SUPPORTED,
  ONE,
  TWO,
  THREE,
  /**
   * This is a default enum value that will be used when attempting to deserialize unknown value.
   */
  @JsonEnumDefaultValue
  __UNKNOWN_VALUE,
}
