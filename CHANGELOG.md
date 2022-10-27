# Changelog

## Release 1.2

Release 1.2 includes the following functionalities and improvements:

* Support Elections in the eCH-0110 file
* Integrate the DecodeWriteIns (including all the sub-algorithms) in the VerifyProcessPlaintexts algorithm
* Check the signature of the eCH-0222 & add it to the completeness check
* Add the verification of the eCH-0222 to the VerifyTallyFiles Algorithm
* Updated dependencies and third-party libraries.

---

## Release 1.1

Release 1.1 fixes the following known issues and incorporates feedback from the Federal Chancellery's mandated experts.
We want to thank the experts for their high-quality, constructive remarks:

* Vanessa Teague (Thinking Cybersecurity), Olivier Pereira (Université catholique Louvain), Thomas Edmund Haines (Australian National University)
* Aleksander Essex (Western University Canada)
* Rolf Haenni, Reto Koenig, Philipp Locher, Eric Dubuis (Bern University of Applied Sciences)

Release 1.1 includes the following functionalities and improvements:

* Enforced the security level EXTENDED (3072 bit modulus) in the algorithm VerifyEncryptionParameters (feedback from Aleksander Essex, Rolf Haenni, Reto Koenig, Philipp Locher, Eric Dubuis, Vanessa Teague, Olivier Pereira, and Thomas Haines).
* Implemented the verification of the proper decoding of voting options and the generation of the tally files (feedback from Rolf Haenni, Reto Koenig, Philipp Locher, and Eric Dubuis).
* Included the primes mapping table in the VerifyVotingClientProofs algorithm to ensure that all parties have a consistent view (feedback from Rolf Haenni, Reto Koenig, Philipp Locher, and Eric Dubuis).
* Implemented the verification of the Schnorr Proof algorithms in the algorithm VerifyKeyGenerationSchnorrProofs and added corresponding consistency checks (feedback Vanessa Teague, Olivier Pereira, and Thomas Haines).
* Added additional consistency checks in the VerifyTally phase (feedback from Vanessa Teague, Olivier Pereira, and Thomas Haines).
* Implemented the DecodeVotingOptions algorithm and included it in the ProcessPlaintexts algorithm (feedback from Rolf Haenni, Reto Koenig, Philipp Locher, Eric Dubuis, Vanessa Teague, Olivier Pereira, and Thomas Haines).
* Fixed the incorrect ordering when reading files from the file system (fixes [GitLab Issue #3](https://gitlab.com/swisspost-evoting/verifier/verifier/-/issues/3)).
* Improved the input validation in the method VerifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm.
* Implemented the verification of the direct trust signatures of the configuration, decryption and eCH-0110 files.
* Enforced the domain of the actual voting options.
* Migrated the JKS keystores to the standard PKCS12 keystores.
* Updated dependencies and third-party libraries.
