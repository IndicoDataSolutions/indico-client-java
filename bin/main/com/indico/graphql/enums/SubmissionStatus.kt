package com.indico.graphql.enums

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonEnumDefaultValue

/**
 * An enumeration.
 */
@Generated
public enum class SubmissionStatus {
  COMPLETE,
  FAILED,
  PENDING_ADMIN_REVIEW,
  PENDING_AUTO_REVIEW,
  PENDING_REVIEW,
  PROCESSING,
  /**
   * This is a default enum value that will be used when attempting to deserialize unknown value.
   */
  @JsonEnumDefaultValue
  __UNKNOWN_VALUE,
}
