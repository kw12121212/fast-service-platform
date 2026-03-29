import path from 'node:path'

import { listPlatformUpgradeTargets } from './app-assembly-lib.mjs'

const targetDir = process.argv[2] ? path.resolve(process.argv[2]) : null
const result = await listPlatformUpgradeTargets(targetDir)

console.log(JSON.stringify(result))
