# Verifier Datasets

## Content

The Verifier has two different datasets: context and tally.

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

## Import password

The datasets are encrypted and need a password to be decrypted. The password needed for decrypting the given datasets
is `LongPassword_Encryption1`.

It can be set in the application.properties file as follows:

```
import.zip.decryption.password=LongPassword_Encryption1
```