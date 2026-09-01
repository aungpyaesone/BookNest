<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **BookNest** (0 symbols, 0 relationships, 0 execution flows).

> Index stale? Run `node .gitnexus/run.cjs analyze --index-only` from the project root — it auto-selects an available runner. No `.gitnexus/run.cjs` yet? Bootstrap with `npx`, `bunx`, or `pnpm dlx` — e.g. `bunx gitnexus@latest analyze` (npm 11 npx crash; #1939).

## Always Do

- **MUST run impact analysis before editing.** Use `impact({target: "symbolName", direction: "upstream"})` (MCP) or `node .gitnexus/run.cjs impact "symbolName" --direction upstream --repo .` (CLI fallback); report callers, processes, and risk. Never substitute grep for graph analysis.
- **MUST analyze graph changes before committing.** Use `detect_changes({scope: "all"})` (MCP) or `node .gitnexus/run.cjs detect-changes --scope all --repo .` (CLI fallback). `partial: true` or `truncated: true` is not a clean check — a zero means unseen, not unaffected; re-run it. For regression review: `detect_changes({scope: "compare", base_ref: "main"})` or `node .gitnexus/run.cjs detect-changes --scope compare --base-ref "main" --repo .`.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- **MUST treat `risk: UNKNOWN` as unresolved, not as low.** An empty caller set is not evidence the symbol is unused — it can also mean the callers are not resolvable by the index (plain-object property access, dynamic dispatch, cross-language calls). `impact` pairs `UNKNOWN` with a `riskNote` saying so. Confirm with a text search before treating the symbol as safe to change or delete; do not proceed on the strength of a zero.
- When exploring unfamiliar code, use `query({search_query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `context({name: "symbolName"})`.
- For security review, `explain({target: "fileOrSymbol"})` lists taint findings (source→sink flows; needs `analyze --pdg`).

## Never Do

- NEVER edit a function, class, or method before MCP/CLI impact analysis.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis, and never read `UNKNOWN` as an all-clear — it means the walk could not answer, which is the one verdict that requires confirming by other means.
- NEVER rename symbols with find-and-replace — use `rename` which understands the call graph.
- NEVER commit before MCP/CLI graph change analysis.

## Resources

| Resource | Use for |
| --- | --- |
| `gitnexus://repo/BookNest/context` | Codebase overview, check index freshness |
| `gitnexus://repo/BookNest/clusters` | All functional areas |
| `gitnexus://repo/BookNest/processes` | All execution flows |
| `gitnexus://repo/BookNest/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
| --- | --- |
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->

# BookNest Engineering Contract

Every contributor and coding agent must follow this contract for every change.

## Required Workflow

1. Read `docs/architecture.md` and the relevant naming table in `docs/coding-standards.md` before creating or moving code.
2. Run GitNexus impact analysis before editing an existing symbol. Treat `UNKNOWN` as unresolved and warn before changing a `HIGH` or `CRITICAL` target.
3. Keep dependencies inside the documented module graph. Presentation must not depend on data, core must not depend on a feature, and features must not depend on one another.
4. Use Navigation 3 keys and events. Do not introduce Navigation 2, `NavController`, or `NavHost`.
5. Preserve compact and expanded list-detail behavior for UI changes and add tests for changed behavior.
6. Do not add broad lint suppressions, lint baselines, destructive Room migrations, or generic `Manager`, `Helper`, `Utils`, `Common`, `Base`, `Impl`, or `I`-prefixed interface names.
7. Run `./gradlew verifyBookNest` before handing work off. Fix failures; do not bypass checks.
8. Refresh GitNexus with `node .gitnexus/run.cjs analyze --index-only`, then run `node .gitnexus/run.cjs detect-changes --scope all --repo .` before a commit or final handoff.

Narrow suppressions are allowed only beside the affected declaration and must include a reason. New modules and dependencies require updating both the architecture documentation and `architectureCheck`.
