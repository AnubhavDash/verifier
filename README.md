# Verifier of the Swiss Post Voting System

The Swiss Post Voting System requires a verification software—the *verifier*—to verify the cryptographic evidence.
The [specification](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf) and
development of the verifier goes hand in hand with the Swiss Post Voting System, and the verifier challenges and extensively tests a protocol run.

Similar to the [e-voting solution](https://gitlab.com/swisspost-evoting/e-voting/e-voting) and
the [crypto-primitives library](https://gitlab.com/swisspost-evoting/crypto-primitives/crypto-primitives), the verifier source code follows a
[precise and unambiguous pseudo-code verifier specification](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/System/Verifier_Specification.pdf)
to bridge the representational gap between mathematics and code.

The verifier's execution must fulfill the following conditions:

* The verifier is operated by the electoral commission under the responsibility of the cantons, **not** by Swiss Post.
* The verifier instance is **offline**. The verifier receives data only via secure USB transfer.
* The machine running the verifier is hardened and has no other purpose than running the
  verifier software.

In general, the verifier heeds web application security best practices when appropriate.
However, we do not enforce authentication between the application's frontend and backend parts, and we omit HTTP security headers.
Please note that while the verifier uses web technologies for the user interface, the verifier backend accepts only local traffic.
If the adversary controls the verifier instance, he could access the internal file system, and sniffing the local HTTP traffic would be pointless.
To prevent an attacker from controlling a verifier instance, we implement the operational safeguards described above.

## Under which license is this code available?

The verifier is released under Apache 2.0.

## Code Quality

We strive for excellent code quality to minimize the risk of bugs and vulnerabilities. We rely on the following tools for code analysis.

| Tool                                    | Focus                                                                                              |
|-----------------------------------------|----------------------------------------------------------------------------------------------------|
| [SonarQube](https://www.sonarqube.org/) | Code quality and code security                                                                     |
| [JFrog X-Ray](https://jfrog.com/xray/)  | Common vulnerabilities and exposures (CVE) analysis, Open-source software (OSS) license compliance | |

## Changelog

An overview of all major changes within the published releases is available [here](CHANGELOG.md).

## E-voting Compatibility

The following table indicates the correspondence between the Verifier and E-voting system version.

| Verifier version | [E-voting](https://gitlab.com/swisspost-evoting/e-voting/e-voting) version |
|------------------|----------------------------------------------------------------------------|
| 1.2.0            | 1.1.0                                                                      |
| 1.3.0            | 1.2.0                                                                      |
| 1.3.1            | 1.2.1                                                                      |
| 1.3.2            | 1.2.2                                                                      |
| 1.3.3            | 1.2.3                                                                      |
| 1.4.0            | 1.3.0                                                                      |
| 1.4.1            | 1.3.1                                                                      |
| 1.4.2            | 1.3.2                                                                      |
| 1.4.3            | 1.3.3                                                                      |
| 1.4.4            | 1.3.4                                                                      |
| 1.5.0            | 1.4.0                                                                      |
| 1.5.1            | 1.4.1                                                                      |
| 1.5.2            | 1.4.2                                                                      |
| 1.5.3            | 1.4.3                                                                      |
| 1.5.4            | 1.4.4                                                                      |

## Build information

The following instructions provide step-by-step information to build the Verifier of the Swiss Post Voting System on a Windows machine.

1. Ensure you have Maven and Node installed. We tested with following versions:
    * OpenJDK Runtime Environment Temurin-21.0.4+7 (build 21.0.4+7)
    * Apache Maven 3.9.9
    * Node: v18.20.3

2. Build using Maven
    * `mvn clean install`

3. The generated artifact is located in verifier-assembly\target\verifier-assembly-\<VERSION>.zip.

## Run

1. Unzip the generated artifact.

2. Ensure that the verifier's keystore and the keystore's password file are located at the place indicated in the
   file [application.properties](./verifier-assembly/src/main/resources/application.properties).

3. Launch the Verifier.exe.

4. Click on the dataset upload section and select a dataset. You can find a test dataset in the [./datasets](./datasets) subfolder.

5. Click on 'Verify Setup' to run the setup verifications, or click on 'Verify Tally' to run the tally verifications.

# Verifier Test Datasets

The Verifier has two different datasets: context and tally. We provide some test datasets to test the verifier. The datasets are available in the
[`verifier-backend/target/test-classes/datasets`](verifier-backend/target/test-classes/datasets) directory once the project has been built.

### Context dataset

The following table shows the contents of the context dataset and the schemas that describe their structures.

| Description                          | Path                                                                      | Schema                                                                   |
|--------------------------------------|---------------------------------------------------------------------------|--------------------------------------------------------------------------|
| Election Event Context               | `context/electionEventContextPayload.json`                                | e-voting-libraries-domain: ElectionEventContextPayload.schema.json       |
| Election Event Configuration         | `context/configuration-anonymized.xml`                                    | e-voting-libraries-xml: evoting-config-7-0.xsd                           |
| Online Control Component Public Keys | `context/controlComponentPublicKeysPayload.${j}.json`                     | e-voting-libraries-domain: ControlComponentPublicKeysPayload.schema.json |
| Setup Component Public Keys          | `context/setupComponentPublicKeysPayload.json`                            | e-voting-libraries-domain: SetupComponentPublicKeysPayload.schema.json   |
| Setup Component Tally Data           | `context/verificationCardSets/${vcs}/setupComponentTallyDataPayload.json` | e-voting-libraries-domain: SetupComponentTallyDataPayload.schema.json    |

The context dataset is used both in the Verify Setup phase and Verify Tally phase.

### Tally dataset

The following table shows the contents of the tally dataset.

| Description                              | Path                                                                 | Schema                                                                  |
|------------------------------------------|----------------------------------------------------------------------|-------------------------------------------------------------------------|
| Control Component Ballot Box             | `tally/ballotBoxes/${bb}/controlComponentBallotBoxPayload_${j}.json` | e-voting-libraries-domain: ControlComponentBallotBoxPayload.schema.json |
| Online Control Component Shuffle         | `tally/ballotBoxes/${bb}/controlComponentShufflePayload_${j}.json`   | e-voting-libraries-domain: ControlComponentShufflePayload.schema.json   |
| Tally Control Component Shuffle          | `tally/ballotBoxes/${bb}/tallyComponentShufflePayload.json`          | e-voting-libraries-domain: TallyComponentShufflePayload.schema.json     |
| Tally Control Component Votes            | `tally/ballotBoxes/${bb}/tallyComponentVotesPayload.json`            | e-voting-libraries-domain: TallyComponentVotesPayload.schema.json       |
| Tally Control Component Detailed Results | `tally/eCH-0222.xml`                                                 | e-voting-libraries-xml: eCH-0222-1-0.xsd                                |

The tally dataset in only used in the Verify Tally phase.

## Import test password

The test datasets are encrypted and need a password to be decrypted. The password needed for decrypting the given datasets
is `LongPassword_Encryption1`.

It can be set in the application.properties file as follows:

```
import.zip.decryption.password=LongPassword_Encryption1
```
