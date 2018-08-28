/**
 * @author afries
 * @date Mar 3, 2015 6:05:20 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;

import java.util.List;

/**
 * Represents a public key in the ElGamal cryptosystem.
 */
public class ElGamalPublicKey {

    private final List<ZpElement> _pubKeys;

    private final Group _group;

    /**
     * Creates an ElGamal public key using the specified list of Zp group elements and the specified Zp subgroup group.
     * All of the elements in the list of Zp group elements (which forms the public key) should be members of the
     * received Zp subgroup. If this condition is not met then the behavior of the class won't be guaranteed.
     *
     * @param pubKeys
     *            The list of Zp group elements to be set as the public key.
     * @param group
     *            The Zp subgroup that this public key belongs to.
     */
    public ElGamalPublicKey(final List<ZpElement> pubKeys, final Group group) {

        validateInputs(pubKeys, group);

        _pubKeys = pubKeys;
        _group = group;
    }

    private void validateInputs(final List<ZpElement> pubKeys, final Group group) throws IllegalArgumentException {

        if ((pubKeys == null) || (pubKeys.isEmpty())) {
            throw new IllegalArgumentException("The public key must be an initialised non-empty list");
        }

        if (group == null) {
            throw new IllegalArgumentException("The group must be an initialised instance of ZpSubgroup");
        }
    }

    public List<ZpElement> getPubKeys() {
        return _pubKeys;
    }

    public Group getGroup() {
        return _group;
    }

    /**
     * Serializes this public key to a file. The generated file will contain the following data (each value is on a
     * separate line):
     * <ul>
     * <li>Group p parameter
     * <li>Group q parameter
     * <li>Group g parameter
     * <li>Public key part [0]
     * <li>Public key part [1]
     * <li>....
     * <li>Public key part [n]
     * </ul>
     *
     * @param pathOutputFile
     *            the path of the file to which this object should be serialized.
     * @throws Exception
     *             if the group is not a ZpGroup.
     */
    /*public void serializeToFile(final Path pathOutputFile) throws Exception {

        List<String> linesToBeWritten = new ArrayList<>();

        ZpGroup group = extractZpGroup(_group);

        // write the group information
        linesToBeWritten.add(group.getP().toString());
        linesToBeWritten.add(group.getOrder().toString());
        linesToBeWritten.add(group.getGenerator().toString());

        linesToBeWritten
            .addAll(_pubKeys.stream().map(element -> element.getValue().toString()).collect(Collectors.toList()));

        FileUtils.writeLines(pathOutputFile.toFile(), linesToBeWritten);
    }*/

    private ZpGroup extractZpGroup(final Group group) throws Exception {

        if (!(group instanceof ZpGroup)) {
            throw new Exception("Currently only ZpGroups are supported");
        }

        return (ZpGroup) group;
    }
}
