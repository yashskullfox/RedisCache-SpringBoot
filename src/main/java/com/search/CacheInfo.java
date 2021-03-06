package com.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CacheInfo implements Serializable {
    /* This class is Cache Data stored in Cache Bucket */

    private int accoutnNumber;
    private String type;
    private Long value;
    Instant lastModification;
}
