# Dynamic Form Component

### Requirement: Platform Provides A Reusable Dynamic Form Component
The system MUST provide a reusable frontend form component that accepts a declarative form descriptor and renders a complete single-entity form without requiring the caller to write individual field widgets, validation logic, or mutation feedback handling.

#### Scenario: A contributor assembles a create form from a descriptor
- GIVEN a contributor supplies a form descriptor with an ordered field list
- WHEN they render the dynamic form component in create mode
- THEN the component displays each described field using the appropriate input widget
- AND each field initializes to its declared default value

#### Scenario: A contributor assembles an edit form from a descriptor
- GIVEN a contributor supplies a form descriptor and an initial-values map matching the descriptor's field keys
- WHEN they render the dynamic form component in edit mode
- THEN the component displays each described field pre-filled with the provided initial values
- AND the component does not fetch data from the backend itself

### Requirement: Dynamic Form Descriptor Covers V1 Field Types
The system MUST support the following widget types in the first dynamic form descriptor: text input, number input, date input, single-value select with static enum options, multi-line textarea, and boolean toggle.

#### Scenario: A contributor describes a form with varied field types
- GIVEN a form descriptor contains fields of each supported V1 widget type
- WHEN the dynamic form component renders that descriptor
- THEN each field is rendered with the widget that matches its declared type

### Requirement: Dynamic Form Validates Before Submission
The system MUST prevent form submission and surface field-level errors when descriptor-declared validation rules are not satisfied, including required-field enforcement.

#### Scenario: A contributor submits a form with a missing required field
- GIVEN a form descriptor marks a field as required
- WHEN the contributor attempts to submit the form with that field empty
- THEN the submission is blocked
- AND a visible error is shown for the missing field

### Requirement: Dynamic Form Hands Off Submission To The Caller
The system MUST deliver the collected field values to the caller through an onSubmit callback rather than performing the backend save operation itself.

#### Scenario: A contributor submits a valid form
- GIVEN a dynamic form passes frontend validation
- WHEN the contributor confirms submission
- THEN the onSubmit callback is invoked with the current field values keyed by field key
- AND the caller's data-access save flow receives those values

### Requirement: Dynamic Form Displays Backend Errors Via Existing Feedback Convention
The system MUST surface backend validation errors or rejection responses through the same mutation feedback convention used by the existing admin write workflows.

#### Scenario: The backend rejects a dynamic form submission
- GIVEN a dynamic form submission reaches the backend and is rejected
- WHEN the caller passes the backend error response to the component
- THEN a visible error is displayed using the existing mutation feedback convention
