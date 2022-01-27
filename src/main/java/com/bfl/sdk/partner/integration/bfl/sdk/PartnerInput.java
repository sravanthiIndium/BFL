package com.bfl.sdk.partner.integration.bfl.sdk;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PartnerInput {
    private String JsonTag;
    private String Value;

    public PartnerInput(String jsonTag) {
        JsonTag = jsonTag;
    }
}
