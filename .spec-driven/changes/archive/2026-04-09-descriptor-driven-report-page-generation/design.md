# Design: descriptor-driven-report-page-generation

## Approach

Extend the existing `reportDescriptor` definition in the management-module descriptor schema to support a `sections` array as an alternative to the current `columns` array. Each section entry declares a section type that maps directly to a dynamic-report section type. When `sections` is present, it takes precedence over `columns`; when `sections` is absent, the existing `columns` path continues to work unchanged.

This is a schema-and-documentation-only change at the descriptor level. No new frontend or backend runtime code is needed because the dynamic-report component already renders all section types and the descriptor path already delegates rendering to it.

### Schema evolution

Add a `reportSection` definition alongside the existing `reportColumn`:

```
reportSection:
  type: object
  required: [sectionKey, type]
  properties:
    sectionKey: string (camelCase)
    type: enum [summary-cards, table, bar-chart, line-chart, pie-chart]
    title: string (required for table and chart types)
    chartType: enum [bar, line, pie] (required when type = chart)
    columns: array of reportColumn (required when type = table)
    cardKeys: array of string (required when type = summary-cards)
```

Update `reportDescriptor` to accept either `columns` (backward compatible) or `sections` (new):

```
reportDescriptor:
  required: [componentId, title]
  oneOf:
    - required: [columns]   # legacy flat-table path
    - required: [sections]  # new multi-section path
  properties:
    componentId: const "dynamic-report"
    title: string
    columns: array of reportColumn     # existing
    sections: array of reportSection   # new
```

### Contract and playbook updates

- Update `descriptor-driven-management-module-contract.json` to document the expanded report surface and add a reference to the new example descriptor
- Extend the AI playbook to explain when to use `sections` vs `columns` and how section keys map to dynamic-report section descriptors

### New example descriptor

Add a second example (e.g. `department-overview.management-module.json`) that demonstrates a multi-section report: summary cards showing department count and active ratio, a table listing departments, and a bar chart showing headcount by department.

## Key Decisions

1. **`sections` alongside `columns`, not replacing it** — Backward compatibility for the existing department-directory example and any descriptors already produced. `columns` remains valid; `sections` is the richer alternative.

2. **Section types map 1:1 to dynamic-report section types** — No abstraction layer between the descriptor and the component. The descriptor expresses exactly what dynamic-report expects, keeping the generation path transparent.

3. **Schema-only change, no runtime code** — The dynamic-report component already handles all section types. This change unlocks the descriptor path to express them, but no new component code is needed.

4. **New example descriptor rather than modifying the existing one** — The department-directory example demonstrates the simple `columns` path. A new example demonstrates the `sections` path. Both coexist.

## Alternatives Considered

1. **Replace `columns` with `sections` entirely** — Would break the existing example and any descriptors already produced. Rejected for backward compatibility.

2. **Add a separate report-page descriptor schema** — Would create a parallel descriptor type for report-only pages, separate from management-module descriptors. Rejected because the milestone scope is specifically about extending the management-module descriptor's report capabilities, not about a standalone report-page path.

3. **Add a `sections` field inside each `reportColumn`** — Would mix the flat-table and multi-section concepts in a confusing way. Rejected for clarity; `sections` as a top-level alternative to `columns` is cleaner.

4. **Defer this change and expand report capabilities as part of V2** — Would delay V1 closure, since the descriptor-driven milestone is the critical path. Rejected for scheduling reasons; this is a bounded, low-risk schema extension.
