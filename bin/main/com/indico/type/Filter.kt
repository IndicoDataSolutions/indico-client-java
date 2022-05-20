package com.indico.type

import com.indico.type.SubmissionStatus

/***
 * Base filter class - AND list and OR list for graphql queries.
 */
abstract class Filter(val ands: List<Filter> = ArrayList(), val ors: List<Filter> = ArrayList()){

}

/**
 * Submission Filters.
 * For a single filter value, populate the value you want to filter on.
 * Otherwise: enter a *single value* in a *single object* for each AND filter or each OR filter
 * and populate the "ands" list and the "ors" list with a list of the matching parameter.
 */
class SubmissionFilter private constructor( /**
                                             * Name of the input file name used in the submission.
                                             */
                                            var inputFileName: String? = null,

                                            /**
                                             * Is this boolean retrieved?
                                             */
                                            var retrieved: Boolean? = null,

                                            /**
                                             * Status of the submission.
                                             */
                                            var status: SubmissionStatus? = null,
        ands: List<SubmissionFilter>,  ors: List<SubmissionFilter>): Filter(ands, ors) {


    data class Builder(
        var inputFileName: String? = null,

        var retrieved: Boolean? = null,
        var status: SubmissionStatus? = null,
        var ands: List<SubmissionFilter> = ArrayList(), var ors: List<SubmissionFilter> = ArrayList()){

        /**
         * Name of the input file name used in the submission.
         */
        fun inputFileName(inputFileName: String)  =apply {this.inputFileName = inputFileName}

        /**
         * Is this boolean retrieved?
         */
        fun retrieved(retrieved: Boolean) = apply {this.retrieved = retrieved}
        /**
         * Status of the submission.
         */
        fun status(status: SubmissionStatus) = apply {this.status = status}

        /**
         * AND Filters.
         */
        fun ands(ands: List<SubmissionFilter>) = apply {this.ands = ands}

        /**
         * OR Filters.
         */
        fun ors(ors: List<SubmissionFilter>) = apply {this.ors = ors}

        /**
         * Create the builder.
         */
        fun build() = SubmissionFilter(inputFileName, retrieved, status, ands, ors)


    }
}