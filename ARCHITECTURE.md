# My Finance Tracker architecture

The project uses one Android application module with package boundaries that follow a layered architecture.

## Layers

- `domain/model`: immutable financial records with no Android dependency.
- `domain/usecase`: pure financial rules and calculations.
- `domain/repository`: storage contracts used by presentation logic.
- `data/backup`: backup security policies.
- `export/xlsx`: independently testable OOXML rules.
- `core/designsystem`: centralized colors, typography, shapes and spacing.
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
