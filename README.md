# My Finance Tracker — Version 8.0

Offline Android finance tracker for EMI plans, loans, debts, payments, and daily expenses.

## Version 8.0 updates

- Added the Calm Financial premium light/dark color system and a complete typography hierarchy.
- Redesigned Home with a monthly-spending hero, clearer commitments and refined recent expenses.
- Redesigned Payments and Settings navigation cards with stronger visual hierarchy.
- Improved form headers, fields, dropdowns, date controls, spacing and large-screen width handling.
- Removed the redundant global app-name bar so every page has more usable space.
- Preserved encrypted records, attachments, financial calculations and backup compatibility.

Version 8.0 retains the clean-architecture foundation and automated unit tests introduced in Version 7.0.

See `ARCHITECTURE.md`, `DESIGN_SYSTEM.md` and `TESTING.md`.

## Earlier Version 6.1 updates

- Completed a full English/বাংলা user-interface text audit.
- Replaced mixed word-by-word translations with natural full-phrase Bangla translations.
- Localized forms, filters, empty states, confirmations, validation warnings, payment screens, settings, reports, receipts, requests, and Excel headings.
- Retained all Version 6.0 fixes, including Microsoft Excel desktop compatibility and dark-mode contrast.

## Earlier Version 6.0 updates

- English and বাংলা can be selected during first setup, on login, and in Settings.
- Bangladesh defaults to BDT and includes a compact built-in bank/mobile-banking list.
- International users select their country and currency and save only the banks/providers they use.
- Country or currency changes display a warning because existing amounts are not converted.
- PDF and Excel report labels follow the selected app language and currency.

## Earlier Version 5.4 updates

- Debts remains one Payments-page section and expands to separate Money I Owe and Money Owed to Me pages.
- Excel export is now a genuine `.xlsx` OpenXML workbook instead of a legacy single-column `.xls` file.
- Separate Summary, EMI, Loans, Debts, Expenses, and Payments worksheets.
- Teal styled headers, frozen top rows, filters, useful column widths, numeric amount cells, and structured fields.
- Workbook export respects the filters currently applied on the Reports page.

- Separate Money I Owe and Money Owed to Me pages with direction fixed during new record creation.
- Reusable method-specific payment fields across payment requests and recorded payments.
- Mobile banking provider selection and searchable offline Bangladeshi bank selection.
- Structured account, branch, routing, reference, cheque, salary and card-detail validation.
- Compact single-row Payment Request actions.
- Searchable and filterable Reports with period, type, status and sorting controls.
- Collapsible report results plus Summary PDF, Detailed PDF and filtered Excel exports.

- Strict debt overpayment protection and completed-debt payment blocking.
- Payment requests support partial payment, paid and cancelled states, editing, confirmed cancellation and request-linked received payments.
- Read-only Plan Information is available for active, completed and archived EMI, Loan and Debt records.
- Optional encrypted signature image in Receipt Profile, displayed on generated receipt and request PDFs.
- Validated receipt-profile phone and email fields, plus broader form limits and date checks.
- Compact payment-history actions with visible Save PDF and Share options.

- Separate EMI, Loan, and Debt list pages with focused plan overview, payment, history, and document screens.
- Debt payments can be recorded with an earlier payment date.
- Expense attachments can be opened directly from the expense card.
- Professional branded PDF layouts for receipts, requests, and reports.
- Stronger validation for names, amounts, interest, installments, payment methods, and dates.

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

The workflow uploads a signed V8.0 release APK and a Play Store AAB. Keep the permanent signing key private and backed up.
