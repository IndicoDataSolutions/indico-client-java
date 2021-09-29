package com.indico.entity

import com.indico.graphql.enums.SubmissionStatus

/***
 * Base filter class - AND list and OR list for graphql queries.
 */
abstract class Filter(val ands: List<Filter> = ArrayList(), val ors: List<Filter> = ArrayList()){

}

/**
 * Submission Filters.
 * For a single filter value, populate the values.
 * Otherwise: enter a single value per object for each AND filter or each OR filter
 * and populate the "ands" list and the "ors" list with a list of the matching parameter.
 */
class SubmissionFilter(ands: List<Filter> = ArrayList(), ors: List<Filter> = ArrayList()): Filter(ands, ors){
    /**
     * Name of the input file name used in the submission.
     */
    var inputFileName: String? = null

    /**
     * Is this boolean retrieved?
     */
    var retrieved: Boolean? = null

    /**
     * Status of the submission.
     */
    var status: SubmissionStatus? = null
}