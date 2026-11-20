# App shell (#27)

Implemented in `frontend/src/app/layout/shell/`.

Penpot sources: the **Rail** board and **Divider** of every frame on the *Desktop* page (e.g. "Dashboard",
"Transactions"), the **Tab bar** board of every frame on the *Mobile* page, and the **Logo** and
**Nav item** (Active / Default) components on the *Components* page.

| Desktop rail                        | Mobile tab bar                          |
| ----------------------------------- | --------------------------------------- |
| ![](app-shell/desktop-rail.png)     | ![](app-shell/mobile-tab-bar.png)       |

Breakpoint: desktop layout from **1024px**, mobile below. Only one breakpoint.

## Desktop (Penpot frames are 1440 wide)

Frame: page background `--color-bg-page`, row layout: rail, divider, content.

**Rail**: 232px (`--layout-rail-width`), full height, background `--color-bg-page`, column layout,
padding 24 / 16 (`--space-6` / `--space-4`), gap 32 (`--space-8`) between logo, nav and account.
The account block is pushed to the bottom.

**Divider**: 1px, full height, `--color-border-subtle` (the rail's right border in code).

**Logo** (component "Logo"), with 12px (`--space-3`) horizontal inset:

- Mark: 28 × 28, `--color-action` fill, radius 8 (`--radius-control`), padding 8 6 8 4, gap 2.
  Three bars, 12 high, radius 1: Needs 8 wide (`--color-needs-500`), Wants 5 (`--color-wants-500`),
  Savings 3 (`--color-savings-500`): the 50/30/20 split.
- Text "Budget": `--font-heading-s`, `--color-text`. Gap mark to text: 10.

**Nav**: column, gap 4 (`--space-1`). Each item is full rail width (200), 36 high, padding 8 / 12
(`--space-2` / `--space-3`), gap 12 (`--space-3`), radius 8 (`--radius-control`), 20px icon.

| State   | Background        | Border                           | Label                                    | Icon                     |
| ------- | ----------------- | -------------------------------- | ---------------------------------------- | ------------------------ |
| Default | none              | none                             | `--font-body-m`, `--color-neutral-700`   | `--color-text-secondary` |
| Active  | `--color-surface` | 1px inner, `--color-border-subtle` | `--font-body-m-strong`, `--color-text` | `--color-action`         |

Icons: Dashboard `LucideLayoutDashboard`, Transactions `LucideList`, Settings `LucideSettings2`
(see [icons.md](icons.md)).

**Account**: row, padding 8 / 12, gap 10, centred.

- Avatar: 32 circle (`--radius-full`), `--color-primary-100` fill, initials in `--font-label`,
  `--color-primary-700`.
- Name "Personal": `--font-body-m-strong`, `--color-text`.
- Account "Belfius ···· 4417": `--font-caption`, `--color-text-muted`.

**Content**: padding 32 top (`--space-8`), 44 left/right (`--layout-content-inset`), 48 bottom
(`--space-12`); column gap 24 between sections. At 1440 this gives a 1120px column
(`--layout-content-max`); it is left-aligned.

## Mobile (Penpot frames are 390 × 844)

**Content**: padding 16 16 24 16 (`--space-4`, `--space-6` bottom), gap 16.

**Tab bar**: fixed to the bottom, `--color-surface`, 1px top divider `--color-border-subtle`.
Tabs row with padding 4 / 8 (`--space-1` / `--space-2`); three equal-width tabs.

Each tab: column, centred, padding 8 0 (`--space-2`), gap 4 (`--space-1`), 22px icon, 16px label.

| State   | Label                                          | Icon colour              |
| ------- | ---------------------------------------------- | ------------------------ |
| Default | `--font-caption` (400), `--color-text-secondary` | `--color-text-secondary` |
| Active  | `--font-label` (500), `--color-action`         | `--color-action`         |

No logo or account block on mobile.

## Off-scale values

| Value                              | Handling in code                                                        |
| ---------------------------------- | ----------------------------------------------------------------------- |
| Content inset 44                   | Code-only token `--layout-content-inset: 44px` in `tokens.css`          |
| Logo mark padding 6, gap 2, bars   | Literal px in `shell.css` (illustration, not layout)                    |
| Gap 10 (logo, account)             | `--space-3` (12)                                                        |
| Tab bar bottom padding 24          | iPhone home-indicator area in the mock: `max(--space-1, env(safe-area-inset-bottom))` |

## Not designed (code decisions)

- Hover on default nav items (desktop): label colour `--color-text`.
- Keyboard focus: 2px `--color-focus-ring` outline, 2px offset, on `:focus-visible` only.
