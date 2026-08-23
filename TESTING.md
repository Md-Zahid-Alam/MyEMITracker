# Automated quality checks

GitHub Actions runs `:app:testDebugUnitTest` before signing or packaging the release.

Current unit-test contracts cover:

- installment rounding and final-payment reconciliation;
- debt remaining balances and overpayment rejection;
- backup size and PBKDF iteration safety policies;
- Microsoft Excel OOXML worksheet ordering.

The release APK and AAB are not produced when these tests fail.
