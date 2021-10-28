# Verifier of the Swiss Post Voting System

The Swiss Post Voting System requires a verification software—the *verifier*—to verify the cryptographic evidence. The specification and development of the verifier goes hand in hand with the Swiss Post Voting System, and the verifier challenges and extensively tests a protocol run. The Swiss Post Voting System consists of three phase, and each phase has at least one verification algorithm.

| Block                          | Phase         | Algorithm          |
|--------------------------------|---------------|--------------------|
| Pre-election verification      | Configuration | VerifyConfigPhase  |
| Ballot box verification        | Voting        | VerifyVotingPhase  |
| Mixing decryption verification | Tally         | VerifyOnlineTally  |
| Result verification            | Tally         | VerifyOfflineTally |

## Known Issues

The current version of the verifier has the following known issues:

* Consistency checks in Block 1, 2, 3 and 4 are missing.
* Authenticity checks in Block 1, 2, 3 and 4 are missing.
* Block 1: the CheckSigEncryptionParams test has yet to be adapted to the new signature format.
* Block 2: the SecureLogs are not yet verified.
* Block 2: the voting phase exponentiation proofs (partial Choice Return Codes (pCC) and confirmation key (CK)) are not yet verified
* Block 2: the extractability of the pCC and CK in the Return Codes Mapping table are not yet verified.
* Block 2: the mixnet initial payload is not yet verified.
* Block 4: the verification of the eCH-files is not yet specified in pseudo-code.



## Build information

The following guide provide step by step informations to build the Verifier Swiss Post on a Windows machine.  

1. Ensure you have Maven and Node installed. We tested with following versions:
    - OpenJDK Runtime Environment Temurin-11.0.12+7 (build 11.0.12+7)
    - Apache Maven 3.8.3 (ff8e977a158738155dc465c6a97ffaf31982d739)
    - Node: v14.17.0

2. Build using Maven
    - <code>mvn clean install</code>

3. The generated artifact is generated in verifier-assembly\target\verifier-assembly-\<VERSION>.zip

4. Unzip the generated artifact and then launch verifier.exe
