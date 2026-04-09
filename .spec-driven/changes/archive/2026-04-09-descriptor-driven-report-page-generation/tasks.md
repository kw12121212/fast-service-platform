# Tasks: descriptor-driven-report-page-generation

## Implementation

- [x] Add `reportSection` definition to `docs/ai/schemas/descriptor-driven-management-module.schema.json` with properties: sectionKey, type, title, columns, cardKeys
- [x] Update `reportDescriptor` in the schema to accept `oneOf` with `columns` (existing) or `sections` (new) as alternative required fields
- [x] Validate the existing `department-directory.management-module.json` still passes the updated schema
- [x] Create a second example descriptor `docs/ai/management-modules/department-overview.management-module.json` using `sections` with at least summary-cards, table, and bar-chart sections
- [x] Validate the new example descriptor against the updated schema
- [x] Update `docs/ai/descriptor-driven-management-module-contract.json` to document the expanded report descriptor surface and reference the new example
- [x] Update `docs/ai/playbooks/prepare-descriptor-driven-management-module.md` to cover sections-based report descriptor usage, including when to use `sections` vs `columns` and how section keys map to dynamic-report section descriptors
- [x] Update `.spec-driven/specs/platform/built-in-components.md` to document the dynamic-report section types available for descriptor-driven generation

## Testing

- [x] Run schema validation against both example descriptors: `ajv validate -s docs/ai/schemas/descriptor-driven-management-module.schema.json -d docs/ai/management-modules/department-directory.management-module.json`
- [x] Run schema validation against the new example descriptor: `ajv validate -s docs/ai/schemas/descriptor-driven-management-module.schema.json -d docs/ai/management-modules/department-overview.management-module.json`
- [x] Write and run a unit test that programmatically validates both `columns`-based and `sections`-based report descriptors against the updated schema, confirming the `oneOf` constraint accepts either but not neither

## Verification

- [x] Verify both example descriptors pass schema validation without errors
- [x] Verify the existing department-directory example descriptor is unchanged (columns-based, no sections)
- [x] Verify the new department-overview example descriptor uses sections, not columns
- [x] Verify the playbook references both report descriptor paths (columns and sections)
- [x] Verify the contract documents the expanded report surface
- [x] Run `/spec-driven-verify descriptor-driven-report-page-generation` to confirm artifact completeness
