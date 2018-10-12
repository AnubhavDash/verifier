/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block2.secureLog;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@Setter
public class SecureLogMetadata {
    private String sg;
    private String lsk;
    private String esk;
    private String hmac;
    private String phmac;
    private String ls;
    private String tl;
    private String ts;

}
