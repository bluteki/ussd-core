package com.bluteki.ussd.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlaresUssdResponse {
    private String msisdn;
    private String sessionId;
    private String type;
    private String msg;
}
