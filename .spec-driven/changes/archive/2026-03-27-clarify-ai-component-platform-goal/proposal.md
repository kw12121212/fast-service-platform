# clarify-ai-component-platform-goal

## What

Clarify the repository's primary goal as an AI-oriented enterprise application component platform rather than an in-repository AI interaction or generation product.
Update the main documentation and active specs so they describe reliable, AI-friendly frontend and backend enterprise components as the core value of the project.

## Why

The current docs and specs still overemphasize natural-language input, prototype images, reference websites, and generated-project framing.
That wording makes the repository sound like it should directly implement an AI interaction surface, which is not the intended direction.
The repository's real job is to provide dependable enterprise components that AI can reuse safely.

## Scope

In scope:
- Update `README.md` and `RTK.md` to reflect the component-platform goal.
- Modify active product specs so they describe AI as the consumer of reusable components rather than the operator of an in-repository generation workflow.
- Replace the current active `generation/*` spec emphasis with component-oriented specs that define reusable baseline capability and external AI-context boundaries.
- Keep the rest of the current enterprise-management and engineering-support baseline unchanged.

Out of scope:
- Adding any new backend or frontend runtime behavior.
- Implementing AI chat, prompt intake, or generation workflows inside this repository.
- Changing the current minimum enterprise component set.

## Unchanged Behavior

Behaviors that must not change as a result of this change (leave blank if nothing is at risk):
- The platform remains focused on enterprise internal management applications.
- The platform remains AI-oriented and AI-friendly.
- The current backend and frontend implementation baseline remains valid.
