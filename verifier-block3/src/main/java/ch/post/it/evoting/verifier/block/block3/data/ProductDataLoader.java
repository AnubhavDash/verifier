package ch.post.it.evoting.verifier.block.block3.data;

import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PrivateCommitment;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import com.scytl.products.ov.mixnet.commons.tools.CiphertextTools;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHash;

import java.io.File;
import java.io.IOException;

public class ProductDataLoader {

    private final RandomOracleHash _ro;
    private final PublicCommitment[] _cPA;
    private final Exponent[][] _vecX;
    private final Exponent _rhsPA;
    private final Ciphertext _lhsME;
    private final ShuffleDataLoader _shuffleData;
    private final int _m;
    private final int _n;

    public ProductDataLoader(File mixingDirectory) throws IOException {

        _shuffleData = new ShuffleDataLoader(mixingDirectory);

        _m = _shuffleData.getReencryptedBallotsCiphertext().length;// m;
        _n = _shuffleData.getReencryptedBallotsCiphertext()[0].length;// n;

        _ro = new RandomOracleHash(_shuffleData.getCommitmentParams().getGroup().getOrder());
        _ro.addDataToRO(_shuffleData.getEncryptedBallots());
        _ro.addDataToRO(_shuffleData.getReencryptedBallots());
        _ro.addDataToRO(_shuffleData.getShuffleProof().getInitialMessage());
        final Exponent challengeX = _ro.getHash();
        _ro.addDataToRO(_shuffleData.getShuffleProof().getFirstAnswer());
        final Exponent challengeY = _ro.getHash();
        _ro.addDataToRO("1");
        final Exponent challengeZ = _ro.getHash();
        _ro.reset();

        _cPA = computeCommitment(_shuffleData.getShuffleProof().getInitialMessage(), _shuffleData.getShuffleProof().getFirstAnswer(), challengeY, challengeZ);

        _vecX = computeVecX(challengeX);
        _rhsPA = computeRightHandProductArg(challengeY, challengeZ, _vecX);
        _lhsME = computeLeftHandMEArg(_vecX);


    }

    public ShuffleDataLoader getShuffleData() {
        return _shuffleData;
    }

    public PublicCommitment[] getCPA() {
        return _cPA;
    }

    public Exponent getRhsPA() {
        return _rhsPA;
    }

    private Ciphertext computeLeftHandMEArg(final Exponent[][] vecX) {
        Ciphertext result = null;
        for (int i = 0; i < _m; i++) {
            final Ciphertext aux = CiphertextTools.compVecCiphVecExp(_shuffleData.getEncryptedBallotsCiphertext()[i], vecX[i]);
            result = (result == null ? aux : result.multiply(aux));
        }
        return result;
    }

    private Exponent computeRightHandProductArg(final Exponent challengeY, final Exponent challengeZ,
                                                final Exponent[][] vecX) {
        Exponent result = new Exponent(1, _shuffleData.getCommitmentParams().getGroup().getOrder());
        Exponent minusZ = challengeZ.negate();

        for (int i = 0; i < _m * _n; i++) {
            final Exponent factor =
                    challengeY.multiply(new Exponent(i + 1, _shuffleData.getCommitmentParams().getGroup().getOrder())).add(vecX[i / _n][i % _n]).add(minusZ);
            result = result.multiply(factor);
        }
        return result;
    }

    private Exponent[][] computeVecX(final Exponent challengeX) {
        Exponent accum = challengeX;
        final Exponent[][] result = new Exponent[_m][_n];
        for (int i = 0; i < _m; i++) {
            for (int j = 0; j < _n; j++) {
                result[i][j] = accum;
                accum = accum.multiply(challengeX);
            }
        }
        return result;
    }

    private PublicCommitment[] computeCommitment(PublicCommitment[] cA, PublicCommitment[] cB, Exponent challengeY,
                                                 Exponent challengeZ) {
        final Exponent zNegated = challengeZ.negate();

        final PublicCommitment[] cMinusZ = new PublicCommitment[_m];
        Exponent zero = new Exponent(0, _shuffleData.getCommitmentParams().getGroup().getOrder());
        final PublicCommitment minusZCommitment =
                new PrivateCommitment(zNegated, zero, _shuffleData.getCommitmentParams(), _n).makePublicCommitment();
        for (int i = 0; i < _m; i++) {
            cMinusZ[i] = minusZCommitment;
        }

        final PublicCommitment[] cD = new PublicCommitment[_m];
        for (int i = 0; i < cD.length; i++) {
            cD[i] = cA[i].exponentiate(challengeY).multiply(cB[i]);
        }

        PublicCommitment[] cPA;
        if (_m == 1) {
            cPA = new PublicCommitment[_m + 1];
        } else {
            cPA = new PublicCommitment[_m];
        }
        for (int i = 0; i < _m; i++) {
            cPA[i] = cD[i].multiply(cMinusZ[i]);
        }

        if (_m == 1) {
            Exponent identity = new Exponent(Constants.MULTIPLICATIVE_IDENTITY, _shuffleData.getCommitmentParams().getGroup().getOrder());
            cPA[_m] = new PrivateCommitment(identity, zero, _shuffleData.getCommitmentParams(), _n).makePublicCommitment();
        }
        return cPA;
    }
}
