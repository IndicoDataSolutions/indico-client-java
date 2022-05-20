package com.indico.graphql.jobstatusgraphql

import com.expediagroup.graphql.client.Generated
import com.indico.graphql.enums.JobStatus

@Generated
public data class Job(
  public val status: JobStatus?
)
