package com.bfl.sdk.partner.integration.bfl.sdk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Specs {

    private String Id;
    private String ParamName;
    private String JsonTag;
    private int Min_len;
    private int Max_len;
    private String IsMandatory;
    private String DataType;
}
