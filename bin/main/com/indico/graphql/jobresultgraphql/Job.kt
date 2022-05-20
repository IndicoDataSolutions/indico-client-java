package com.indico.graphql.jobresultgraphql

import com.expediagroup.graphql.client.Generated
import com.indico.graphql.JSONString
import com.indico.graphql.enums.JobStatus

@Generated
public data class Job(
  public val status: JobStatus?,
  public val result: JSONString?
)
