# My Finance Tracker architecture — Version 9

The project uses one Android application module with package boundaries that follow a layered architecture.

## Layers

- `domain/model`: immutable financial records with no Android dependency.
- `domain/usecase`: pure financial rules and calculations.
- `domain/repository`: storage contracts used by presentation logic.
- `data/backup`: backup security policies.
- `export/xlsx`: independently testable OOXML rules.
- `core/designsystem`: centralized colors, typography, shapes and spacing.
- `core/country`: complete offline ISO country/currency catalog.
- `core/ui`: reusable application-level Compose controls.
- `domain/validation`: reusable pure validation rules.
- `navigation`: typed destination contracts.
- `export/model`: renderer-independent structured report documents.
- root application package: Android integration and the remaining Compose feature screens.

## Dependency direction

UI depends on domain contracts. The Android repository implements the domain repository interface. Domain code never depends on Compose, Android storage, JSON, notifications or export code.

## Compatibility guarantees

- Package name remains `com.mdzahidalam.myfinancetracker`.
- Existing encrypted local records remain readable.
- Existing encrypted and supported legacy backups remain readable.
- Model property names and JSON keys remain unchanged.
- V7 increases only the application version; it does not silently rewrite financial values.

## Continuing rule

New financial rules must be placed in `domain/usecase` and covered by unit tests. New colors, shapes and spacing must come from `core/designsystem`. Export-format rules must remain independently testable.

## Version 10 source boundaries

- `MainActivity.kt`: application/session host only (134 lines).
- `data/legacy/SecurityLayer.kt`: password hashing and local/backup encryption compatibility.
- `data/legacy/FinanceRepository.kt`: encrypted persistence, JSON serialization and backup migration.
- `data/legacy/ReminderSystem.kt`: private notification scheduling and receiver integration.
- `presentation/FinanceViewModel.kt`: application state mutation and repository coordination.
- `navigation/FinanceAppNavigation.kt`: root screen routing and backup/restore UI flow.
- `feature/authentication`: setup and unlock experiences.
- `feature/dashboard`: financial overview and recent expenses.
- `feature/payments`: separate EMI, loan, debt and expense forms plus plan details, history, receipts and requests.
- `feature/expenses`: daily/monthly expense browsing.
- `feature/reports`: report filtering, structured PDF rendering and XLSX generation.
- `core/ui`: settings and shared form/payment components.

The first Version 10 extraction deliberately keeps compatible declarations in the application package so moving files cannot change serialization, manifest component names or encrypted data behavior. Further namespace moves must be behavior-neutral and covered by the architecture and feature-parity tests.
