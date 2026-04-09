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

    const moduleSelectionTs = await readFile(
      path.join(outputDir, 'frontend/src/app/module-selection.ts'),
      'utf8'
    )
    assert.ok(moduleSelectionTs.includes('project: false'))
    assert.ok(moduleSelectionTs.includes('ticket: false'))
    assert.ok(moduleSelectionTs.includes('kanban: false'))
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('scaffoldDerivedApp emits all-true module-selection.ts for baseline-v1 profile', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-baseline-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/default-baseline-app.json'),
      outputDir
    })

    const moduleSelectionTs = await readFile(
      path.join(outputDir, 'frontend/src/app/module-selection.ts'),
      'utf8'
    )
    assert.ok(moduleSelectionTs.includes('project: true'))
    assert.ok(moduleSelectionTs.includes('ticket: true'))
    assert.ok(moduleSelectionTs.includes('kanban: true'))
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

test('compatibility suite fixture coverage includes representative valid and invalid boundaries', async () => {
  const suite = await loadCompatibilitySuite()

  assert.deepEqual(
    suite.fixtures.valid.map((fixture) => fixture.id),
    ['default-baseline', 'core-admin', 'project-admin', 'project-repository', 'project-kanban']
  )
  assert.deepEqual(
    suite.fixtures.invalid.map((fixture) => fixture.id),
    [
      'missing-required-core',
      'unknown-module',
      'missing-dependency',
      'duplicate-module',
      'kanban-without-project',
      'repository-without-project',
      'invalid-package-prefix'
    ]
  )
})

test('generated app verification contract is exposed as a normative asset', async () => {
  const assemblyContract = await loadAssemblyContract()
  const lifecycleContract = await loadDerivedAppLifecycleContract()
  const executionContract = await loadDerivedAppUpgradeExecutionContract()
  const platformAdvisory = await loadPlatformReleaseAdvisory()
  const platformHistory = await loadPlatformReleaseHistory()
  const platformRelease = await loadPlatformReleaseMetadata()
  const verificationContract = await loadGeneratedAppVerificationContract()
  const solutionInputContract = await readJson(
    path.join(REPO_ROOT, 'docs/ai/ai-solution-input-contract.json')
  )
  const solutionInputSchema = await readJson(
    path.join(REPO_ROOT, 'docs/ai/schemas/ai-solution-input.schema.json')
  )
  const planningContract = await readJson(
    path.join(REPO_ROOT, 'docs/ai/solution-to-manifest-planning-contract.json')
  )
  const planningSchema = await readJson(
    path.join(REPO_ROOT, 'docs/ai/schemas/solution-to-manifest-planning.schema.json')
  )
  const planningExample = await readJson(
    path.join(REPO_ROOT, 'docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json')
  )
  const recommendationContract = await readJson(
    path.join(REPO_ROOT, 'docs/ai/solution-to-manifest-recommendation-contract.json')
  )
  const recommendationSchema = await readJson(
    path.join(REPO_ROOT, 'docs/ai/schemas/solution-to-manifest-recommendation.schema.json')
  )
  const recommendationExample = await readJson(
    path.join(REPO_ROOT, 'docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json')
  )
  const descriptorContract = await readJson(
    path.join(REPO_ROOT, 'docs/ai/descriptor-driven-management-module-contract.json')
  )
  const descriptorSchema = await readJson(
    path.join(REPO_ROOT, 'docs/ai/schemas/descriptor-driven-management-module.schema.json')
  )
  const descriptorExample = await readJson(
    path.join(REPO_ROOT, 'docs/ai/management-modules/department-directory.management-module.json')
  )
  const solutionInputExample = await readJson(
    path.join(REPO_ROOT, 'docs/ai/solution-inputs/core-admin-console.solution-input.json')
  )
  const templateContract = await readJson(
    path.join(REPO_ROOT, 'docs/ai/structured-app-template-contract.json')
  )
  const templateContractSchema = await readJson(
    path.join(REPO_ROOT, 'docs/ai/schemas/structured-app-template-contract.schema.json')
  )
  const templateMap = await readJson(
    path.join(REPO_ROOT, 'docs/ai/template-classifications/default-derived-app-template-map.json')
  )
  const templateMapSchema = await readJson(
    path.join(REPO_ROOT, 'docs/ai/schemas/derived-app-template-map.schema.json')
  )
  const orchestrationContract = await readJson(
    path.join(REPO_ROOT, 'docs/ai/ai-tool-orchestration-contract.json')
  )
  const aiContextText = await readFile(path.join(REPO_ROOT, 'docs/ai/context.yaml'), 'utf8')
  const quickstartText = await readFile(path.join(REPO_ROOT, 'docs/ai/quickstart.md'), 'utf8')

  assert.equal(
    assemblyContract.normativeAssets.aiSolutionInputContract,
    'docs/ai/ai-solution-input-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.aiSolutionInputSchema,
    'docs/ai/schemas/ai-solution-input.schema.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.solutionToManifestPlanningContract,
    'docs/ai/solution-to-manifest-planning-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.solutionToManifestPlanningSchema,
    'docs/ai/schemas/solution-to-manifest-planning.schema.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.solutionToManifestRecommendationContract,
    'docs/ai/solution-to-manifest-recommendation-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.solutionToManifestRecommendationSchema,
    'docs/ai/schemas/solution-to-manifest-recommendation.schema.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.descriptorDrivenManagementModuleContract,
    'docs/ai/descriptor-driven-management-module-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.descriptorDrivenManagementModuleSchema,
    'docs/ai/schemas/descriptor-driven-management-module.schema.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.descriptorDrivenManagementModuleExample,
    'docs/ai/management-modules/department-directory.management-module.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.aiToolOrchestrationContract,
    'docs/ai/ai-tool-orchestration-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.aiToolOrchestrationContractSchema,
    'docs/ai/schemas/ai-tool-orchestration-contract.schema.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.structuredAppTemplateContract,
    'docs/ai/structured-app-template-contract.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.structuredAppTemplateContractSchema,
    'docs/ai/schemas/structured-app-template-contract.schema.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.derivedAppTemplateMap,
    'docs/ai/template-classifications/default-derived-app-template-map.json'
  )
  assert.equal(
    assemblyContract.normativeAssets.derivedAppTemplateMapSchema,
    'docs/ai/schemas/derived-app-template-map.schema.json'
  )
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
  assert.equal(solutionInputContract.schemaVersion, 'fsp-ai-solution-input-contract/v1')
  assert.equal(solutionInputSchema.title, 'Fast Service Platform AI Solution Input')
  assert.equal(planningContract.schemaVersion, 'fsp-solution-to-manifest-planning-contract/v1')
  assert.equal(
    planningContract.planningArtifact.schema,
    'docs/ai/schemas/solution-to-manifest-planning.schema.json'
  )
  assert.equal(planningSchema.title, 'Fast Service Platform Solution To Manifest Plan')
  assert.equal(planningExample.schemaVersion, 'fsp-solution-to-manifest-plan/v1')
  assert.equal(planningExample.solutionInput.sourceAsset, 'docs/ai/solution-inputs/core-admin-console.solution-input.json')
  assert.equal(planningExample.manifestPreparation.standaloneManifestRequired, true)
  assert.equal(
    recommendationContract.schemaVersion,
    'fsp-solution-to-manifest-recommendation-contract/v1'
  )
  assert.equal(
    recommendationContract.recommendationArtifact.schema,
    'docs/ai/schemas/solution-to-manifest-recommendation.schema.json'
  )
  assert.equal(
    recommendationSchema.title,
    'Fast Service Platform Solution To Manifest Recommendation'
  )
  assert.equal(
    recommendationExample.schemaVersion,
    'fsp-solution-to-manifest-recommendation/v1'
  )
  assert.equal(
    recommendationExample.planningInput.sourceAsset,
    'docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json'
  )
  assert.equal(
    recommendationExample.manifestPreparationBoundary.standaloneManifestRequired,
    true
  )
  assert.equal(
    descriptorContract.schemaVersion,
    'fsp-descriptor-driven-management-module-contract/v1'
  )
  assert.equal(
    descriptorContract.descriptorArtifact.schema,
    'docs/ai/schemas/descriptor-driven-management-module.schema.json'
  )
  assert.equal(
    descriptorSchema.title,
    'Fast Service Platform Descriptor-Driven Management Module'
  )
  assert.equal(descriptorExample.schemaVersion, 'fsp-management-module-descriptor/v1')
  assert.equal(
    descriptorExample.source.planningInput.sourceAsset,
    'docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json'
  )
  assert.equal(
    descriptorExample.source.recommendationInput.sourceAsset,
    'docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json'
  )
  assert.equal(descriptorExample.managementModule.report.componentId, 'dynamic-report')
  assert.equal(descriptorExample.managementModule.form.componentId, 'dynamic-form')
  assert.equal(descriptorExample.manifestPreparation.standaloneManifestRequired, true)
  assert.equal(descriptorExample.boundaries.usesWorkflowGeneration, false)
  assert.equal(descriptorExample.boundaries.extendsClosedModuleRegistry, false)
  assert.equal(descriptorExample.boundaries.descriptorIsAssemblyRuntimeInput, false)
  assert.equal(templateContract.schemaVersion, 'fsp-structured-app-template-contract/v1')
  assert.equal(templateContractSchema.title, 'Fast Service Platform Structured App Template Contract')
  assert.equal(templateMap.schemaVersion, 'fsp-derived-app-template-map/v1')
  assert.equal(templateMapSchema.title, 'Fast Service Platform Derived App Template Map')
  assert.equal(
    templateMap.entries.some(
      (entry) => entry.path === 'frontend/src/app/router.tsx' && entry.slotId === 'frontend-admin-routes'
    ),
    true
  )
  assert.equal(
    solutionInputContract.normativeAssets.solutionToManifestPlanningContract,
    'docs/ai/solution-to-manifest-planning-contract.json'
  )
  assert.equal(
    solutionInputContract.normativeAssets.solutionToManifestPlanningSchema,
    'docs/ai/schemas/solution-to-manifest-planning.schema.json'
  )
  assert.equal(
    solutionInputContract.normativeAssets.solutionToManifestRecommendationContract,
    'docs/ai/solution-to-manifest-recommendation-contract.json'
  )
  assert.equal(
    solutionInputContract.normativeAssets.solutionToManifestRecommendationSchema,
    'docs/ai/schemas/solution-to-manifest-recommendation.schema.json'
  )
  assert.equal(
    solutionInputContract.normativeAssets.descriptorDrivenManagementModuleContract,
    'docs/ai/descriptor-driven-management-module-contract.json'
  )
  assert.equal(
    solutionInputContract.normativeAssets.descriptorDrivenManagementModuleSchema,
    'docs/ai/schemas/descriptor-driven-management-module.schema.json'
  )
  assert.equal(
    solutionInputContract.inputLayering.planningRole,
    'produce-deterministic-module-and-manifest-preparation-decisions-before-assembly'
  )
  assert.equal(
    solutionInputContract.inputLayering.recommendationRole,
    'optionally-produce-manifest-guidance-after-planning-before-assembly'
  )
  assert.equal(
    solutionInputContract.inputLayering.descriptorRole,
    'optionally-produce-bounded-management-module-generation-facts-before-assembly'
  )
  assert.equal(
    solutionInputContract.mappingGuidance.planningContract,
    'docs/ai/solution-to-manifest-planning-contract.json'
  )
  assert.equal(
    solutionInputContract.mappingGuidance.recommendationContract,
    'docs/ai/solution-to-manifest-recommendation-contract.json'
  )
  assert.equal(
    solutionInputContract.mappingGuidance.descriptorContract,
    'docs/ai/descriptor-driven-management-module-contract.json'
  )
  assert.deepEqual(solutionInputContract.examplePlans, [
    'docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json'
  ])
  assert.deepEqual(solutionInputContract.exampleRecommendations, [
    'docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json'
  ])
  assert.deepEqual(solutionInputContract.exampleDescriptors, [
    'docs/ai/management-modules/department-directory.management-module.json'
  ])
  assert.equal(
    planningContract.boundaries.assemblyRuntimeInput,
    'planning output MUST be translated into a standalone app-manifest before repository-owned assembly tooling is invoked'
  )
  assert.equal(
    planningContract.boundaries.descriptorBoundary,
    'planning output MAY feed a bounded management-module descriptor, but that descriptor MUST remain distinct from the standalone app-manifest'
  )
  assert.deepEqual(planningContract.examplePlans, [
    'docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json'
  ])
  assert.deepEqual(planningContract.exampleDescriptors, [
    'docs/ai/management-modules/department-directory.management-module.json'
  ])
  assert.equal(
    recommendationContract.boundaries.assemblyRuntimeInput,
    'contributors MUST still produce a standalone app-manifest before repository-owned assembly tooling is invoked'
  )
  assert.equal(
    recommendationContract.boundaries.descriptorBoundary,
    'recommendation output MAY inform a bounded management-module descriptor, but that descriptor remains upstream guidance rather than the assembly runtime input'
  )
  assert.deepEqual(recommendationContract.exampleRecommendations, [
    'docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json'
  ])
  assert.deepEqual(recommendationContract.exampleDescriptors, [
    'docs/ai/management-modules/department-directory.management-module.json'
  ])
  assert.equal(
    assemblyContract.machineReadableIndexes.solutionToManifestPlanningContract,
    'docs/ai/solution-to-manifest-planning-contract.json'
  )
  assert.equal(
    assemblyContract.machineReadableIndexes.solutionToManifestRecommendationContract,
    'docs/ai/solution-to-manifest-recommendation-contract.json'
  )
  assert.equal(
    assemblyContract.machineReadableIndexes.descriptorDrivenManagementModuleContract,
    'docs/ai/descriptor-driven-management-module-contract.json'
  )
  assert.deepEqual(assemblyContract.machineReadableIndexes.solutionPlanningExamples, [
    'docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json'
  ])
  assert.deepEqual(assemblyContract.machineReadableIndexes.solutionRecommendationExamples, [
    'docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json'
  ])
  assert.deepEqual(assemblyContract.machineReadableIndexes.managementModuleDescriptorExamples, [
    'docs/ai/management-modules/department-directory.management-module.json'
  ])
  assert.equal(
    assemblyContract.inputLayers.solutionPlanning.contract,
    'docs/ai/solution-to-manifest-planning-contract.json'
  )
  assert.equal(
    assemblyContract.inputLayers.solutionRecommendation.contract,
    'docs/ai/solution-to-manifest-recommendation-contract.json'
  )
  assert.equal(
    assemblyContract.inputLayers.descriptorDrivenManagementModule.contract,
    'docs/ai/descriptor-driven-management-module-contract.json'
  )
  assert.equal(
    assemblyContract.aiToolingGuidance.solutionToManifestPlanningContract,
    'docs/ai/solution-to-manifest-planning-contract.json'
  )
  assert.equal(
    assemblyContract.aiToolingGuidance.solutionToManifestRecommendationContract,
    'docs/ai/solution-to-manifest-recommendation-contract.json'
  )
  assert.equal(
    assemblyContract.aiToolingGuidance.descriptorDrivenManagementModuleContract,
    'docs/ai/descriptor-driven-management-module-contract.json'
  )
  assert.equal(
    orchestrationContract.supportedWorkflowCategories[0].readBefore.includes(
      'docs/ai/solution-to-manifest-planning-contract.json'
    ),
    true
  )
  assert.equal(
    orchestrationContract.supportedWorkflowCategories[0].readBefore.includes(
      'docs/ai/solution-to-manifest-recommendation-contract.json'
    ),
    true
  )
  assert.equal(
    orchestrationContract.supportedWorkflowCategories[0].readBefore.includes(
      'docs/ai/descriptor-driven-management-module-contract.json'
    ),
    true
  )
  assert.equal(
    orchestrationContract.supportedWorkflowCategories[0].successSignals.includes(
      'solution-input-has-been-mapped-to-solution-plan'
    ),
    true
  )
  assert.equal(
    orchestrationContract.supportedWorkflowCategories[0].successSignals.includes(
      'solution-plan-has-been-reviewed-for-optional-recommendation'
    ),
    true
  )
  assert.equal(
    orchestrationContract.supportedWorkflowCategories[0].successSignals.includes(
      'descriptor-driven-management-module-has-been-reviewed-or-prepared-when-needed'
    ),
    true
  )
  assert.equal(
    solutionInputContract.inputLayering.assemblyManifestRole,
    'direct-input-to-repository-owned-assembly-tooling'
  )
  assert.deepEqual(solutionInputContract.exampleInputs, [
    'docs/ai/solution-inputs/core-admin-console.solution-input.json'
  ])
  assert.deepEqual(solutionInputExample.constraints.requiredModules, [
    'admin-shell',
    'user-management',
    'role-permission-management'
  ])
  assert.equal(orchestrationContract.aiRole, 'tool-orchestrator')
  assert.equal(orchestrationContract.defaultFacade, './scripts/platform-tool.sh <group> <command> [...]')
  assert.deepEqual(
    orchestrationContract.supportedWorkflowCategories.map((workflow) => workflow.id),
    [
      'assembly',
      'generated-app-verification',
      'upgrade-target-selection',
      'release-advisory',
      'upgrade-evaluation',
      'upgrade-execution'
    ]
  )
  assert.deepEqual(verificationContract.checks, [
    'required-files-present',
    'verification-inputs-present',
    'template-boundary-assets-present',
    'generated-context-selected-modules-match-manifest',
    'module-selected-routes-match-registry',
    'module-selected-backend-services-match-registry',
    'module-selected-backend-tables-match-registry'
  ])
  assert.deepEqual(
    verificationContract.referenceVerifiers.map((verifier) => verifier.id),
    ['java-generated-app-verifier']
  )
  assert.deepEqual(
    verificationContract.compatibleVerifiers.map((verifier) => verifier.id),
    ['java-generated-app-verifier-cli']
  )
  assert.equal(
    verificationContract.aiToolingGuidance.orchestrationContract,
    'docs/ai/ai-tool-orchestration-contract.json'
  )
  assert.equal(
    lifecycleContract.normativeAssets.aiToolOrchestrationContract,
    'docs/ai/ai-tool-orchestration-contract.json'
  )
  assert.equal(
    executionContract.normativeAssets.aiToolOrchestrationContract,
    'docs/ai/ai-tool-orchestration-contract.json'
  )
  assert.equal(
    lifecycleContract.normativeAssets.structuredAppTemplateContract,
    'docs/ai/structured-app-template-contract.json'
  )
  assert.equal(
    executionContract.normativeAssets.structuredAppTemplateContract,
    'docs/ai/structured-app-template-contract.json'
  )
  assert.equal(
    executionContract.normativeAssets.templateClassificationMap,
    'docs/ai/template-classifications/default-derived-app-template-map.json'
  )
  assert.equal(
    templateContract.normativeAssets.descriptorDrivenManagementModuleContract,
    'docs/ai/descriptor-driven-management-module-contract.json'
  )
  assert.deepEqual(templateContract.descriptorDrivenModuleOutput.allowedSlotHosts, [
    'frontend-admin-routes',
    'frontend-admin-navigation',
    'backend-table-contract',
    'backend-service-contract'
  ])
  assert.deepEqual(templateContract.descriptorDrivenModuleOutput.requiredInteractionComponents, [
    'dynamic-form',
    'dynamic-report'
  ])
  assert.equal(aiContextText.includes('ai_solution_input_contract: docs/ai/ai-solution-input-contract.json'), true)
  assert.equal(
    aiContextText.includes(
      'solution_to_manifest_planning_contract: docs/ai/solution-to-manifest-planning-contract.json'
    ),
    true
  )
  assert.equal(
    aiContextText.includes(
      'solution_to_manifest_recommendation_contract: docs/ai/solution-to-manifest-recommendation-contract.json'
    ),
    true
  )
  assert.equal(
    aiContextText.includes(
      'descriptor_driven_management_module_contract: docs/ai/descriptor-driven-management-module-contract.json'
    ),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/schemas/solution-to-manifest-planning.schema.json'),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/schemas/solution-to-manifest-recommendation.schema.json'),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/schemas/descriptor-driven-management-module.schema.json'),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json'),
    true
  )
  assert.equal(
    aiContextText.includes(
      '- docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json'
    ),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/management-modules/department-directory.management-module.json'),
    true
  )
  assert.equal(
    aiContextText.includes('structured_app_template_contract: docs/ai/structured-app-template-contract.json'),
    true
  )
  assert.equal(
    aiContextText.includes('derived_app_runtime_smoke: ./scripts/platform-tool.sh generated-app smoke <generated-app-dir>'),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/playbooks/define-ai-solution-input.md'),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/playbooks/prepare-descriptor-driven-management-module.md'),
    true
  )
  assert.equal(
    aiContextText.includes('- docs/ai/playbooks/customize-derived-app-template-boundaries.md'),
    true
  )
  assert.equal(
    quickstartText.includes('docs/ai/ai-solution-input-contract.json'),
    true
  )
  assert.equal(
    quickstartText.includes('docs/ai/solution-to-manifest-planning-contract.json'),
    true
  )
  assert.equal(
    quickstartText.includes('docs/ai/solution-to-manifest-recommendation-contract.json'),
    true
  )
  assert.equal(
    quickstartText.includes('docs/ai/descriptor-driven-management-module-contract.json'),
    true
  )
  assert.equal(
    quickstartText.includes('docs/ai/solution-plans/core-admin-console.solution-to-manifest-plan.json'),
    true
  )
  assert.equal(
    quickstartText.includes(
      'docs/ai/solution-recommendations/core-admin-console.solution-to-manifest-recommendation.json'
    ),
    true
  )
  assert.equal(
    quickstartText.includes('docs/ai/management-modules/department-directory.management-module.json'),
    true
  )
  assert.equal(quickstartText.includes('docs/ai/structured-app-template-contract.json'), true)
  assert.equal(quickstartText.includes('./scripts/platform-tool.sh generated-app smoke ../core-admin-console'), true)
})

test('compatibility suite passes for the java reference implementation', async () => {
  const result = await runCompatibilitySuite()
  assert.equal(result.ok, true)
  assert.deepEqual(
    result.implementations.map((implementation) => implementation.id),
    ['java-cli']
  )
  assert.deepEqual(
    result.implementations.map((implementation) => implementation.validFixtures),
    [['default-baseline', 'core-admin', 'project-admin', 'project-repository', 'project-kanban']]
  )
  assert.deepEqual(
    result.implementations.map((implementation) => implementation.invalidFixtures),
    [
      [
        'missing-required-core',
        'unknown-module',
        'missing-dependency',
        'duplicate-module',
        'kanban-without-project',
        'repository-without-project',
        'invalid-package-prefix'
      ]
    ]
  )
})

test('project-admin compatibility fixture generates projects without kanban or ticket wiring', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-project-admin-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/compatibility/fixtures/project-admin.manifest.json'),
      outputDir
    })

    const router = await readFile(path.join(outputDir, 'frontend/src/app/router.tsx'), 'utf8')
    assert.equal(router.includes("path: 'projects'"), true)
    assert.equal(router.includes("path: 'kanban'"), false)
    assert.equal(router.includes("path: 'tickets'"), false)

    const servicesSql = await readFile(
      path.join(outputDir, 'backend/src/main/resources/sql/services.sql'),
      'utf8'
    )
    const tablesSql = await readFile(
      path.join(outputDir, 'backend/src/main/resources/sql/tables.sql'),
      'utf8'
    )
    const projectsPage = await readFile(
      path.join(outputDir, 'frontend/src/features/projects/projects-page.tsx'),
      'utf8'
    )
    assert.equal(servicesSql.includes('project_service'), true)
    assert.equal(servicesSql.includes('kanban_service'), false)
    assert.equal(servicesSql.includes('ticket_service'), false)
    assert.equal(servicesSql.includes('bindProjectRepository'), false)
    assert.equal(servicesSql.includes('switchProjectBranch'), false)
    assert.equal(tablesSql.includes('software_project'), true)
    assert.equal(tablesSql.includes('project_repository_binding'), false)
    assert.equal(projectsPage.includes('Bind repository'), false)
    assert.equal(projectsPage.includes('Switch branch'), false)
    assert.equal(
      projectsPage.includes('Repository binding workflows are not enabled for this derived application assembly.'),
      true
    )
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('project-repository compatibility fixture generates repository wiring without kanban or ticket wiring', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-project-repository-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/compatibility/fixtures/project-repository.manifest.json'),
      outputDir
    })

    const router = await readFile(path.join(outputDir, 'frontend/src/app/router.tsx'), 'utf8')
    const servicesSql = await readFile(
      path.join(outputDir, 'backend/src/main/resources/sql/services.sql'),
      'utf8'
    )
    const tablesSql = await readFile(
      path.join(outputDir, 'backend/src/main/resources/sql/tables.sql'),
      'utf8'
    )
    const projectsPage = await readFile(
      path.join(outputDir, 'frontend/src/features/projects/projects-page.tsx'),
      'utf8'
    )

    assert.equal(router.includes("path: 'projects'"), true)
    assert.equal(router.includes("path: 'kanban'"), false)
    assert.equal(router.includes("path: 'tickets'"), false)
    assert.equal(servicesSql.includes('project_service'), true)
    assert.equal(servicesSql.includes('kanban_service'), false)
    assert.equal(servicesSql.includes('ticket_service'), false)
    assert.equal(servicesSql.includes('bindProjectRepository'), true)
    assert.equal(servicesSql.includes('switchProjectBranch'), true)
    assert.equal(tablesSql.includes('software_project'), true)
    assert.equal(tablesSql.includes('project_repository_binding'), true)
    assert.equal(projectsPage.includes('Bind repository'), true)
    assert.equal(projectsPage.includes('Switch branch'), true)
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('project-kanban compatibility fixture generates kanban without ticket wiring', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-project-kanban-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/compatibility/fixtures/project-kanban.manifest.json'),
      outputDir
    })

    const router = await readFile(path.join(outputDir, 'frontend/src/app/router.tsx'), 'utf8')
    assert.equal(router.includes("path: 'projects'"), true)
    assert.equal(router.includes("path: 'kanban'"), true)
    assert.equal(router.includes("path: 'tickets'"), false)

    const servicesSql = await readFile(
      path.join(outputDir, 'backend/src/main/resources/sql/services.sql'),
      'utf8'
    )
    assert.equal(servicesSql.includes('project_service'), true)
    assert.equal(servicesSql.includes('kanban_service'), true)
    assert.equal(servicesSql.includes('ticket_service'), false)
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
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
      context.contractInputs.aiSolutionInputContract,
      'docs/ai/ai-solution-input-contract.json'
    )
    assert.equal(
      context.contractInputs.solutionToManifestPlanningContract,
      'docs/ai/solution-to-manifest-planning-contract.json'
    )
    assert.equal(
      context.contractInputs.solutionToManifestRecommendationContract,
      'docs/ai/solution-to-manifest-recommendation-contract.json'
    )
    assert.equal(
      context.contractInputs.descriptorDrivenManagementModuleContract,
      'docs/ai/descriptor-driven-management-module-contract.json'
    )
    assert.equal(
      context.contractInputs.descriptorDrivenManagementModuleExample,
      'docs/ai/management-modules/department-directory.management-module.json'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedUpgradeEvaluation,
      './scripts/platform-tool.sh upgrade evaluate <generated-app-dir>'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedUpgradeTargetSelection,
      './scripts/platform-tool.sh upgrade targets [generated-app-dir]'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedReleaseAdvisory,
      './scripts/platform-tool.sh upgrade advisory [generated-app-dir]'
    )
    assert.equal(
      context.lifecycle.repositoryOwnedUpgradeExecution,
      './scripts/platform-tool.sh upgrade execute <generated-app-dir> [--apply]'
    )
    assert.equal(
      context.contractInputs.aiToolOrchestrationContract,
      'docs/ai/ai-tool-orchestration-contract.json'
    )
    assert.equal(
      context.contractInputs.structuredAppTemplateContract,
      'docs/ai/structured-app-template-contract.json'
    )
    assert.equal(
      context.contractInputs.structuredAppTemplateMap,
      'docs/ai/template-classifications/default-derived-app-template-map.json'
    )
    assert.equal(
      context.validation.repositoryOwned,
      './scripts/platform-tool.sh generated-app verify <generated-app-dir>'
    )
    assert.equal(
      context.templateSystem.contract,
      'docs/ai/structured-app-template-contract.json'
    )
    assert.equal(
      context.templateSystem.classificationMap,
      'docs/ai/template-classifications/default-derived-app-template-map.json'
    )
    assert.equal(
      lifecycle.upgradeEvaluation.repositoryOwnedTargetSelectionEntrypoint,
      './scripts/platform-tool.sh upgrade targets [generated-app-dir]'
    )
    assert.equal(
      lifecycle.templateSystem.templateContract,
      'docs/ai/structured-app-template-contract.json'
    )
    assert.equal(
      lifecycle.templateSystem.templateClassificationMap,
      'docs/ai/template-classifications/default-derived-app-template-map.json'
    )
    assert.equal(
      lifecycle.upgradeEvaluation.repositoryOwnedEntrypoint,
      './scripts/platform-tool.sh upgrade evaluate <generated-app-dir>'
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
    assert.ok(readme.includes('## AI Tooling'))
    assert.ok(readme.includes('docs/ai/ai-tool-orchestration-contract.json'))
    assert.ok(readme.includes('./scripts/platform-tool.sh'))
    assert.ok(readme.includes('./scripts/platform-tool.sh generated-app verify'))
    assert.ok(readme.includes('./scripts/platform-tool.sh upgrade evaluate'))
    assert.ok(readme.includes('./scripts/platform-tool.sh upgrade targets'))
    assert.ok(readme.includes('./scripts/platform-tool.sh upgrade advisory'))
    assert.ok(readme.includes('./scripts/platform-tool.sh upgrade execute'))
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
    assert.equal(result.verifierId, 'java-generated-app-verifier')
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
    assert.equal(payload.verifierId, 'java-generated-app-verifier-cli')
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

test('platform-tool façade verifies a generated app through the unified entrypoint', async () => {
  const outputDir = await mkdtemp(path.join(os.tmpdir(), 'fsp-platform-tool-verify-'))

  try {
    await scaffoldDerivedApp({
      manifestPath: path.join(REPO_ROOT, 'docs/ai/manifests/core-admin-app.json'),
      outputDir
    })

    const result = spawnSync(
      path.join(REPO_ROOT, 'scripts/platform-tool.sh'),
      ['generated-app', 'verify', outputDir],
      {
        cwd: REPO_ROOT,
        encoding: 'utf8'
      }
    )

    assert.equal(result.status, 0, result.stderr || result.stdout)
    const payload = JSON.parse(result.stdout.trim())
    assert.equal(payload.verified, true)
    assert.equal(payload.verifierId, 'java-generated-app-verifier')
  } finally {
    await rm(outputDir, { recursive: true, force: true })
  }
})

test('platform-tool façade exposes assembly compatibility verification', () => {
  const result = spawnSync(
    path.join(REPO_ROOT, 'scripts/platform-tool.sh'),
    ['assembly', 'compatibility'],
    {
      cwd: REPO_ROOT,
      encoding: 'utf8'
    }
  )

  assert.equal(result.status, 0, result.stderr || result.stdout)
  const payload = JSON.parse(result.stdout.trim())
  assert.equal(payload.ok, true)
  assert.deepEqual(
    payload.implementations.map((implementation) => implementation.id),
    ['java-cli']
  )
})

test('platform-tool façade exposes upgrade target lookup', () => {
  const result = spawnSync(
    path.join(REPO_ROOT, 'scripts/platform-tool.sh'),
    ['upgrade', 'targets'],
    {
      cwd: REPO_ROOT,
      encoding: 'utf8'
    }
  )

  assert.equal(result.status, 0, result.stderr || result.stdout)
  const payload = JSON.parse(result.stdout.trim().split('\n').at(-1))
  assert.equal(payload.currentReleaseId, 'fast-service-platform/0.1.0-dev')
  assert.ok(Array.isArray(payload.recognizedReleases))
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
