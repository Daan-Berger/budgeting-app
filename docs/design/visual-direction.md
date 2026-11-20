# Visual direction

Design foundation for issue #25. The Penpot file "Budgeting App" holds the source of truth for tokens and components (pages: Foundations, Components, Desktop, Mobile); this note records the reasoning behind them. In code, the tokens live in `frontend/src/styles/tokens.css` and are mapped onto Angular Material in `frontend/src/material-theme.scss`.

## Brief

- **Product:** a personal budgeting app that splits each month's spending across three buckets: Needs (50%), Wants (30%), Savings (20%). Transactions come in from Belfius CSV exports and are categorised into buckets.
- **Audience:** one person (later perhaps a household) checking in a few times a month: importing a statement, sorting uncategorised rows, and seeing how the month is going.
- **Primary job:** answer "how is this month split, and what still needs sorting?" in one glance.
- **Tone:** calm, precise and a little tactile. It should feel like a well-kept paper ledger, not a trading terminal or a marketing site.

## Concept: banknote ledger

The colour story comes from the euro banknotes the money is actually in. Each bucket takes the colour of a note: Needs is €20 blue, Wants is €50 orange, Savings is €100 green. The rest of the interface stays quiet in cool, slightly green-grey "banknote paper" neutrals with deep petrol ink, so those three colours are the only loud thing on screen. Wherever a bucket appears (badge, split bar, chart segment), it has the same colour.

**The memorable element** is the *split bar*: one horizontal strip per month, divided into the three bucket colours by actual spend, with fixed tick marks at the 50% and 80% targets. It leads the dashboard and repeats in miniature elsewhere. Everything else is kept plain.

## Colour

| Role | Token | Hex |
|---|---|---|
| Page background ("paper") | `neutral-50` | `#F4F6F3` |
| Surface | `neutral-0` | `#FFFFFF` |
| Text ("ink") | `neutral-900` | `#161C1A` |
| Primary action (petrol) | `primary-600` | `#1F5160` |
| Needs (€20 blue) | `needs-500` | `#2F6DB5` |
| Wants (€50 orange) | `wants-500` | `#E07B24` |
| Savings (€100 green) | `savings-500` | `#3E9A5C` |

Semantic colours (success / warning / danger) are a step deeper than the bucket colours so they are never mistaken for a bucket. Amounts are not coloured red or green. The sign and the column carry the meaning, so bucket colour stays unambiguous.

## Typography

**Hanken Grotesk** for everything: a warm, slightly narrow grotesque with good tabular figures, which matter for columns of amounts. Use one family and build hierarchy from size and weight. Amounts always use tabular numerals (`font-variant-numeric: tabular-nums`).

Scale (based on the classic typographic scale, 4px baseline):

| Style | Size / line height | Weight | Use |
|---|---|---|---|
| Display | 48 / 56 | 600 | The month's headline figure on the dashboard |
| Heading L | 32 / 40 | 600 | Page titles |
| Heading M | 24 / 32 | 600 | Section titles |
| Heading S | 18 / 26 | 600 | Card titles |
| Body L | 16 / 24 | 400 | Long-form text, login |
| Body M | 14 / 20 | 400 | Default UI text, table rows |
| Body M Strong | 14 / 20 | 600 | Amounts, emphasis |
| Label | 12 / 16 | 500 | Form labels, badges |
| Caption | 12 / 16 | 400 | Helper text, metadata |

Labels are sentence case; there are no all-caps eyebrows.

Transactions without a category are labelled **No category** (filter chip, row badge, dashboard card). It names the state, not a task, and stays short enough for the filter chips. The action uses "categorise": "Categorise all 12", "Choose a category".

## Spacing, radius, elevation

- Spacing: 4px base: `4, 8, 12, 16, 24, 32, 48, 64`.
- Radius follows hierarchy rather than one value everywhere: badges `4`, controls (buttons, inputs) `8`, cards `14`, plus `full` for dots and pills.
- Elevation: surfaces are separated by a 1px `border-subtle` hairline on paper, not drop shadows. Only overlays (menus, dialogs) get a shadow.

## Layout

Desktop (1440): a fixed 232px left rail on the paper background, plus a content column (white surface sections, left-aligned, max 1120px, 12 columns, 24px gutter).

```
┌────────────┬──────────────────────────────────────────────┐
│ ● Budget   │  November 2026                    [Import]   │
│            │  ┌────────────────────────────────────────┐  │
│ Dashboard  │  │ ███████████████▌██████████▌█████       │  │  ← split bar
│ Transact.  │  │ Needs 48%     │ Wants 34% │ Savings 18%│  │
│ Settings   │  └────────────────────────────────────────┘  │
│            │  ┌── No category (12) ──┐ ┌── Buckets ────┐  │
│            │  │ rows…                │ │ …             │  │
│            │  └──────────────────────┘ └───────────────┘  │
└────────────┴──────────────────────────────────────────────┘
```

Mobile: the rail becomes a bottom tab bar (Dashboard, Transactions, Settings); content is a single column with 16px gutters.

Screens: **Login**, **Dashboard**, **Transactions** (list, import, sort), **Transaction detail**, **Settings**.

## Principles

1. Colour means bucket. If something is blue, orange or green, it is Needs, Wants or Savings.
2. Numbers line up. Use tabular figures, right-aligned amounts and consistent decimals.
3. Uncategorised work is always visible and one click from done.
4. Borders over shadows, quiet over decorative; the split bar is the one bold element.
