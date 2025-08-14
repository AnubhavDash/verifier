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

## Security Considerations

The verifier operates within the cantonal infrastructure under [strict operational and procedural security](https://gitlab.com/swisspost-evoting/e-voting/e-voting-documentation/-/blob/master/Operations/Recommendation_Safety_Measures_Cantonal_Infrastructure.md?ref_type=heads). For example, it runs on a hardened, offline machine under a strict four-eyes principle.

## E-voting Compatibility

The following table indicates the correspondence between the Verifier and E-voting system version.

| Verifier version | [E-voting](https://gitlab.com/swisspost-evoting/e-voting/e-voting) version |
|------------------|----------------------------------------------------------------------------|
| 1.5.0            | 1.4.0                                                                      |
| 1.5.1            | 1.4.1                                                                      |
| 1.5.2            | 1.4.2                                                                      |
| 1.5.3            | 1.4.3                                                                      |
| 1.5.4            | 1.4.4                                                                      |
| 1.5.5            | 1.4.5                                                                      |
| 1.6.0            | 1.5.0                                                                      |
| 1.6.1            | 1.5.1                                                                      |

## Build information

The following instructions provide step-by-step information to build the Verifier of the Swiss Post Voting System on a Windows machine.

1. Ensure you have Maven and Node installed. We tested with following versions:
   * OpenJDK Runtime Environment Temurin-21.0.8+9 (build 21.0.8+9)
    * Apache Maven 3.9.11
    * Node: v20.19.1

2. Build using Maven
    * `mvn clean install`

3. The generated artifact is located in verifier-assembly\target\verifier-assembly-\<VERSION>.zip.

## Run

1. Unzip the generated artifact.

2. Ensure that the verifier's keystore and the keystore's password file are located at the place indicated in the
   file [application.yaml](./verifier-assembly/src/main/resources/application.yaml).

3. Launch the Verifier.exe.

4. Click on the dataset upload section and select a dataset. See below on how to obtain an encrypted dataset.

5. Click on 'Verify Setup' to run the setup verifications, or click on 'Verify Tally' to run the tally verifications.

## Protocol Algorithms

The implementation of some protocol algorithms is tested with JSON files.
These files are available in the
[`verifier-backend/target/test-classes/protocol-algorithms/json`](verifier-backend/target/test-classes/protocol-algorithms/json)
directory once the project has been built.
Ensure that the tests are executed only after an initial build.

## Verifier Test Datasets

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
| Tally Control Component Detailed Results | `tally/eCH-0222.xml`                                                 | e-voting-libraries-xml: eCH-0222-3-0.xsd                                |

The tally dataset is only used in the Verify Tally phase.

### Encryption

The provided datasets are not encrypted. To be able to use them in the Verifier, they need to be encrypted.

The File Cryptor Tool provides such functionality to encrypt these datasets using a password. To know more about this tool, please refer to
the [File Cryptor Tool README](https://gitlab.com/swisspost-evoting/e-voting/e-voting/-/blob/master/tools/file-cryptor/README.md).

You can use any password to encrypt the datasets as long as it respects the `import.zip.decryption.password` policy defined in
the [application.yml](verifier-backend/src/main/resources/application.yaml).

For example, if you want to use the password `LongPassword_Encryption1` to encrypt the D2/context.zip, you need to add the following line to the
`application.yaml` file:

```
import.zip.decryption.password=LongPassword_Encryption1
```

And encrypt the datasets using the File Cryptor Tool with the password `LongPassword_Encryption1`:

```bash
java 
-Dmode=ENCRYPT
-Dpassword=LongPassword_Encryption1 
-Dsource.file-path=<path-to-verifier>\\verifier-backend\\target\\test-classes\\datasets\\D2\\context.zip 
-Dtarget.file-path=<path-to-verifier>\\verifier-backend\\target\\test-classes\\datasets\\D2\\encrypted-context.zip 
-jar file-cryptor-<VERSION>-runnable.jar
```
