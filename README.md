# My Finance Tracker — Version 4.9

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 4.9 updates

- Permanent owner-specific package identity: `com.mdzahidalam.myfinancetracker`
- Kotlin namespace and source folder migrated to the owner-specific package
- Existing permanent APK signing configuration retained
- App ownership page, login ownership text, finance features, and backup format retained
- This release installs as a separate app from the former `com.example.myemitracker` package

## Private local data

The app stores data on the device and supports JSON backup and restore. Before moving from an older APK signed with a different key, export a JSON backup, uninstall the old app once, install V4.4, and restore the backup.

## Build

The GitHub Actions workflow requires two repository secrets:

- `MFT_SIGNING_KEY_BASE64`
- `MFT_SIGNING_PASSWORD`

The signed APK is uploaded as the `My-Finance-Tracker-V4.9-owner-package` Actions artifact. Keep the permanent signing key private and backed up.
