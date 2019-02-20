/**
 * @author aescala
 * @date 16/09/2013 15:39:39
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.proofs.bg.commitments;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;

/**
 * This class encapsulates a set of commitment parameters, which are needed to generate {@link PrivateCommitment}.
 */
public class CommitmentParams {

    private final GroupElement _h;

    private final GroupElement[] _g;

    private final Group _group;

    private final int _commitmentlength;

    public CommitmentParams(final Group group, final int commitmentlength) {
        _group = group;
        _h = group.getRandomElement();
        _commitmentlength = commitmentlength;
        _g = group.getVectorRandomElement(_commitmentlength);
    }

    public CommitmentParams(final Group group, final GroupElement h, final GroupElement[] g) {
        _group = group;
        _h = h;
        _g = g;
        _commitmentlength = _g.length;
    }

    public GroupElement getH() {
        return _h;
    }

    public GroupElement[] getG() {
        return _g;
    }

    public Group getGroup() {
        return _group;
    }

    public int getCommitmentLength() {
        return _commitmentlength;
    }

    /**
     * Serializes the values of this CommitmentParams to a file.
     * <p>
     * The generated file will contain the following data (each value is on a separate line):
     * <ul>
     * <li>Group p parameter
     * <li>Group q parameter
     * <li>Group g parameter
     * <li>H
     * <li>G[0]
     * <li>G[i]
     * <li>...
     * <li>G[n]
     * </ul>
     * <p>
     * Note: There is a variable number of G elements.
     *
     * @param pathOutputFile
     *            the path of the file to which this object should be serialized.
     * @throws Exception
     *             if the group is not a ZpGroup.
     */
    public void serializeToFile(final Path pathOutputFile) throws Exception {

        final List<String> linesToBeWritten = new ArrayList<>();

        final ZpGroup group = extractZpGroup(_group);

        // write the group information
        linesToBeWritten.add(group.getP().toString());
        linesToBeWritten.add(group.getOrder().toString());
        linesToBeWritten.add(group.getGenerator().toString());

        // write the element information
        linesToBeWritten.add((_h.getValue()).toString());
        for (GroupElement a_g : _g) {
            linesToBeWritten.add((a_g.getValue()).toString());
        }

        FileUtils.writeLines(pathOutputFile.toFile(), linesToBeWritten);
    }

    private ZpGroup extractZpGroup(final Group group) throws Exception {

        if (!(group instanceof ZpGroup)) {
            throw new Exception("Currently only ZpGroups are supported");
        }

        return (ZpGroup) group;
    }
}
