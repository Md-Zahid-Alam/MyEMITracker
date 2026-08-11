# My Finance Tracker — Version 4.6

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 4.6 updates

- Enlarged adaptive launcher icon without the white double-padding effect
- Search stays hidden until the header Search icon is selected
- Sort choices open from a compact header icon
- Compact Payments, EMI/Loan/Debt, and status tabs
- Tappable payment-plan cards with no redundant Open button
- Edit, Reopen/Restore, Archive, and Delete actions under the card’s three-dot menu
- Compact expense month navigation and collapsible category summary
- Existing teal themes, safe Back behavior, and permanent APK signing retained

## Private local data

The app stores data on the device and supports JSON backup and restore. Before moving from an older APK signed with a different key, export a JSON backup, uninstall the old app once, install V4.4, and restore the backup.

## Build

The GitHub Actions workflow requires two repository secrets:

- `MFT_SIGNING_KEY_BASE64`
- `MFT_SIGNING_PASSWORD`

The signed APK is uploaded as the `My-Finance-Tracker-V4.6-signed` Actions artifact. Keep the permanent signing key private and backed up.
