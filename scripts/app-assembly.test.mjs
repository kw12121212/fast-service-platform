import assert from 'node:assert/strict'
import { spawnSync } from 'node:child_process'
import { mkdtemp, readFile, rm, writeFile } from 'node:fs/promises'
import os from 'node:os'
import path from 'node:path'
import test from 'node:test'

import {
  REPO_ROOT,
  executeDerivedAppUpgrade,
  evaluateDerivedAppUpgrade,
  loadAssemblyContract,
  loadCompatibilitySuite,
  loadDerivedAppLifecycleContract,
  loadDerivedAppUpgradeExecutionContract,
  loadGeneratedAppVerificationContract,
  loadModuleRegistry,
  loadPlatformReleaseAdvisory,
  loadPlatformReleaseHistory,
  loadPlatformReleaseMetadata,
  listPlatformUpgradeTargets,
  planDerivedAppUpgrade,
  readJson,
  readPlatformReleaseAdvisory,
  runCompatibilitySuite,
  scaffoldDerivedApp,
  validateManifest,
  verifyDerivedApp
} from './app-assembly-lib.mjs'

test('validateManifest rejects missing required core modules', async () => {
  const registry = await loadModuleRegistry()
  const contract = await loadAssemblyContract()
  const manifest = {
    schemaVersion: 'fsp-app-manifest/v1',
    application: {
      id: 'broken-app',
      name: 'Broken App',
      packagePrefix: 'com.fastservice.platform.derived'
    },
    modules: ['admin-shell', 'user-management']
  }

  assert.throws(() => validateManifest(manifest, registry, contract), /required core module/)
})

test('scaffoldDerivedApp generates a core-only app without delivery routes', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-core-admin-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const verification = await verifyDerivedApp(outputDir)
    assert.equal(verification.ok, true)

    const router = await readFile(path.join(outputDir, 'frontend/src/app/router.tsx'), 'utf8')
    assert.equal(router.includes("path: 'projects'"), false)
    assert.equal(router.includes("path: 'tickets'"), false)
    assert.equal(router.includes("path: 'kanban'"), false)

    const servicesSql = await readFile(
      path.join(outputDir, 'backend/src/main/resources/sql/services.sql'),
      'utf8'
    )
    assert.equal(servicesSql.includes('project_service'), false)
    assert.equal(servicesSql.includes('ticket_service'), false)
    assert.equal(servicesSql.includes('kanban_service'), false)
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('compatibility suite fixtures match module registry profiles', async () => {
  const registry = await loadModuleRegistry()
  const suite = await loadCompatibilitySuite()

  for (const fixture of suite.fixtures.valid) {
    assert.deepEqual(
      registry.profiles[fixture.expectedProfile].modules,
      fixture.expectedSelectedModules
    )
  }
})

test('generated app verification contract is exposed as a normative asset', async () => {
  const assemblyContract = await loadAssemblyContract()
  const lifecycleContract = await loadDerivedAppLifecycleContract()
  const executionContract = await loadDerivedAppUpgradeExecutionContract()
  const platformAdvisory = await loadPlatformReleaseAdvisory()
  const platformHistory = await loadPlatformReleaseHistory()
  const platformRelease = await loadPlatformReleaseMetadata()
  const verificationContract = await loadGeneratedAppVerificationContract()

  assert.equal(
    assemblyContract.normativeAssets.generatedAppVerificationContract,
    'docs/ai/generated-app-verification-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.generatedAppVerificationContractSchema,
    'docs/ai/schemas/generated-app-verification-contract.schema.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.derivedAppLifecycleContract,
    'docs/ai/derived-app-lifecycle-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.platformReleaseMetadata,
    'docs/ai/platform-release.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.platformReleaseHistory,
    'docs/ai/platform-release-history.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.platformReleaseAdvisory,
    'docs/ai/platform-release-advisory.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.derivedAppUpgradeExecutionContract,
    'docs/ai/derived-app-upgrade-execution-contract.json'
  )
  assert.equal(lifecycleContract.schemaVersion, 'fsp-derived-app-lifecycle-contract/v1')
  assert.equal(executionContract.schemaVersion, 'fsp-derived-app-upgrade-execution-contract/v1')
  assert.equal(platformRelease.currentRelease.lifecycleContractVersion, lifecycleContract.schemaVersion)
  assert.equal(platformHistory.schemaVersion, 'fsp-platform-release-history/v1')
  assert.equal(
    lifecycleContract.normativeAssets.platformReleaseHistory,
    'docs/ai/platform-release-history.json'
  )
  assert.equal(
    executionContract.normativeAssets.platformReleaseHistory,
    'docs/ai/platform-release-history.json'
  )
  assert.equal(platformAdvisory.schemaVersion, 'fsp-platform-release-advisory/v1')
  assert.equal(platformRelease.currentRelease.releaseAdvisory, 'docs/ai/platform-release-advisory.json')
  assert.equal(platformRelease.currentRelease.releaseHistory, 'docs/ai/platform-release-history.json')
  assert.deepEqual(verificationContract.checks, [
    'required-files-present',
    'verification-inputs-present',
    'generated-context-selected-modules-match-manifest',
    'module-selected-routes-match-registry',
    'module-selected-backend-services-match-registry',
    'module-selected-backend-tables-match-registry'
  ])
  assert.deepEqual(
    verificationContract.compatibleVerifiers.map((verifier) => verifier.id),
    ['java-generated-app-verifier']
  )
})

test('compatibility suite passes for the node reference implementation', async () => {
  const result = await runCompatibilitySuite()
  assert.equal(result.ok, true)
  assert.deepEqual(
    result.implementations.map((implementation) => implementation.id),
    ['node-scaffolder', 'java-cli']
  )
  assert.deepEqual(
    result.implementations.map((implementation) => implementation.validFixtures),
    [
      ['default-baseline', 'core-admin'],
      ['default-baseline', 'core-admin']
    ]
  )
  assert.deepEqual(
    result.implementations.map((implementation) => implementation.invalidFixtures),
    [
      ['missing-required-core', 'unknown-module', 'missing-dependency'],
      ['missing-required-core', 'unknown-module', 'missing-dependency']
    ]
  )
})

test('generated output includes contract schemas required by the standardized invariants', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-schema-output-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const contract = await readJson(path.join(outputDir, 'docs/ai/app-assembly-contract.json'))
    for (const relativePath of contract.outputInvariants.requiredFiles) {
      const absolutePath = path.join(outputDir, relativePath)
      const contents = await readFile(absolutePath, 'utf8')
      assert.ok(contents.length > 0, `Expected non-empty generated file: ${relativePath}`)
    }
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('generated output includes lifecycle metadata and upgrade guidance', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-lifecycle-output-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const lifecycle = await readJson(path.join(outputDir, 'docs/ai/derived-app-lifecycle.json'))
    const context = await readJson(path.join(outputDir, 'docs/ai/context.json'))
    const manifest = await readJson(path.join(outputDir, 'app-manifest.json'))
    const readme = await readFile(path.join(outputDir, 'README.md'), 'utf8')

    assert.equal(lifecycle.schemaVersion, 'fsp-derived-app-lifecycle/v1')
    assert.equal(lifecycle.contractVersion, 'fsp-derived-app-lifecycle-contract/v1')
    assert.equal(lifecycle.sourcePlatform.id, 'fast-service-platform')
    assert.deepEqual(lifecycle.selectedModules, manifest.modules)
    assert.equal(context.lifecycle.metadata, 'docs/ai/derived-app-lifecycle.json')
    assert.equal(
      context.contractInputs.platformReleaseHistory,
      'docs/ai/platform-release-history.json'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedUpgradeEvaluation,
      './scripts/evaluate-derived-app-upgrade.sh <generated-app-dir>'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedUpgradeTargetSelection,
      './scripts/list-platform-upgrade-targets.sh [generated-app-dir]'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedReleaseAdvisory,
      './scripts/show-platform-release-advisory.sh [generated-app-dir]'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedUpgradeExecution,
      './scripts/execute-derived-app-upgrade.sh <generated-app-dir> [--apply]'
    )
    assert.equal(
      lifecycle.upgradeEvaluation.repositoryOwnedTargetSelectionEntrypoint,
      './scripts/list-platform-upgrade-targets.sh [generated-app-dir]'
    )
    assert.equal(
      lifecycle.upgradeEvaluation.platformReleaseHistory,
      'docs/ai/platform-release-history.json'
    )
    assert.equal(
      lifecycle.upgradeEvaluation.platformReleaseAdvisory,
      'docs/ai/platform-release-advisory.json'
    )
    assert.equal(
      lifecycle.upgradeEvaluation.upgradeExecutionContract,
      'docs/ai/derived-app-upgrade-execution-contract.json'
    )
    assert.ok(readme.includes('./scripts/evaluate-derived-app-upgrade.sh'))
    assert.ok(readme.includes('./scripts/list-platform-upgrade-targets.sh'))
    assert.ok(readme.includes('./scripts/show-platform-release-advisory.sh'))
    assert.ok(readme.includes('./scripts/execute-derived-app-upgrade.sh'))
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('verifyDerivedApp returns standardized verifier metadata', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-verifier-metadata-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const result = await verifyDerivedApp(outputDir)
    assert.equal(result.ok, true)
    assert.equal(result.contractVersion, 'fsp-generated-app-verification-contract/v1')
    assert.equal(result.verifierId, 'node-generated-app-verifier')
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('java generated-app verifier validates a generated app through repository-owned wrapper', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-java-verifier-wrapper-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const result = spawnSync(
      path.join(REPO_ROOT, 'scripts/verify-derived-app-java.sh'),
      [outputDir],
      {
        cwd: REPO_ROOT,
        encoding: 'utf8'
      }
    )

    assert.equal(result.status, 0, result.stderr || result.stdout)
    const payload = JSON.parse(result.stdout.trim().split('\n').at(-1))
    assert.equal(payload.contractVersion, 'fsp-generated-app-verification-contract/v1')
    assert.equal(payload.verifierId, 'java-generated-app-verifier')
    assert.deepEqual(payload.selectedModules, [
      'admin-shell',
      'user-management',
      'role-permission-management'
    ])
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('evaluateDerivedAppUpgrade returns compatibility metadata for a generated app', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-upgrade-eval-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const result = spawnSync(
      path.join(REPO_ROOT, 'scripts/evaluate-derived-app-upgrade.sh'),
      [outputDir],
      {
        cwd: REPO_ROOT,
        encoding: 'utf8'
      }
    )

    assert.equal(result.status, 0, result.stderr || result.stdout)
    const payload = JSON.parse(result.stdout.trim().split('\n').at(-1))
    assert.equal(payload.compatible, true)
    assert.equal(payload.sourcePlatformRelease, 'fast-service-platform/0.1.0-dev')
    assert.equal(payload.targetPlatformRelease, 'fast-service-platform/0.1.0-dev')
    assert.equal(payload.recommendedAction, 'already-on-target-platform-release')
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('listPlatformUpgradeTargets returns recognized releases and supported targets for a generated app', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-upgrade-targets-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const result = await listPlatformUpgradeTargets(outputDir)
    assert.equal(result.platformId, 'fast-service-platform')
    assert.equal(result.currentReleaseId, 'fast-service-platform/0.1.0-dev')
    assert.equal(result.sourceReleaseId, 'fast-service-platform/0.1.0-dev')
    assert.ok(
      result.recognizedReleases.some(
        (release) => release.releaseId === 'fast-service-platform/0.0.0-bootstrap'
      )
    )
    assert.deepEqual(
      result.availableTargetReleases.map((release) => release.releaseId),
      ['fast-service-platform/0.1.0-dev']
    )
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('platform upgrade target wrapper returns machine-readable lineage output', async () => {
  const result = spawnSync(
    path.join(REPO_ROOT, 'scripts/list-platform-upgrade-targets.sh'),
    [],
    {
      cwd: REPO_ROOT,
      encoding: 'utf8'
    }
  )

  assert.equal(result.status, 0, result.stderr || result.stdout)
  const payload = JSON.parse(result.stdout.trim().split('\n').at(-1))
  assert.equal(payload.platformId, 'fast-service-platform')
  assert.equal(payload.currentReleaseId, 'fast-service-platform/0.1.0-dev')
  assert.equal(payload.defaultTargetReleaseId, 'fast-service-platform/0.1.0-dev')
  assert.ok(
    payload.recognizedReleases.some(
      (release) => release.releaseId === 'fast-service-platform/0.0.0-bootstrap'
    )
  )
})

test('evaluateDerivedAppUpgrade reports unsupported source platform ids', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-upgrade-eval-negative-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const lifecyclePath = path.join(outputDir, 'docs/ai/derived-app-lifecycle.json')
    const lifecycle = await readJson(lifecyclePath)
    lifecycle.sourcePlatform.id = 'external-platform'
    await writeFile(lifecyclePath, `${JSON.stringify(lifecycle, null, 2)}\n`)

    const result = await evaluateDerivedAppUpgrade(outputDir)
    assert.equal(result.compatible, false)
    assert.ok(
      result.issues.some((issue) => issue.includes('Unsupported source platform id')),
      result.issues.join('\n')
    )
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('readPlatformReleaseAdvisory returns release delta and relevant checks for a derived app', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-advisory-read-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const advisory = await readPlatformReleaseAdvisory(outputDir)
    assert.equal(advisory.releaseId, 'fast-service-platform/0.1.0-dev')
    assert.equal(advisory.previousReleaseId, 'fast-service-platform/0.0.0-bootstrap')
    assert.equal(advisory.overallCompatibilityPosture, 'compatible-with-review')
    assert.deepEqual(advisory.selectedModules, [
      'admin-shell',
      'user-management',
      'role-permission-management'
    ])
    assert.ok(advisory.relevantChanges.length > 0)
    assert.ok(advisory.recommendedChecks.includes('review-platform-release-advisory'))
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('platform release advisory wrapper returns machine-readable output', async () => {
  const result = spawnSync(
    path.join(REPO_ROOT, 'scripts/show-platform-release-advisory.sh'),
    [],
    {
      cwd: REPO_ROOT,
      encoding: 'utf8'
    }
  )

  assert.equal(result.status, 0, result.stderr || result.stdout)
  const payload = JSON.parse(result.stdout.trim().split('\n').at(-1))
  assert.equal(payload.releaseId, 'fast-service-platform/0.1.0-dev')
  assert.equal(payload.previousReleaseId, 'fast-service-platform/0.0.0-bootstrap')
  assert.equal(payload.overallCompatibilityPosture, 'compatible-with-review')
})

test('planDerivedAppUpgrade reports dry-run auto-apply items for stale managed assets', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-upgrade-plan-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    await writeFile(
      path.join(outputDir, 'docs/ai/platform-release-advisory.json'),
      '{"schemaVersion":"stale"}\n'
    )

    const plan = await planDerivedAppUpgrade(outputDir)
    assert.equal(plan.dryRun, true)
    assert.equal(plan.compatible, true)
    assert.ok(
      plan.autoApplyItems.some((item) => item.path === 'docs/ai/platform-release-advisory.json'),
      JSON.stringify(plan.autoApplyItems)
    )
    assert.ok(plan.manualInterventionItems.length > 0)
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('executeDerivedAppUpgrade applies supported managed asset updates', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-upgrade-apply-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    await writeFile(
      path.join(outputDir, 'docs/ai/platform-release-advisory.json'),
      '{"schemaVersion":"stale"}\n'
    )

    const result = await executeDerivedAppUpgrade(outputDir, { apply: true })
    assert.equal(result.dryRun, false)
    assert.equal(result.applied, true)
    assert.ok(
      result.appliedItems.some((item) => item.path === 'docs/ai/platform-release-advisory.json'),
      JSON.stringify(result.appliedItems)
    )

    const refreshedAdvisory = await readJson(path.join(outputDir, 'docs/ai/platform-release-advisory.json'))
    assert.equal(refreshedAdvisory.schemaVersion, 'fsp-platform-release-advisory/v1')
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('upgrade execution wrapper returns machine-readable dry-run output', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-upgrade-wrapper-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const result = spawnSync(
      path.join(REPO_ROOT, 'scripts/execute-derived-app-upgrade.sh'),
      [outputDir],
      {
        cwd: REPO_ROOT,
        encoding: 'utf8'
      }
    )

    assert.equal(result.status, 0, result.stderr || result.stdout)
    const payload = JSON.parse(result.stdout.trim().split('\n').at(-1))
    assert.equal(payload.planVersion, 'fsp-derived-app-upgrade-plan/v1')
    assert.equal(payload.dryRun, true)
    assert.equal(payload.compatible, true)
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})
