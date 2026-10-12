package com.bluteki.ussd.core.service.operator;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;

public interface OperatorService {
     CommonUssdResponse processRequest(CommonUssdRequest request);

    boolean supports(String operator);
}
