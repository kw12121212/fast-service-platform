import assert from 'node:assert/strict'

import { runCompatibilitySuite } from './app-assembly-lib.mjs'

const result = await runCompatibilitySuite()

assert.ok(
  result.ok,
  `App assembly compatibility verification failed\n${result.issues.map((issue) => `- ${issue}`).join('\n')}`
)

console.log(
  JSON.stringify(
    {
      implementations: result.implementations,
      verified: true
    },
    null,
    2
  )
)
