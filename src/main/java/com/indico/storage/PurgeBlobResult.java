package com.indico.storage;

import java.util.List;
import com.indico.Enums.PurgeBlobStatus;


/**
 * Result of a purge blog request
 */
public class PurgeBlobResult {
    public PurgeBlobStatus status;
    public List<String> errors;
}