## List of executed verifications per phase

All verifications performed in the verifier are listed in the table below:

| Phase | Category     | Id    | Name of the verification                          |
|-------|--------------|-------|---------------------------------------------------|
| Setup | Completeness | 01.01 | VerifySetupCompleteness                           |
| Setup | Authenticity | 02.01 | VerifySignatureSetupComponentEncryptionParameters |
| Setup | Authenticity | 02.02 | VerifySignatureCantonConfig                       |
| Setup | Authenticity | 02.03 | VerifySignatureSetupComponentPublicKeys           |
| Setup | Authenticity | 02.04 | VerifySignatureControlComponentPublicKeys         |
| Setup | Authenticity | 02.05 | VerifySignatureSetupComponentVerificationData     |
| Setup | Authenticity | 02.06 | VerifySignatureControlComponentCodeShares         |
| Setup | Authenticity | 02.07 | VerifySignatureSetupComponentTallyData            |
| Setup | Authenticity | 02.08 | VerifySignatureElectionEventContext               |
| Setup | Consistency  | 03.01 | VerifyEncryptionGroupConsistency                  |
| Setup | Consistency  | 03.02 | VerifySetupFileNamesConsistency                   |
| Setup | Consistency  | 03.03 | VerifyCCrChoiceReturnCodesPublicKeyConsistency    |
| Setup | Consistency  | 03.04 | VerifyCCmElectionPublicKeyConsistency             |
| Setup | Consistency  | 03.05 | VerifyCcmAndCcrSchnorrProofsConsistency           |
| Setup | Consistency  | 03.06 | VerifyChoiceReturnCodesPublicKeyConsistency       |
| Setup | Consistency  | 03.07 | VerifyElectionPublicKeyConsistency                |
| Setup | Consistency  | 03.08 | VerifyPrimesMappingTableConsistency               |
| Setup | Consistency  | 03.09 | VerifyElectionEventIdConsistency                  |
| Setup | Consistency  | 03.10 | VerifyVerificationCardSetIdsConsistency           |
| Setup | Consistency  | 03.11 | VerifyFileNameVerificationCardSetIdsConsistency   |
| Setup | Consistency  | 03.12 | VerifyVerificationCardIdsConsistency              |
| Setup | Consistency  | 03.13 | VerifyTotalVotersConsistency                      |
| Setup | Consistency  | 03.14 | VerifyNodeIdsConsistency                          |
| Setup | Consistency  | 03.15 | VerifyChunkConsistency                            |
| Setup | Evidence     | 05.01 | VerifyEncryptionParameters                        |
| Setup | Evidence     | 05.02 | VerifySmallPrimeGroupMembers                      |
| Setup | Evidence     | 05.03 | VerifyVotingOptions                               |
| Setup | Evidence     | 05.04 | VerifyKeyGenerationSchnorrProofs                  |
| Setup | Evidence     | 05.21 | VerifyEncryptedPCCExponentiationProofs            |
| Setup | Evidence     | 05.22 | VerifyEncryptedCKExponentiationProofs             |
| Tally | Completeness | 06.01 | VerifyTallyCompleteness                           |
| Tally | Authenticity | 07.01 | VerifySignatureControlComponentBallotBox          |
| Tally | Authenticity | 07.02 | VerifySignatureControlComponentShuffle            |
| Tally | Authenticity | 07.03 | VerifySignatureTallyComponentShuffle              |
| Tally | Authenticity | 07.04 | VerifySignatureTallyComponentVotes                |
| Tally | Authenticity | 07.05 | VerifySignatureTallyComponentDecrypt              |
| Tally | Authenticity | 07.06 | VerifySignatureTallyComponentEch0110              |
| Tally | Authenticity | 07.07 | VerifySignatureTallyComponentEch0222              |
| Tally | Consistency  | 08.01 | VerifyConfirmedEncryptedVotesConsistency          |
| Tally | Consistency  | 08.02 | VerifyCiphertextsConsistency                      |
| Tally | Consistency  | 08.03 | VerifyPlaintextsConsistency                       |
| Tally | Consistency  | 08.04 | VerifyVerificationCardIdsConsistency              |
| Tally | Consistency  | 08.05 | VerifyBallotBoxIdsConsistency                     |
| Tally | Consistency  | 08.06 | VerifyFileNameBallotBoxIdsConsistency             |
| Tally | Consistency  | 08.07 | VerifyNumberConfirmedEncryptedVotesConsistency    |
| Tally | Consistency  | 08.08 | VerifyElectionEventIdConsistency                  |
| Tally | Consistency  | 08.09 | VerifyNodeIdsConsistency                          |
| Tally | Consistency  | 08.10 | VerifyFileNameNodeIdsConsistency                  |
| Tally | Consistency  | 08.11 | VerifyEncryptionGroupConsistency                  |
| Tally | Evidence     | 10.01 | VerifyOnlineControlComponents                     |
| Tally | Evidence     | 10.02 | VerifyTallyControlComponent                       |

For detailed information, please refer to the verifier
specification [document](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf).
