# My Finance Tracker — Version 4.4

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 4.4 updates

- Permanent APK signing support for reliable in-place updates
- New teal wallet app logo and refreshed login screen
- Add button appears only on the main Payments and Expenses pages
- Completed and archived plans open in view-only mode
- Confirmation warnings for edit, update, undo paid, archive, restore, and reopen actions
- Improved Completed filter sizing and light teal selected controls
- Forms hide the bottom navigation to reduce accidental navigation

## Private local data

The app stores data on the device and supports JSON backup and restore. Before moving from an older APK signed with a different key, export a JSON backup, uninstall the old app once, install V4.4, and restore the backup.

## Build

The GitHub Actions workflow requires two repository secrets:

- `MFT_SIGNING_KEY_BASE64`
- `MFT_SIGNING_PASSWORD`

The signed APK is uploaded as the `My-Finance-Tracker-V4.4-signed` Actions artifact. Keep the permanent signing key private and backed up.
