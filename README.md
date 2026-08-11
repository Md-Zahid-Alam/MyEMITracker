# My Finance Tracker — Version 4.5

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 4.5 updates

- Consistent teal styling in Light and Dark modes
- System default, Light, and Dark appearance choices saved locally
- Search and sorting for EMI, loan, debt, and expense records
- Clear Back header on every editable form and detail page
- Unsaved-change warning for the header arrow and Android Back button
- Bottom navigation remains hidden while a form or detail page is open

## Private local data

The app stores data on the device and supports JSON backup and restore. Before moving from an older APK signed with a different key, export a JSON backup, uninstall the old app once, install V4.4, and restore the backup.

## Build

The GitHub Actions workflow requires two repository secrets:

- `MFT_SIGNING_KEY_BASE64`
- `MFT_SIGNING_PASSWORD`

The signed APK is uploaded as the `My-Finance-Tracker-V4.5-signed` Actions artifact. Keep the permanent signing key private and backed up.
