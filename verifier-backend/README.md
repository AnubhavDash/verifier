## List of executed verifications per phase

All verifications performed in the verifier are listed in the table below:

| Phase | Category     | Id    | Name of the verification                        |
|-------|--------------|-------|-------------------------------------------------|
| Setup | Completeness | 01.01 | VerifySetupCompleteness                         |
| Setup | Authenticity | 02.01 | VerifySignatureCantonConfig                     |
| Setup | Authenticity | 02.02 | VerifySignatureSetupComponentPublicKeys         |
| Setup | Authenticity | 02.03 | VerifySignatureControlComponentPublicKeys       |
| Setup | Authenticity | 02.04 | VerifySignatureSetupComponentTallyData          |
| Setup | Authenticity | 02.05 | VerifySignatureElectionEventContext             |
| Setup | Consistency  | 03.01 | VerifyEncryptionGroupConsistency                |
| Setup | Consistency  | 03.02 | VerifyNodeIdsConsistency                        |
| Setup | Consistency  | 03.03 | VerifyFileNameNodeIdsConsistency                |
| Setup | Consistency  | 03.04 | VerifyElectionEventIdConsistency                |
| Setup | Consistency  | 03.05 | VerifyVerificationCardSetIdsConsistency         |
| Setup | Consistency  | 03.06 | VerifyFileNameVerificationCardSetIdsConsistency |
| Setup | Consistency  | 03.07 | VerifyVerificationCardIdsConsistency            |
| Setup | Consistency  | 03.08 | VerifyCCRChoiceReturnCodesPublicKeyConsistency  |
| Setup | Consistency  | 03.09 | VerifyCCMElectionPublicKeyConsistency           |
| Setup | Consistency  | 03.10 | VerifyCCMAndCCRSchnorrProofsConsistency         |
| Setup | Consistency  | 03.11 | VerifyChoiceReturnCodesPublicKeyConsistency     |
| Setup | Consistency  | 03.12 | VerifyElectionPublicKeyConsistency              |
| Setup | Consistency  | 03.13 | VerifyPrimesMappingTableConsistency             |
| Setup | Consistency  | 03.14 | VerifyTotalVotersConsistency                    |
| Setup | Evidence     | 05.01 | VerifyEncryptionParameters                      |
| Setup | Evidence     | 05.02 | VerifySmallPrimeGroupMembers                    |
| Setup | Evidence     | 05.03 | VerifyVotingOptions                             |
| Setup | Evidence     | 05.04 | VerifySchnorrProofs                             |
| Tally | Completeness | 06.01 | VerifyTallyCompleteness                         |
| Tally | Authenticity | 07.01 | VerifySignatureControlComponentBallotBox        |
| Tally | Authenticity | 07.02 | VerifySignatureControlComponentShuffle          |
| Tally | Authenticity | 07.03 | VerifySignatureTallyComponentShuffle            |
| Tally | Authenticity | 07.04 | VerifySignatureTallyComponentVotes              |
| Tally | Authenticity | 07.05 | VerifySignatureTallyComponentEch0222            |
| Tally | Consistency  | 08.01 | VerifyEncryptionGroupConsistency                |
| Tally | Consistency  | 08.02 | VerifyNodeIdsConsistency                        |
| Tally | Consistency  | 08.03 | VerifyFileNameNodeIdsConsistency                |
| Tally | Consistency  | 08.04 | VerifyElectionEventIdConsistency                |
| Tally | Consistency  | 08.05 | VerifyBallotBoxIdsConsistency                   |
| Tally | Consistency  | 08.06 | VerifyFileNameBallotBoxIdsConsistency           |
| Tally | Consistency  | 08.07 | VerifyVerificationCardIdsConsistency            |
| Tally | Consistency  | 08.08 | VerifyConfirmedEncryptedVotesConsistency        |
| Tally | Consistency  | 08.09 | VerifyCiphertextsConsistency                    |
| Tally | Consistency  | 08.10 | VerifyPlaintextsConsistency                     |
| Tally | Consistency  | 08.11 | VerifyNumberConfirmedEncryptedVotesConsistency  |
| Tally | Evidence     | 10.01 | VerifyOnlineControlComponents                   |
| Tally | Evidence     | 10.02 | VerifyTallyControlComponent                     |

For detailed information, please refer to the verifier
specification [document](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf).
