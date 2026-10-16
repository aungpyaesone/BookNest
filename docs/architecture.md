# BookNest Architecture

BookNest is a Kotlin Multiplatform application with shared Compose UI on Android and iOS. The foundation is local-first: it displays a Room-backed library and a vertical reader backed by replaceable content and progress contracts. Backend, synchronization, import, add, and edit flows are deliberately outside this foundation.

## Module Graph

| Module | Responsibility | Allowed project dependencies |
| --- | --- | --- |
| `:core:domain` | Typed `Result` and application errors | None |
| `:core:database` | Room database, entities, DAOs, builders, schemas | `:core:domain` only when required |
| `:core:presentation` | `UiText`, event collection, error-to-UI mapping | `:core:domain` |
| `:core:design-system` | Theme and reusable adaptive UI | None |
| `:core:settings` | App and reader preference contracts with Android/iOS persistence | None |
| `:feature:home:presentation` | Home storefront MVI, local display models, adaptive feed, and Home Navigation 3 key | Core presentation/design-system |
| `:feature:library:domain` | Book model and local data contract | `:core:domain` |
| `:feature:library:data` | Room data source and entity mappings | Library domain, core database/domain |
| `:feature:library:presentation` | MVI contracts, ViewModels, screens, navigation keys | Library domain, core presentation/design-system/domain |
| `:feature:reader:domain` | Readable content and stable reading-progress contracts | Core domain |
| `:feature:reader:data` | Bundled demo content and Room progress persistence | Reader domain, core database/domain |
| `:feature:reader:presentation` | Vertical reader MVI, appearance controls, and Reader Navigation 3 key | Reader domain, core settings/presentation/design-system/domain |
| `:feature:settings:presentation` | Theme settings MVI and adaptive settings UI | Core settings/presentation/design-system |
| `:shared` | App shell, top-level Navigation 3 back stack, adaptive scene strategy, app theme state, Koin composition | Core and all feature modules |
| `:androidApp` | Android launcher only | `:shared` |

Forbidden directions are presentation to data, core to feature, and feature to feature. Platform implementations live in `androidMain` and `iosMain` while keeping the same package structure.

## Presentation and Navigation

Each screen uses an immutable State/Action/Event contract and a Root/Screen composable split. A ViewModel may emit a navigation event, but only the `:shared` composition root mutates the back stack. Home is a presentation-only feature while its content is local fixture data; domain and data modules are added only when a real catalog contract exists.

Navigation uses serializable, user-owned Navigation 3 keys. `BookNestNavigation` starts at `HomeKey` and owns `rememberNavBackStack`, `NavDisplay`, and `rememberListDetailSceneStrategy`. Compact and medium windows use bottom navigation; expanded windows use a navigation rail. The Library destination preserves compact one-pane and expanded list-detail behavior. Navigation 2, `NavController`, and `NavHost` are prohibited by `architectureCheck`.

`SettingsKey` is a secondary Navigation 3 destination opened from the fixed Home app bar. It does not replace the Profile destination. The Home tab remains selected while Settings is open, Back returns to Home, and selecting another top-level destination replaces the secondary stack.

`ReaderKey(bookId)` is a secondary destination opened from Home Continue Reading or Library Book Detail. Reader screens hide the top-level bottom bar and navigation rail, retain their source destination on the user-owned back stack, and load content by identifier through `BookContentDataSource`.

`NavDisplay` stays in a stable full-window container. `BookNestChromeSceneStrategy` wraps each calculated scene with its own app chrome and safe-area treatment, including the complete adaptive Library scene. Chrome derives from entry metadata, not the current global stack top, so an outgoing Reader retains its fullscreen viewport while Home returns. Scene keys include the underlying scene type and key to preserve Navigation 3 transitions.

The design system owns BookNest semantic color schemes, Noto Sans Myanmar typography, spacing, shapes, touch-target sizes, and reusable branded controls. Home uses a single-column mobile feed below 600dp, a two-column medium layout from 600dp, and expanded navigation plus a four-column book grid from 840dp.

Theme preference defaults to Light, supports Light/Dark/System, and is persisted through `ThemeLocalDataSource` using SharedPreferences on Android and NSUserDefaults on iOS. The app composition root observes this state so a setting change updates every destination without rebuilding navigation state.

## Data and Dependency Injection

`BookLocalDataSource` is defined in library domain and exposes observable typed results. `RoomBookDataSource` is its local implementation. Reader content uses `BookContentDataSource`; the development implementation provides an original Burmese demo book and can later be replaced by an EPUB/PDF implementation without changing presentation. `ReadingProgressLocalDataSource` stores stable chapter and paragraph identifiers in Room. Room schema files are version-controlled; versions increase sequentially and destructive migration is not allowed.

Koin definitions are grouped by layer: `coreDatabaseModule`, `coreSettingsModule`, feature presentation/data modules, and `appPresentationModule`. `appModules` is assembled in `:shared`; Android and iOS initialize it at their platform entry points.

## Enforcement

- `architectureCheck` validates project dependencies, domain imports, and the Navigation 3-only policy.
- `codingStandardsCheck` runs ktlint, Detekt naming checks, and resource-name validation.
- `verifyBookNest` runs architecture and coding checks, tests, the Android debug build, and iOS simulator compilation.
- CI publishes `verify-booknest`; repository branch protection should require it before merge.
