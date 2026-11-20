# Design specs

Penpot ("Budgeting App" file) is the source of truth for the design. So that a screen can be built and
reviewed without the Penpot MCP plugin connected, every frontend issue stores the styling it implements
here, extracted from Penpot:

- `<feature>.md`: the Penpot frames and boards it comes from, plus every measurement, colour, font and
  state as the design token it maps to (`--color-*`, `--font-*`, `--space-*`, `--radius-*`). Values
  that have no token are listed under **Off-scale values** with how the code handles them.

Shared references:

- [icons.md](icons.md): the Penpot icon set mapped to Lucide icons.
- Tokens: `frontend/src/styles/tokens.css`. Rationale: [../visual-direction.md](../visual-direction.md).

If the design changes in Penpot, update the spec in the same PR as the code.
