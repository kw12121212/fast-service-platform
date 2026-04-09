# Questions: descriptor-driven-report-page-generation

## Open

<!-- No open questions -->

## Resolved

- [x] Q: What should the second example descriptor's business domain be — department overview with summary cards, or a different domain like employee statistics?
  - **Decision**: Use department-overview as the second example — same domain as department-directory but with richer visualization (summary cards for counts/ratios, table for details, bar chart for headcount distribution).
  - **Rationale**: Keeps the examples cohesive while demonstrating the full range of section types.

- [x] Q: Should the `summary-cards` section type require a `cardKeys` array that names which aggregated metrics to display, or should it accept a free-form `cards` array with label/value pairs?
  - **Decision**: Use `cardKeys` — the descriptor declares which metric keys the summary-cards section expects, and the caller provides the aggregated values at runtime.
  - **Rationale**: Consistent with the descriptor pattern (declare keys, caller provides data), maintains clean separation between structure (descriptor) and runtime data.
