/**
 * @author afries
 * @date   Sep 16, 2015 4:44:58 PM
 *
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.exceptions;

public class InvalidInputException extends RuntimeException {

    private static final long serialVersionUID = 1093175482117334780L;

    public InvalidInputException(final String message) {
        super(message);
    }

}
