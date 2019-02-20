/**
 * @author afries
 * @date   Sep 16, 2015 5:11:54 PM
 *
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.exceptions;

public class VerifierException extends RuntimeException {

    private static final long serialVersionUID = 1098989592637986287L;

    public VerifierException(final String message) {
        super(message);
    }

    public VerifierException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public VerifierException(final Throwable cause) {
        super(cause);
    }
}
