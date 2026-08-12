# My Finance Tracker — Version 4.8

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 4.8 updates

- “Powered by Md. Zahid Alam” displayed on the login screen
- About My Finance Tracker page added to Settings
- About page identifies Md. Zahid Alam as creator and owner
- Ownership and copyright information displayed with app version
- PDF and Excel reports include ownership footers
- Package name and backup format remain unchanged

## Private local data

The app stores data on the device and supports JSON backup and restore. Before moving from an older APK signed with a different key, export a JSON backup, uninstall the old app once, install V4.4, and restore the backup.

## Build

The GitHub Actions workflow requires two repository secrets:

- `MFT_SIGNING_KEY_BASE64`
- `MFT_SIGNING_PASSWORD`

The signed APK is uploaded as the `My-Finance-Tracker-V4.8-signed` Actions artifact. Keep the permanent signing key private and backed up.
