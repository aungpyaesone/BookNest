# BookNest Architecture

BookNest is a Kotlin Multiplatform application with shared Compose UI on Android and iOS. The foundation is local-first and read-only: it displays a Room-backed library list and book detail. Backend, synchronization, import, add, and edit flows are deliberately outside this foundation.

## Module Graph

| Module | Responsibility | Allowed project dependencies |
| --- | --- | --- |
| `:core:domain` | Typed `Result` and application errors | None |
| `:core:database` | Room database, entities, DAOs, builders, schemas | `:core:domain` only when required |
| `:core:presentation` | `UiText`, event collection, error-to-UI mapping | `:core:domain` |
| `:core:design-system` | Theme and reusable adaptive UI | None |
| `:feature:library:domain` | Book model and local data contract | `:core:domain` |
| `:feature:library:data` | Room data source and entity mappings | Library domain, core database/domain |
| `:feature:library:presentation` | MVI contracts, ViewModels, screens, navigation keys | Library domain, core presentation/design-system/domain |
| `:shared` | Navigation back stack, adaptive scene strategy, Koin composition | Core and library modules |
| `:androidApp` | Android launcher only | `:shared` |

Forbidden directions are presentation to data, core to feature, and feature to feature. Platform implementations live in `androidMain` and `iosMain` while keeping the same package structure.

## Presentation and Navigation

Each screen uses an immutable State/Action/Event contract and a Root/Screen composable split. A ViewModel may emit a navigation event, but only the `:shared` composition root mutates the back stack.

Navigation uses serializable, user-owned Navigation 3 keys. `BookNestNavigation` owns `rememberNavBackStack`, `NavDisplay`, and `rememberListDetailSceneStrategy`. Compact windows show one pane at a time. Expanded windows show the library list beside a placeholder or selected book detail. Navigation 2, `NavController`, and `NavHost` are prohibited by `architectureCheck`.

## Data and Dependency Injection

`BookLocalDataSource` is defined in library domain and exposes observable typed results. `RoomBookDataSource` is its local implementation. Room schema files are version-controlled; versions increase sequentially and destructive migration is not allowed.

Koin definitions are grouped by layer: `coreDatabaseModule`, `libraryDataModule`, and `libraryPresentationModule`. `appModules` is assembled in `:shared`; Android and iOS initialize it at their platform entry points.

## Enforcement

- `architectureCheck` validates project dependencies, domain imports, and the Navigation 3-only policy.
- `codingStandardsCheck` runs ktlint, Detekt naming checks, and resource-name validation.
- `verifyBookNest` runs architecture and coding checks, tests, the Android debug build, and iOS simulator compilation.
- CI publishes `verify-booknest`; repository branch protection should require it before merge.
