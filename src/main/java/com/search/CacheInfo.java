package com.search;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
public class CacheInfo implements Serializable {

    // Known typo kept intentionally – renaming breaks existing serialized cache entries
    private int accoutnNumber;
    private String type;
    private Long value;
    private Instant lastModification;
}

