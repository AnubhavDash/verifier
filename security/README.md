# Java Security Configuration

The BFH E-Voting group highlighted the importance of high-quality randomness in
their [report on the Swiss Post E-Voting System](https://www.bk.admin.ch/dam/bk/de/dokumente/pore/Scope%201%20Final%20Report%20BFH%2028.03.2022.pdf.download.pdf/Scope%201%20Final%20Report%20BFH%2028.03.2022.pdf)
.

The Swiss Post Voting System relies on the operating system to select a high-quality PRNG when performing cryptographic operations.
For increased auditability and to ensure that an appropriate PRNG is selected in practice, this folder contains the Java security configuration
for the Windows operating system used in the deployed system.

## Changes from the default `java.security`

The following properties have been modified compared to the default JDK `java.security` configuration:

| Property                          | Default value | Custom value | 
|-----------------------------------|---------------|--------------| 
| `policy.allowSystemProperty`      | `true`        | `false`      | 
| `security.overridePropertiesFile` | `true`        | `false`      | 

### `policy.allowSystemProperty=false`

By default, Java allows specifying an additional security policy file via the command line (`-Djava.security.auth.login.config`). Setting this
property to `false` prevents this mechanism, ensuring that an attacker or misconfigured environment cannot inject a custom policy that would weaken
the application's security posture. This guarantees that only the policy defined by the administrator applies at runtime.

### `security.overridePropertiesFile=false`

By default, Java allows appending to or completely overriding the master `java.security` configuration file via the command line
(`-Djava.security.properties`). Setting this property to `false` disables this capability, ensuring that the security configuration shipped with the
application cannot be tampered with at startup. This is critical for maintaining the integrity of the security settings — including the PRNG
configuration — and prevents any runtime override that could downgrade the cryptographic guarantees of the system.
