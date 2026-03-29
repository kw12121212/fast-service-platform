import path from 'node:path'

import { executeDerivedAppUpgrade } from './app-assembly-lib.mjs'

const rawArgs = process.argv.slice(2)
const apply = rawArgs.includes('--apply')
const targetArg = rawArgs.find((arg) => arg !== '--apply')
const targetDir = path.resolve(targetArg ?? process.cwd())

const result = await executeDerivedAppUpgrade(targetDir, { apply })
console.log(JSON.stringify(result))
