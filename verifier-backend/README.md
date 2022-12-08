## List of executed verifications per phase

All verifications performed in the verifier are listed in the table below:

| Phase | Category     | Id  | Name of the verification                          |
|-------|--------------|-----|---------------------------------------------------|
| Setup | Completeness | 100 | VerifySetupCompleteness                           |
| Setup | Authenticity | 200 | VerifySignatureSetupComponentEncryptionParameters |
| Setup | Authenticity | 201 | VerifySignatureCantonConfig                       |
| Setup | Authenticity | 202 | VerifySignatureSetupComponentPublicKeys           |
| Setup | Authenticity | 203 | VerifySignatureControlComponentPublicKeys         |
| Setup | Authenticity | 204 | VerifySignatureSetupComponentVerificationData     |
| Setup | Authenticity | 205 | VerifySignatureControlComponentCodeShares         |
| Setup | Authenticity | 206 | VerifySignatureSetupComponentTallyData            |
| Setup | Authenticity | 207 | VerifySignatureElectionEventContext               |
| Setup | Consistency  | 300 | VerifyEncryptionGroupConsistency                  |
| Setup | Consistency  | 301 | VerifySetupFileNamesConsistency                   |
| Setup | Consistency  | 302 | VerifyCCrChoiceReturnCodesPublicKeyConsistency    |
| Setup | Consistency  | 303 | VerifyCCmElectionPublicKeyConsistency             |
| Setup | Consistency  | 304 | VerifyCcmAndCcrSchnorrProofsConsistency           |
| Setup | Consistency  | 305 | VerifyChoiceReturnCodesPublicKeyConsistency       |
| Setup | Consistency  | 306 | VerifyElectionPublicKeyConsistency                |
| Setup | Consistency  | 307 | VerifyPrimesMappingTableConsistency               |
| Setup | Consistency  | 308 | VerifyElectionEventIdConsistency                  |
| Setup | Consistency  | 309 | VerifyVerificationCardSetIdsConsistency           |
| Setup | Consistency  | 310 | VerifyFileNameVerificationCardSetIdsConsistency   |
| Setup | Consistency  | 311 | VerifyVerificationCardIdsConsistency              |
| Setup | Consistency  | 312 | VerifyTotalVotersConsistency                      |
| Setup | Consistency  | 313 | VerifyNodeIdsConsistency                          |
| Setup | Consistency  | 314 | VerifyChunkConsistency                            |
| Setup | Evidence     | 500 | VerifyEncryptionParameters                        |
| Setup | Evidence     | 501 | VerifySmallPrimeGroupMembers                      |
| Setup | Evidence     | 502 | VerifyVotingOptions                               |
| Setup | Evidence     | 503 | VerifyKeyGenerationSchnorrProofs                  |
| Setup | Evidence     | 504 | VerifyEncryptedPCCExponentiationProofs            |
| Setup | Evidence     | 505 | VerifyEncryptedCKExponentiationProofs             |
| Tally | Completeness | 100 | VerifyTallyCompleteness                           |
| Tally | Authenticity | 200 | VerifySignatureControlComponentBallotBox          |
| Tally | Authenticity | 201 | VerifySignatureControlComponentShuffle            |
| Tally | Authenticity | 202 | VerifySignatureTallyComponentShuffle              |
| Tally | Authenticity | 203 | VerifySignatureTallyComponentVotes                |
| Tally | Authenticity | 204 | VerifySignatureTallyComponentDecrypt              |
| Tally | Authenticity | 205 | VerifySignatureTallyComponentEch0110              |
| Tally | Authenticity | 206 | VerifySignatureTallyComponentEch0222              |
| Tally | Consistency  | 300 | VerifyConfirmedEncryptedVotesConsistency          |
| Tally | Consistency  | 301 | VerifyCiphertextsConsistency                      |
| Tally | Consistency  | 302 | VerifyPlaintextsConsistency                       |
| Tally | Consistency  | 303 | VerifyVerificationCardIdsConsistency              |
| Tally | Consistency  | 304 | VerifyBallotBoxIdsConsistency                     |
| Tally | Consistency  | 305 | VerifyFileNameBallotBoxIdsConsistency             |
| Tally | Consistency  | 306 | VerifyNumberConfirmedEncryptedVotesConsistency    |
| Tally | Consistency  | 307 | VerifyElectionEventIdConsistency                  |
| Tally | Consistency  | 308 | VerifyNodeIdsConsistency                          |
| Tally | Consistency  | 309 | VerifyFileNameNodeIdsConsistency                  |
| Tally | Consistency  | 310 | VerifyEncryptionGroupConsistency                  |
| Tally | Evidence     | 500 | VerifyOnlineControlComponents                     |
| Tally | Evidence     | 501 | VerifyTallyControlComponent                       |

For detailed information, please refer to the verifier
specification [document](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf).
