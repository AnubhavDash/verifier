## List of executed verifications per phase

All verifications performed in the verifier are listed in the table below:

| Phase | Category     | Id    | Name of the verification                          |
|-------|--------------|-------|---------------------------------------------------|
| Setup | Completeness | 1.01  | VerifySetupCompleteness                           |
| Setup | Authenticity | 2.01  | VerifySignatureSetupComponentEncryptionParameters |
| Setup | Authenticity | 2.02  | VerifySignatureCantonConfig                       |
| Setup | Authenticity | 2.03  | VerifySignatureSetupComponentPublicKeys           |
| Setup | Authenticity | 2.04  | VerifySignatureControlComponentPublicKeys         |
| Setup | Authenticity | 2.05  | VerifySignatureSetupComponentVerificationData     |
| Setup | Authenticity | 2.06  | VerifySignatureControlComponentCodeShares         |
| Setup | Authenticity | 2.07  | VerifySignatureSetupComponentTallyData            |
| Setup | Authenticity | 2.08  | VerifySignatureElectionEventContext               |
| Setup | Consistency  | 3.01  | VerifyEncryptionGroupConsistency                  |
| Setup | Consistency  | 3.02  | VerifySetupFileNamesConsistency                   |
| Setup | Consistency  | 3.03  | VerifyCCrChoiceReturnCodesPublicKeyConsistency    |
| Setup | Consistency  | 3.04  | VerifyCCmElectionPublicKeyConsistency             |
| Setup | Consistency  | 3.05  | VerifyCcmAndCcrSchnorrProofsConsistency           |
| Setup | Consistency  | 3.06  | VerifyChoiceReturnCodesPublicKeyConsistency       |
| Setup | Consistency  | 3.07  | VerifyElectionPublicKeyConsistency                |
| Setup | Consistency  | 3.08  | VerifyPrimesMappingTableConsistency               |
| Setup | Consistency  | 3.09  | VerifyElectionEventIdConsistency                  |
| Setup | Consistency  | 3.10  | VerifyVerificationCardSetIdsConsistency           |
| Setup | Consistency  | 3.11  | VerifyFileNameVerificationCardSetIdsConsistency   |
| Setup | Consistency  | 3.12  | VerifyVerificationCardIdsConsistency              |
| Setup | Consistency  | 3.13  | VerifyTotalVotersConsistency                      |
| Setup | Consistency  | 3.14  | VerifyNodeIdsConsistency                          |
| Setup | Consistency  | 3.15  | VerifyChunkConsistency                            |
| Setup | Evidence     | 5.01  | VerifyEncryptionParameters                        |
| Setup | Evidence     | 5.02  | VerifySmallPrimeGroupMembers                      |
| Setup | Evidence     | 5.03  | VerifyVotingOptions                               |
| Setup | Evidence     | 5.04  | VerifyKeyGenerationSchnorrProofs                  |
| Setup | Evidence     | 5.21  | VerifyEncryptedPCCExponentiationProofs            |
| Setup | Evidence     | 5.22  | VerifyEncryptedCKExponentiationProofs             |
| Tally | Completeness | 6.01  | VerifyTallyCompleteness                           |
| Tally | Authenticity | 7.01  | VerifySignatureControlComponentBallotBox          |
| Tally | Authenticity | 7.02  | VerifySignatureControlComponentShuffle            |
| Tally | Authenticity | 7.03  | VerifySignatureTallyComponentShuffle              |
| Tally | Authenticity | 7.04  | VerifySignatureTallyComponentVotes                |
| Tally | Authenticity | 7.05  | VerifySignatureTallyComponentDecrypt              |
| Tally | Authenticity | 7.06  | VerifySignatureTallyComponentEch0110              |
| Tally | Authenticity | 7.07  | VerifySignatureTallyComponentEch0222              |
| Tally | Consistency  | 8.01  | VerifyConfirmedEncryptedVotesConsistency          |
| Tally | Consistency  | 8.02  | VerifyCiphertextsConsistency                      |
| Tally | Consistency  | 8.03  | VerifyPlaintextsConsistency                       |
| Tally | Consistency  | 8.04  | VerifyVerificationCardIdsConsistency              |
| Tally | Consistency  | 8.05  | VerifyBallotBoxIdsConsistency                     |
| Tally | Consistency  | 8.06  | VerifyFileNameBallotBoxIdsConsistency             |
| Tally | Consistency  | 8.07  | VerifyNumberConfirmedEncryptedVotesConsistency    |
| Tally | Consistency  | 8.08  | VerifyElectionEventIdConsistency                  |
| Tally | Consistency  | 8.09  | VerifyNodeIdsConsistency                          |
| Tally | Consistency  | 8.10  | VerifyFileNameNodeIdsConsistency                  |
| Tally | Consistency  | 8.11  | VerifyEncryptionGroupConsistency                  |
| Tally | Evidence     | 10.01 | VerifyOnlineControlComponents                     |
| Tally | Evidence     | 10.02 | VerifyTallyControlComponent                       |

For detailed information, please refer to the verifier
specification [document](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf).
