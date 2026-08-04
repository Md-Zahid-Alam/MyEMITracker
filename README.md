# My Finance Tracker — Version 3

Offline/local Android finance tracker.

## Included

- App password lock with PBKDF2-HMAC-SHA256 protected credential
- Optional fingerprint/biometric unlock
- EMI tracking for phones, electronics, appliances, vehicles, furniture and other items
- EMI interest amount/rate field
- Financed amount
- Previous installment support
- Payment history and progress
- Custom reminder days
- Separate loan tracking
- Separate debt tracking
- Local JSON backup/restore
- Local PDF reports
- No Supabase/Firebase/cloud account in Version 3

## Build

Use GitHub Actions workflow:

`.github/workflows/build-apk.yml`

It runs:

`gradle :app:assembleDebug --no-daemon`

The APK is uploaded as an Actions artifact.

## Important

Version 3 is intentionally offline. Version 4 can add account/cloud synchronization later.
