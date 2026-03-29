import path from 'node:path'

import { readPlatformReleaseAdvisory } from './app-assembly-lib.mjs'

const targetDir = process.argv[2] ? path.resolve(process.argv[2]) : null
const advisory = await readPlatformReleaseAdvisory(targetDir)

console.log(JSON.stringify(advisory))
