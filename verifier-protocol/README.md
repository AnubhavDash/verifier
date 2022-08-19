# Verifier-Protocol repository

We strive for maximum independence between the verifier and the e-voting source code. However, the verifier shares the crypto-primitives and crypto-primitives-domain library containing the common cryptographic building blocks and data structures. Moreover, certain algorithms in the verifier are identical to the ones from the e-voting system. To highlight the verifier's independence, we duplicate the implementation of the shared algorithms and locate them in the verifier-protocol folder.

These algorithms (for example VerifyMixDecOffline or Factorize) are unlikely to incur significant changes over time, and we deliberately avoid a shared library for these protocol algorithms.
