# Questions: add-java-cli-assembly-implementation

## Open

<!-- No open questions -->

## Resolved

- [x] Q: Should the next compatible implementation be a Java CLI or an AI-native no-script path?
  Context: This determines the implementation direction for the next app assembly expansion.
  A: The next compatible implementation should be a Java CLI.

- [x] Q: Should the Java CLI live inside the existing `backend/` workspace or in a separate Maven workspace?
  Context: This determines how strongly repository tooling is separated from backend runtime code.
  A: The Java CLI should live in a separate Maven workspace.
