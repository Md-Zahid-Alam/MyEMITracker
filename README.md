# My Finance Tracker — Version 5.0

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 5.0 updates

- AES-256-GCM encrypted financial records protected by Android Keystore
- Password-protected encrypted backup with safe legacy JSON import
- Signed release APK and Play Store AAB workflow
- Private lock-screen payment notifications
- Financing details and encrypted supporting documents
- Payment method, channel, reference, notes, and evidence
- Separate debt-to-pay and money-to-receive views and totals
- PDF payment receipts and owed-to-me payment requests

## Private local data

The app stores encrypted data on the device. New backups use the `.mftbackup` format and a user-selected password. Old readable JSON backups can be imported through the clearly marked legacy restore path, but new plaintext backups are not created. New image/PDF attachments are included in encrypted records and backups; each document is limited to 5 MB.

## Build

The GitHub Actions workflow requires two repository secrets:

- `MFT_SIGNING_KEY_BASE64`
- `MFT_SIGNING_PASSWORD`

The workflow uploads a signed V5.0 release APK and a Play Store AAB. Keep the permanent signing key private and backed up.
