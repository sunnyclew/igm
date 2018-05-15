package com.igm.igm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {
    private int idx;
    private String dName;
    private String dPlace;
    private String dUser;
}
