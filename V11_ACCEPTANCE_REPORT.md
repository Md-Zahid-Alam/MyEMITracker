# Version 11 acceptance report

## Architecture

- MainActivity: 137 lines and application-host responsibilities only.
- Root package: only MainActivity and the compatibility/localization foundation.
- Real namespaces: authentication, dashboard, payments, expenses, reports, presentation, navigation, repository, security and notifications.
- Data dependency rule: no Compose imports and no feature imports; enforced by unit test.
- Navigation state: validated `FinanceRoute` value and `PaymentSection` enum replace raw mutable route/section strings.
- Domain: models, calculations, validation and repository contract remain Android/Compose independent.
- Compatibility: application ID, signing config, encryption format, JSON keys and backup restore formats unchanged.

## Design system and UI/UX

- Calm Financial semantic light/dark palettes, typography, shapes, spacing and width constraints retained.
- Forms use one shared header with back navigation, premium surface treatment and explicit read-only state.
- Shared fields, dates, dropdowns, attachments, confirmation dialogs and unsaved-change handling retained.
- English/Bangla, dark mode and responsive scrolling/content constraints retained.
- No user field, button, filter, status or action was removed.

## Reports

- Renderer-independent report blocks retained.
- Dedicated receipt, payment-request, summary and detailed-document domain types added.
- Branded PDF and organized eight-sheet XLSX capabilities retained without changing export file compatibility.

## Automated release gates

- Calculation, debt-limit, backup, validation, country and OOXML tests.
- Architecture boundary and critical feature-parity tests.
- GitHub runs tests before signed APK/AAB generation.

Version 11 remains a release candidate until the GitHub workflow passes.
