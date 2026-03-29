import assert from 'node:assert/strict'
import path from 'node:path'

import { evaluateDerivedAppUpgrade } from './app-assembly-lib.mjs'

const targetDir = process.argv[2] ? path.resolve(process.argv[2]) : process.cwd()
const result = await evaluateDerivedAppUpgrade(targetDir)

assert.ok(
  result.compatible,
  `Derived app upgrade evaluation failed for ${targetDir}\n${result.issues.map((issue) => `- ${issue}`).join('\n')}`
)

console.log(
  JSON.stringify(
    {
      targetDir,
      compatible: result.compatible,
      issues: result.issues,
      sourcePlatformRelease: result.sourcePlatformRelease,
      targetPlatformRelease: result.targetPlatformRelease,
      recommendedAction: result.recommendedAction
    }
  )
)
