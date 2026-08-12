# My Finance Tracker — Version 4.7

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 4.7 updates

- Compact pill tabs with a clear light-teal selected state
- Payment cards match the approved compact preview
- Three-dot actions moved into the card’s top-right corner
- Paid count, remaining amount, progress percentage, and next date use compact rows
- Direct month and year selection by tapping the expense period
- All Months remains available in the period selector
- Calendar action jumps directly to one specific expense date
- Existing themes, confirmations, adaptive icon, and permanent signing retained

## Private local data

The app stores data on the device and supports JSON backup and restore. Before moving from an older APK signed with a different key, export a JSON backup, uninstall the old app once, install V4.4, and restore the backup.

## Build

The GitHub Actions workflow requires two repository secrets:

- `MFT_SIGNING_KEY_BASE64`
- `MFT_SIGNING_PASSWORD`

The signed APK is uploaded as the `My-Finance-Tracker-V4.7-signed` Actions artifact. Keep the permanent signing key private and backed up.
