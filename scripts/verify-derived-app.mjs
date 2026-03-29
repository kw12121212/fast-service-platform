import assert from 'node:assert/strict'
import path from 'node:path'

import { verifyDerivedApp } from './app-assembly-lib.mjs'

const targetDir = process.argv[2] ? path.resolve(process.argv[2]) : process.cwd()
const result = await verifyDerivedApp(targetDir)

assert.ok(
  result.ok,
  `Derived app verification failed for ${targetDir}\n${result.issues.map((issue) => `- ${issue}`).join('\n')}`
)

console.log(
  JSON.stringify(
    {
      targetDir,
      contractVersion: result.contractVersion,
      verifierId: result.verifierId,
      selectedModules: result.selectedModules,
      verified: true
    },
    null,
    2
  )
)
