# V1 Scope Boundaries

This file defines explicit negative constraints for V1. These are not aspirational — they
are hard boundaries that apply to all current roadmap work. Any proposal that would cross
these boundaries is out of scope for V1 regardless of how it is framed.

---

### Requirement: V1 Does Not Support Multi-Tenant Application Output
The platform MUST NOT produce derived applications that serve multiple tenants from a shared
runtime in V1.

#### Scenario: A contributor proposes a multi-tenant output target
- GIVEN a contributor wants to generate a multi-tenant SaaS application
- WHEN they check V1 supported output shapes
- THEN they find that V1 only targets single-tenant enterprise internal management monoliths
- AND they do not interpret the assembly or descriptor system as a path to multi-tenant output

---

### Requirement: V1 Does Not Support Microservice or Distributed Output
The platform MUST NOT generate derived applications composed of multiple independently
deployable services in V1.

#### Scenario: A contributor evaluates whether to add a microservice output path
- GIVEN a contributor considers adding a split-service assembly target
- WHEN they inspect the V1 product boundary
- THEN they see that V1 targets a single deployable monolith and does not support service decomposition

---

### Requirement: V1 Is Not A General Low-Code Platform
The descriptor-driven generation path MUST stay constrained to platform-owned interaction
patterns (dynamic form, dynamic report, workflow, template boundaries). It MUST NOT become
a general low-code or no-code platform for arbitrary application shapes.

#### Scenario: A contributor proposes adding an arbitrary CRUD generator
- GIVEN a contributor wants to generate any CRUD module from a free-form descriptor
- WHEN they check what the descriptor surface supports
- THEN they find it is limited to the current platform-owned module and interaction patterns
- AND they do not treat the descriptor system as a general-purpose code generation engine

---

### Requirement: V1 Does Not Include Runtime AI Interaction Features
The platform MUST NOT embed runtime AI chat, AI-assisted UI, or AI-driven in-product
recommendation features in V1 derived application output.

#### Scenario: A contributor proposes adding AI interaction to generated app UIs
- GIVEN a contributor wants generated apps to include AI chat or AI-suggestion widgets
- WHEN they check V1 derived application output scope
- THEN they find that AI is a platform contributor tool, not a runtime feature of generated apps

---

### Requirement: V1 Does Not Expose A Public API Or External Integration Surface
The platform MUST NOT define a public-facing API layer or external webhook/integration
surface for derived applications in V1.

#### Scenario: A contributor asks whether generated apps should expose external API endpoints
- GIVEN a contributor wants generated management apps to integrate with external systems via API
- WHEN they inspect V1 output shape
- THEN they see that V1 targets internal enterprise management use and does not define
  a public or partner-facing API contract

---

### Requirement: Module Registry Is Closed In V1
The platform MUST NOT provide a path for external or project-local contributors to register
new platform-owned business modules in V1. Module selection is limited to the existing
repository-owned module set.

#### Scenario: A contributor wants to create and register a new reusable module
- GIVEN a contributor wants to extend the platform module registry with a custom module
- WHEN they check V1 module extension support
- THEN they find that the module registry is closed in V1 and new module registration
  is not a supported contributor path

---

## V1 Closure

V1 is closed. The authoritative closure record is at `docs/ai/V1-CLOSURE.md`.

All six boundaries above remain in effect for V1 output. They are preserved, not relaxed.

The e2e fixture at `docs/ai/tests/e2e-fixture.solution-input.json` validates the complete V1 pipeline end-to-end.
