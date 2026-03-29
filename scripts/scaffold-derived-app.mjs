import assert from 'node:assert/strict'

import { scaffoldDerivedApp } from './app-assembly-lib.mjs'

function parseArgs(argv) {
  const args = {
    manifest: '',
    output: ''
  }

  for (let index = 0; index < argv.length; index += 1) {
    const value = argv[index]
    if (value === '--manifest') {
      args.manifest = argv[index + 1] ?? ''
      index += 1
    } else if (value === '--output') {
      args.output = argv[index + 1] ?? ''
      index += 1
    }
  }

  return args
}

const args = parseArgs(process.argv.slice(2))
assert.ok(args.manifest, 'Missing --manifest <path>')
assert.ok(args.output, 'Missing --output <absolute-output-dir>')

const result = await scaffoldDerivedApp({
  manifestPath: args.manifest,
  outputDir: args.output
})

console.log(
  JSON.stringify(
    {
      outputDir: result.outputDir,
      selectedModules: result.selectedModules
    },
    null,
    2
  )
)
