/**
 * $Id$
 * @author aescala
 * @date   29/10/2013 16:47:28
 *
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 *
 * All rights reserved.
 *
 */
package com.scytl.products.ov.mixnet.commons.tools;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

/**
 *
 */
public class RandomOracleHash {

	private final List<Object> objects = new LinkedList<>();

	private final BigInteger groupOrder;

	public RandomOracleHash(final BigInteger groupOrder) {
		this.groupOrder = groupOrder;
	}

	public void addDataToRO(final Object o) {
		objects.add(o);
	}

	public void addDataToRO(final Object[] o) {
		for (Object object : o) {
			addDataToRO(object);
		}
	}

	public void addDataToRO(final Object[][] o) {
		for (Object[] object : o) {
			addDataToRO(object);
		}
	}

	public Exponent getHash() {
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			for (Object object : objects) {
				//System.out.println(object.toString()+"\n");
				digest.update(object.toString().getBytes(UTF_8));
			}
			BigInteger value = new BigInteger(1, digest.digest());
			return new Exponent(value, groupOrder);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Failed to get hash.", e);
		}

	}

	public void reset() {
		objects.clear();
	}
}
