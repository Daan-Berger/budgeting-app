# Icons

Every icon in the Penpot file ("Icon / *" groups) is a [Lucide](https://lucide.dev/icons/) icon. Use them
through `@lucide/angular`: import the icon component and render it on an `<svg>`, for example
`<svg lucideList></svg>`, or `<svg [lucideIcon]="LucideList"></svg>` when the icon is data-driven.

Penpot draws icons with a **fixed 1.75px stroke** at every size. `app.config.ts` sets this globally with
`provideLucideConfig({ strokeWidth: 1.75, nonScalingStroke: true })`, so only the size needs setting
per use (from CSS, or the `size` input). Colour follows `currentColor`.

| Penpot name           | Lucide component        | Size | Used in (Penpot frames)                                                   |
| --------------------- | ----------------------- | ---- | ------------------------------------------------------------------------- |
| `Icon / dashboard`    | `LucideLayoutDashboard` | 20   | Rail + tab bar (22 on mobile): Dashboard                                  |
| `Icon / list`         | `LucideList`            | 20   | Rail + tab bar (22 on mobile): Transactions                               |
| `Icon / settings`     | `LucideSettings2`       | 20   | Rail + tab bar (22 on mobile): Settings                                   |
| `Icon / upload`       | `LucideUpload`          | 18   | Import button (all screens), import dialog                                |
| `Icon / file`         | `LucideFile`            | 18   | Import dialog / sheet: selected file                                      |
| `Icon / check`        | `LucideCheck`           | 18   | Import result banner (stroke **2.25**), checkboxes, settings totals       |
| `Icon / x`            | `LucideX`               | 18   | Close: import banner, import dialog, selected file                        |
| `Icon / logout`       | `LucideLogOut`          | 18   | Mobile settings: sign out                                                 |
| `Icon / search`       | `LucideSearch`          | 16   | Search fields (transactions, import, category sheet)                      |
| `Icon / calendar`     | `LucideCalendar`        | 16   | Transactions: month picker                                                |
| `Icon / chevronDown`  | `LucideChevronDown`     | 16   | Month picker, category select, form fields                                |
| `Icon / chevronLeft`  | `LucideChevronLeft`     | 16   | Pagination (prev), mobile dashboard month switcher                        |
| `Icon / chevronRight` | `LucideChevronRight`    | 16   | Pagination (next), mobile list rows, mobile month switcher                |
| `Icon / arrowLeft`    | `LucideArrowLeft`       | 16   | Transaction detail: back                                                  |
| `Icon / arrowRight`   | `LucideArrowRight`      | 16   | Dashboard: link                                                           |
| `Icon / plus`         | `LucidePlus`            | 16   | Transactions: new category; Settings: add                                 |
| `Icon / more`         | `LucideEllipsis`        | 16   | Settings: category row actions; mobile transaction detail nav             |
| `Icon / info`         | `LucideInfo`            | 16   | Dashboard: status                                                         |

Sizes are the most common size in Penpot; the screen's own spec is authoritative for each use.

Known differences from current Lucide (verified by comparing the Penpot geometry on Lucide's 24px grid):

- `calendar`: Penpot's frame has corner radius 3 instead of Lucide's 2. Not visible at 16px.
- `file`: Penpot uses Lucide's older outline (straight folded corner). Close enough at 18px.

When Penpot gets a new icon, check it against Lucide the same way and add a row here.
