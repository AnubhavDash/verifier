## List of executed verifications per phase

All verifications performed in the verifier are listed in the table below:

| Phase | Id  | Name of the verification                       |
|-------|-----|------------------------------------------------|
| Setup | 100 | VerifySetupCompleteness                        |
| Setup | 200 | CheckSignatureEncryptionParameters             |
| Setup | 201 | CheckSignatureElectionEventContextData         |
| Setup | 202 | CheckSignatureOnlineCCKeys                     |
| Setup | 203 | CheckSignatureVerificationData                 |
| Setup | 204 | CheckSignatureEncryptedCodeShares              |
| Setup | 205 | CheckSignatureTallyData                        |
| Setup | 300 | VerifyEncryptionGroupConsistency               |
| Setup | 301 | VerifyCCrChoiceReturnCodesPublicKeyConsistency |
| Setup | 302 | VerifyCCmElectionPublicKeyConsistency          |
| Setup | 303 | VerifyChoiceReturnCodesPublicKeyConsistency    |
| Setup | 304 | VerifyElectionPublicKeyConsistency             |
| Setup | 305 | VerifyPrimesMappingTableConsistency            |
| Setup | 306 | VerifyElectionEventIdConsistency               |
| Setup | 307 | VerifyVerificationCardSetIdsConsistency        |
| Setup | 308 | VerifyVerificationCardIdsConsistency           |
| Setup | 309 | VerifyVerificationCardSetsConsistency          |
| Setup | 310 | VerifyChunkConsistency                         |
| Setup | 311 | VerifyNodeIdsConsistency                       |
| Setup | 500 | VerifyEncryptionParameters                     |
| Setup | 501 | VerifySmallPrimeGroupMembers                   |
| Setup | 502 | VerifyVotingOptions                            |
| Setup | 503 | VerifyEncryptedPCCExponentiationProofs         |
| Setup | 504 | VerifyEncryptedCKExponentiationProofs          |
| Tally | 100 | VerifyTallyCompleteness                        |
| Tally | 200 | CheckSignatureBallotBox                        |
| Tally | 201 | CheckSignatureOnlineShuffle                    |
| Tally | 202 | CheckSignatureOfflineShuffle                   |
| Tally | 203 | CheckSignatureProcessedVotes                   |
| Tally | 300 | VerifyConfirmedEncryptedVotesConsistency       |
| Tally | 301 | VerifyCiphertextsConsistency                   |
| Tally | 302 | VerifyPlaintextsConsistency                    |
| Tally | 303 | VerifyBallotBoxesConsistency                   |
| Tally | 304 | VerifyNumberConfirmedEncryptedVotesConsistency |
| Tally | 305 | VerifyEncryptionGroupConsistency               |
| Tally | 306 | VerifyTallyNodeIdsConsistency                  |
| Tally | 307 | VerifyVotingCardIdsConsistency                 |
| Tally | 500 | VerifyOnlineControlComponents                  |
| Tally | 501 | VerifyTallyControlComponent                    |

For detailed information, please refer to the verifier specification [document](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf).
