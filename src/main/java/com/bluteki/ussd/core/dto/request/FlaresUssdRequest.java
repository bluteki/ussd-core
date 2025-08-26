package com.bluteki.ussd.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlaresUssdRequest {
    private String msisdn;
    private String sessionId;
    private String type;
    private String msg;
}
