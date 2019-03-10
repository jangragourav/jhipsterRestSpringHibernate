package com.srsconsultinginc.paraamarsh.processor.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;


public class BusinessErrorResponseHandler extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public BusinessErrorResponseHandler(String message , Status status) {
        super(ErrorConstants.BUSINESS_EXCEPTION_TYPE, message, status);
    }
}
