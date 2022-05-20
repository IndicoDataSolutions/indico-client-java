package com.indico.graphql.enums

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonEnumDefaultValue

/**
 * Adapted from Celery Task Status
 */
@Generated
public enum class JobStatus {
  FAILURE,
  IGNORED,
  PENDING,
  RECEIVED,
  REJECTED,
  RETRY,
  REVOKED,
  STARTED,
  SUCCESS,
  TRAILED,
  /**
   * This is a default enum value that will be used when attempting to deserialize unknown value.
   */
  @JsonEnumDefaultValue
  __UNKNOWN_VALUE,
}
