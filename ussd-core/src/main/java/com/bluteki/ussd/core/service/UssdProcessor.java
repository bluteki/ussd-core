package com.bluteki.ussd.core.service;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;

public interface UssdProcessor {
    CommonUssdResponse handleInitialRequest(CommonUssdRequest request);
    CommonUssdResponse handleUserInput(CommonUssdRequest request);
}
