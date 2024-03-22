# Verifier Datasets

## Content

The Verifier has three different datasets: context, setup and tally.

### Context dataset

The following table shows the contents of the context dataset.

| Description                          | Path                                                                        |
|--------------------------------------|-----------------------------------------------------------------------------|
| Election Event Context               | `context/electionEventContextPayload.json`                                  |
| Election Event Configuration         | `context/configuration-anonymized.xml`                                      |
| Online Control Component Public Keys | `context/controlComponentPublicKeysPayload.${j}.json`                       |
| Setup Component Public Keys          | `context/setupComponentPublicKeysPayload.json`                              |
| Setup Component Tally Data           | `context/verification_card_sets/${vcs}/setupComponentTallyDataPayload.json` |

The context dataset is used both in the Verify Setup phase and Verify Tally phase.

### Setup dataset

The following table shows the contents of the setup dataset.

| Description                       | Path                                                                                        |
|-----------------------------------|---------------------------------------------------------------------------------------------|
| Setup Component Verification Data | `setup/verification_card_sets/${vcs}/setupComponentVerificationDataPayload.${chunkId}.json` |
| Control Component Code Shares     | `setup/verification_card_sets/${vcs}/controlComponentCodeSharesPayload.${chunkId}.json`     |

The setup dataset in only used in the Verify Setup phase.

### Tally dataset

The following table shows the contents of the tally dataset.

| Description                              | Path                                                                  |
|------------------------------------------|-----------------------------------------------------------------------|
| Control Component Ballot Box             | `tally/ballot_boxes/${bb}/controlComponentBallotBoxPayload_${j}.json` |
| Online Control Component Shuffle         | `tally/ballot_boxes/${bb}/controlComponentShufflePayload_${j}.json`   |
| Tally Control Component Shuffle          | `tally/ballot_boxes/${bb}/tallyComponentShufflePayload.json`          |
| Tally Control Component Votes            | `tally/ballot_boxes/${bb}/tallyComponentVotesPayload.json`            |
| Tally Control Component Decryptions      | `tally/evoting-decrypt.xml`                                           |
| Tally Control Component Detailed Results | `tally/eCH-0222.xml`                                                  |
| Tally Control Component Results          | `tally/eCH-0110.xml`                                                  |

The tally dataset in only used in the Verify Tally phase.

## Import password

The datasets are encrypted and need a password to be decrypted. The password needed for decrypting the given datasets
is `LongPassword_Encryption1`.

It can be set in the application.properties file as follows:

```
import.zip.decryption.password=LongPassword_Encryption1
```