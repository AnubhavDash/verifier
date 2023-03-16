## List of executed verifications per phase

All verifications performed in the verifier are listed in the table below:

| Phase | Category     | Id  | Spec ID | Name of the verification |
|-------|--------------|-----|---------|--------------------------|
| Setup | Completeness | 100 | -       | VerifySetupCompleteness  |
| Setup | Authenticity | 200 | 2.01    | VerifySignatureSetupComponentEncryptionParameters |
| Setup | Authenticity | 201 | 2.02    | VerifySignatureCantonConfig                      |
| Setup | Authenticity | 202 | 2.03    | VerifySignatureSetupComponentPublicKeys          |
| Setup | Authenticity | 203 | 2.04    | VerifySignatureControlComponentPublicKeys         |
| Setup | Authenticity | 204 | 2.05    | VerifySignatureSetupComponentVerificationData     |
| Setup | Authenticity | 205 | 2.06    | VerifySignatureControlComponentCodeShares         |
| Setup | Authenticity | 206 | 2.07    | VerifySignatureSetupComponentTallyData            |
| Setup | Authenticity | 207 | 2.08    | VerifySignatureElectionEventContext               |
| Setup | Consistency  | 300 | 3.01    | VerifyEncryptionGroupConsistency                  |
| Setup | Consistency  | 301 | 3.02    | VerifySetupFileNamesConsistency                   |
| Setup | Consistency  | 302 | 3.03    | VerifyCCrChoiceReturnCodesPublicKeyConsistency    |
| Setup | Consistency  | 303 | 3.04    | VerifyCCmElectionPublicKeyConsistency             |
| Setup | Consistency  | 304 | 3.05    | VerifyCcmAndCcrSchnorrProofsConsistency           |
| Setup | Consistency  | 305 | 3.06    | VerifyChoiceReturnCodesPublicKeyConsistency       |
| Setup | Consistency  | 306 | 3.07    | VerifyElectionPublicKeyConsistency                |
| Setup | Consistency  | 307 | 3.08    | VerifyPrimesMappingTableConsistency               |
| Setup | Consistency  | 308 | 3.09    | VerifyElectionEventIdConsistency                  |
| Setup | Consistency  | 309 | 3.10    | VerifyVerificationCardSetIdsConsistency           |
| Setup | Consistency  | 310 | 3.11    | VerifyFileNameVerificationCardSetIdsConsistency   |
| Setup | Consistency  | 311 | 3.12    | VerifyVerificationCardIdsConsistency              |
| Setup | Consistency  | 312 | 3.13    | VerifyTotalVotersConsistency                      |
| Setup | Consistency  | 313 | 3.14    | VerifyNodeIdsConsistency                          |
| Setup | Consistency  | 314 | 3.15    | VerifyChunkConsistency                            |
| Setup | Evidence     | 500 | 5.01    | VerifyEncryptionParameters                        |
| Setup | Evidence     | 501 | 5.02    | VerifySmallPrimeGroupMembers                      |
| Setup | Evidence     | 502 | 5.03    | VerifyVotingOptions                               |
| Setup | Evidence     | 503 | 5.04    | VerifyKeyGenerationSchnorrProofs                  |
| Setup | Evidence     | 504 | 5.21    | VerifyEncryptedPCCExponentiationProofs            |
| Setup | Evidence     | 505 | 5.22    | VerifyEncryptedCKExponentiationProofs             |
| Tally | Completeness | 100 | -       | VerifyTallyCompleteness                           |
| Tally | Authenticity | 200 | 7.01    | VerifySignatureControlComponentBallotBox          |
| Tally | Authenticity | 201 | 7.02    | VerifySignatureControlComponentShuffle            |
| Tally | Authenticity | 202 | 7.03    | VerifySignatureTallyComponentShuffle              |
| Tally | Authenticity | 203 | 7.04    | VerifySignatureTallyComponentVotes                |
| Tally | Authenticity | 204 | 7.05    | VerifySignatureTallyComponentDecrypt              |
| Tally | Authenticity | 205 | 7.06    | VerifySignatureTallyComponentEch0110              |
| Tally | Authenticity | 206 | 7.07    | VerifySignatureTallyComponentEch0222              |
| Tally | Consistency  | 300 | 8.01    | VerifyConfirmedEncryptedVotesConsistency          |
| Tally | Consistency  | 301 | 8.02    | VerifyCiphertextsConsistency                      |
| Tally | Consistency  | 302 | 8.03    | VerifyPlaintextsConsistency                       |
| Tally | Consistency  | 303 | 8.04    | VerifyVerificationCardIdsConsistency              |
| Tally | Consistency  | 304 | 8.05    | VerifyBallotBoxIdsConsistency                     |
| Tally | Consistency  | 305 | 8.06    | VerifyFileNameBallotBoxIdsConsistency             |
| Tally | Consistency  | 306 | 8.07    | VerifyNumberConfirmedEncryptedVotesConsistency    |
| Tally | Consistency  | 307 | 8.08    | VerifyElectionEventIdConsistency                  |
| Tally | Consistency  | 308 | 8.09    | VerifyNodeIdsConsistency                          |
| Tally | Consistency  | 309 | 8.10    | VerifyFileNameNodeIdsConsistency                  |
| Tally | Consistency  | 310 | 8.11    | VerifyEncryptionGroupConsistency                  |
| Tally | Evidence     | 500 | 10.01   | VerifyOnlineControlComponents                     |
| Tally | Evidence     | 501 | 10.02   | VerifyTallyControlComponent                       |

For detailed information, please refer to the verifier
specification [document](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf).
