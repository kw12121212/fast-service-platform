import { type FormEvent, useState } from 'react'
import { RefreshCcw } from 'lucide-react'

import { DynamicForm } from '@/components/admin'
import type { FormDescriptor } from '@/components/admin'
import { MutationStatus } from '@/components/admin/mutation-status'
import { PageHeader } from '@/components/admin/page-header'
import { ResourceState } from '@/components/admin/resource-state'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  useBindProjectRepositoryAction,
  useCreateProjectSandboxContainerAction,
  useCreateProjectSandboxImageAction,
  useCreateProjectWorktreeAction,
  useCreateProjectAction,
  useDeleteProjectSandboxContainerAction,
  useDeleteProjectWorktreeAction,
  useMergeProjectWorktreeAction,
  useProjectDerivedAppAssemblyResource,
  useProjectDerivedAppUpgradeSupportResource,
  useProjectDerivedAppVerificationResource,
  useProjectsResource,
  usePruneProjectWorktreesAction,
  useRepairProjectWorktreesAction,
  useRequestProjectDerivedAppAssemblyAction,
  useRequestProjectDerivedAppUpgradeSupportAction,
  useRequestProjectDerivedAppVerificationAction,
  useSwitchProjectBranchAction,
} from '@/lib/api/hooks'
import type {
  ProjectDerivedAppAssemblyContext,
  ProjectDerivedAppUpgradeSupportContext,
  ProjectDerivedAppVerificationContext,
  ProjectRepositorySummary,
  ProjectWorktreeSummary,
  SoftwareProject,
} from '@/lib/api/types'

function repositoryStateTone(
  workingTreeState: string,
  headState: string | null | undefined,
) {
  if (headState === 'DETACHED') {
    return 'bg-rose-500/12 text-rose-700'
  }

  if (workingTreeState === 'DIRTY') {
    return 'bg-amber-500/14 text-amber-700'
  }

  return 'bg-emerald-500/12 text-emerald-700'
}

function currentRefLabel(project: SoftwareProject) {
  if (!project.repository) {
    return null
  }

  if (project.repository.headState === 'DETACHED') {
    return 'Detached HEAD'
  }

  return project.repository.branch
}

function branchSwitchRestriction(project: SoftwareProject) {
  if (!project.repository) {
    return 'Bind a repository first to inspect local branches and switch Git context.'
  }

  if (project.repository.headState === 'DETACHED') {
    return 'Branch switching is unavailable while the repository is in detached HEAD state.'
  }

  if (project.repository.workingTreeState === 'DIRTY') {
    return 'Branch switching is unavailable while the working tree has uncommitted changes.'
  }

  if (project.repository.availableBranches.length === 0) {
    return 'No local branches are available for this repository.'
  }

  return null
}

function availableWorktreeBranches(repository: ProjectRepositorySummary) {
  const assignedBranches = new Set(
    repository.worktrees
      .map((worktree) => worktree.branch)
      .filter((branch): branch is string => branch !== null),
  )

  return repository.availableBranches.filter((branch) => !assignedBranches.has(branch))
}

function worktreeCreationRestriction(repository: ProjectRepositorySummary | null) {
  if (!repository) {
    return 'Bind a repository first to create and maintain project worktrees.'
  }

  if (repository.headState === 'DETACHED') {
    return 'Worktree creation is unavailable while the repository is in detached HEAD state.'
  }

  if (availableWorktreeBranches(repository).length === 0) {
    return 'Every visible local branch already has a worktree.'
  }

  return null
}

function worktreeTone(worktree: ProjectWorktreeSummary) {
  if (worktree.main) {
    return 'bg-primary/12 text-primary'
  }
  if (worktree.stale) {
    return 'bg-rose-500/12 text-rose-700'
  }
  if (worktree.workingTreeState === 'DIRTY' || worktree.hasUnpushedCommits) {
    return 'bg-amber-500/14 text-amber-700'
  }
  return 'bg-emerald-500/12 text-emerald-700'
}

function worktreeStatusLabel(worktree: ProjectWorktreeSummary) {
  if (worktree.main) {
    return 'Main worktree'
  }
  if (worktree.stale) {
    return 'Stale record'
  }
  if (worktree.workingTreeState === 'DIRTY') {
    return 'Dirty'
  }
  if (worktree.hasUnpushedCommits) {
    return 'Unpushed commits'
  }
  if (!worktree.hasUpstream) {
    return 'No upstream'
  }
  return 'Linked worktree'
}

function worktreeRefLabel(worktree: ProjectWorktreeSummary) {
  if (worktree.headState === 'DETACHED') {
    return 'Detached HEAD'
  }
  return worktree.branch ?? 'Unknown ref'
}

function sandboxTone(worktree: ProjectWorktreeSummary) {
  if (!worktree.sandbox.supported || worktree.sandbox.imageStatus === 'FAILED') {
    return 'bg-rose-500/12 text-rose-700'
  }
  if (worktree.sandbox.containerStatus === 'ACTIVE') {
    return 'bg-primary/12 text-primary'
  }
  if (worktree.sandbox.containerStatus === 'FAILED') {
    return 'bg-amber-500/14 text-amber-700'
  }
  if (worktree.sandbox.imageStatus === 'READY') {
    return 'bg-emerald-500/12 text-emerald-700'
  }
  return 'bg-muted text-muted-foreground'
}

function sandboxStatusLabel(worktree: ProjectWorktreeSummary) {
  if (!worktree.sandbox.supported) {
    return 'Restricted'
  }
  if (worktree.sandbox.containerStatus === 'ACTIVE') {
    return 'Container active'
  }
  if (worktree.sandbox.containerStatus === 'FAILED') {
    return 'Container failed'
  }
  if (worktree.sandbox.imageStatus === 'FAILED') {
    return 'Image failed'
  }
  if (worktree.sandbox.imageStatus === 'READY') {
    return 'Image ready'
  }
  return 'Image missing'
}

function scriptSourceLabel(source: string) {
  return source === 'WORKTREE_PROPERTY' ? 'Worktree property' : 'Default path'
}

type ProjectRepositoryCardProps = {
  project: SoftwareProject
  onRepositoryBound: () => void
}

function formatAssemblyOutcomeLabel(context: ProjectDerivedAppAssemblyContext | null) {
  const latestOutcome = context?.latestOutcome
  if (!latestOutcome) {
    return 'No assembly run yet'
  }

  if (latestOutcome.status === 'SUCCESS') {
    return 'Latest run succeeded'
  }

  return latestOutcome.category === 'REQUEST_VALIDATION'
    ? 'Latest request was rejected'
    : 'Latest run failed'
}

function assemblyOutcomeTone(context: ProjectDerivedAppAssemblyContext | null) {
  const latestOutcome = context?.latestOutcome
  if (!latestOutcome) {
    return 'bg-muted text-muted-foreground'
  }

  if (latestOutcome.status === 'SUCCESS') {
    return 'bg-emerald-500/12 text-emerald-700'
  }

  return latestOutcome.category === 'REQUEST_VALIDATION'
    ? 'bg-amber-500/14 text-amber-700'
    : 'bg-rose-500/12 text-rose-700'
}

function formatVerificationOutcomeLabel(
  context: ProjectDerivedAppVerificationContext | null,
) {
  const latestOutcome = context?.latestOutcome
  if (!latestOutcome) {
    return 'No verification run yet'
  }

  if (latestOutcome.status === 'SUCCESS') {
    return 'Latest validation succeeded'
  }

  return latestOutcome.category === 'REQUEST_VALIDATION'
    ? 'Latest validation was rejected'
    : 'Latest validation failed'
}

function verificationOutcomeTone(
  context: ProjectDerivedAppVerificationContext | null,
) {
  const latestOutcome = context?.latestOutcome
  if (!latestOutcome) {
    return 'bg-muted text-muted-foreground'
  }

  if (latestOutcome.status === 'SUCCESS') {
    return 'bg-emerald-500/12 text-emerald-700'
  }

  return latestOutcome.category === 'REQUEST_VALIDATION'
    ? 'bg-amber-500/14 text-amber-700'
    : 'bg-rose-500/12 text-rose-700'
}

function formatUpgradeSupportOutcomeLabel(
  context: ProjectDerivedAppUpgradeSupportContext | null,
) {
  const latestOutcome = context?.latestOutcome
  if (!latestOutcome) {
    return 'No upgrade check yet'
  }

  if (latestOutcome.status === 'SUCCESS') {
    return 'Latest upgrade check succeeded'
  }

  return latestOutcome.category === 'REQUEST_VALIDATION'
    ? 'Latest upgrade check was rejected'
    : 'Latest upgrade check failed'
}

function upgradeSupportOutcomeTone(
  context: ProjectDerivedAppUpgradeSupportContext | null,
) {
  const latestOutcome = context?.latestOutcome
  if (!latestOutcome) {
    return 'bg-muted text-muted-foreground'
  }

  if (latestOutcome.status === 'SUCCESS') {
    return 'bg-emerald-500/12 text-emerald-700'
  }

  return latestOutcome.category === 'REQUEST_VALIDATION'
    ? 'bg-amber-500/14 text-amber-700'
    : 'bg-rose-500/12 text-rose-700'
}

function formatUpgradeSupportRequestLabel(requestType: string) {
  if (requestType === 'SUPPORTED_TARGETS') {
    return 'Supported targets'
  }
  if (requestType === 'ADVISORY') {
    return 'Release advisory'
  }
  if (requestType === 'EVALUATE') {
    return 'Compatibility evaluation'
  }
  if (requestType === 'DRY_RUN_EXECUTE') {
    return 'Dry-run plan'
  }
  return requestType
}

function ProjectDerivedAppUpgradeSupportCard({
  project,
  refreshNonce,
}: {
  project: SoftwareProject
  refreshNonce: number
}) {
  const upgradeSupport = useProjectDerivedAppUpgradeSupportResource(
    project.id,
    project.repository?.rootPath ?? null,
    refreshNonce,
  )
  const requestUpgradeSupport = useRequestProjectDerivedAppUpgradeSupportAction()
  const [selectedTargetReleaseId, setSelectedTargetReleaseId] = useState('')
  const [loadedTargetReleases, setLoadedTargetReleases] = useState<
    Array<{ releaseId: string; supportStatus?: string }>
  >([])
  const [loadedDefaultTargetReleaseId, setLoadedDefaultTargetReleaseId] =
    useState('')

  const context = upgradeSupport.data
  const latestOutcome = context?.latestOutcome ?? null
  const latestResult = latestOutcome?.result

  function readSupportedTargets(contextValue: ProjectDerivedAppUpgradeSupportContext | null) {
    const latest = contextValue?.latestOutcome
    const result = latest?.result
    if (
      latest?.requestType !== 'SUPPORTED_TARGETS' ||
      !result ||
      !('availableTargetReleases' in result) ||
      !Array.isArray(result.availableTargetReleases)
    ) {
      return {
        targets: [] as Array<{ releaseId: string; supportStatus?: string }>,
        defaultTargetReleaseId: '',
      }
    }

    return {
      targets: result.availableTargetReleases
        .filter(
          (entry): entry is { releaseId: string; supportStatus?: string } =>
            typeof entry === 'object' &&
            entry !== null &&
            'releaseId' in entry &&
            typeof entry.releaseId === 'string',
        )
        .map((entry) => ({
          releaseId: entry.releaseId,
          supportStatus:
            'supportStatus' in entry && typeof entry.supportStatus === 'string'
              ? entry.supportStatus
              : undefined,
        })),
      defaultTargetReleaseId:
        'defaultTargetReleaseId' in result &&
        typeof result.defaultTargetReleaseId === 'string'
          ? result.defaultTargetReleaseId
          : '',
    }
  }

  async function handleRequest(requestType: string) {
    const targetReleaseId =
      requestType === 'DRY_RUN_EXECUTE' ? targetReleaseValue || null : null

    try {
      const nextContext = await requestUpgradeSupport.submit({
        projectId: project.id,
        requestType,
        targetReleaseId,
      })
      if (requestType === 'SUPPORTED_TARGETS') {
        const supportedTargets = readSupportedTargets(nextContext)
        setLoadedTargetReleases(supportedTargets.targets)
        setLoadedDefaultTargetReleaseId(supportedTargets.defaultTargetReleaseId)
        if (
          selectedTargetReleaseId === '' &&
          supportedTargets.defaultTargetReleaseId !== ''
        ) {
          setSelectedTargetReleaseId(supportedTargets.defaultTargetReleaseId)
        }
      }
      upgradeSupport.reload()
    } catch {
      upgradeSupport.reload()
    }
  }

  const latestSupportedTargets = readSupportedTargets(context)
  const availableTargets =
    latestSupportedTargets.targets.length > 0
      ? latestSupportedTargets.targets
      : loadedTargetReleases
  const defaultTargetReleaseId =
    latestSupportedTargets.defaultTargetReleaseId || loadedDefaultTargetReleaseId
  const targetReleaseValue = availableTargets.some(
    (entry) => entry.releaseId === selectedTargetReleaseId,
  )
    ? selectedTargetReleaseId
    : defaultTargetReleaseId

  return (
    <div className="space-y-4 rounded-[20px] border border-border/60 bg-background/80 p-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
            Derived-app upgrade support
          </div>
          <div className="mt-2 text-sm text-muted-foreground">
            Inspect supported target releases, advisory guidance, compatibility,
            and repository-owned dry-run upgrade planning for this project&apos;s
            latest successful derived-app assembly output.
          </div>
        </div>
        <Badge className={upgradeSupportOutcomeTone(context)}>
          {formatUpgradeSupportOutcomeLabel(context)}
        </Badge>
      </div>

      <ResourceState
        status={upgradeSupport.status}
        error={upgradeSupport.error}
        empty={false}
        emptyTitle="Upgrade support context unavailable"
        emptyMessage="Refresh the project upgrade-support state to inspect derived-app upgrade readiness."
        onRetry={upgradeSupport.reload}
        skeletonCount={2}
      >
        <div className="space-y-4">
          {context?.restricted ? (
            <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
              {context.restriction}
            </div>
          ) : context ? (
            <>
              <div className="rounded-[18px] border border-border/60 bg-muted/24 p-3 text-sm text-muted-foreground">
                Source context{' '}
                <span className="font-medium text-foreground">
                  {context.sourceContext.type}
                </span>
                <br />
                Upgrade target{' '}
                <span className="font-medium text-foreground">
                  {context.targetContext.type}
                </span>
                <br />
                Latest successful assembly output{' '}
                <span className="font-medium text-foreground break-all">
                  {context.targetContext.outputDirectory}
                </span>
              </div>

              <MutationStatus
                status={requestUpgradeSupport.status}
                error={requestUpgradeSupport.error}
                submittingMessage="Running repository-owned derived-app upgrade support for this project..."
                successMessage="Derived-app upgrade support completed and the latest project outcome has been refreshed."
              />

              <div className="flex flex-wrap gap-3">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => handleRequest('SUPPORTED_TARGETS')}
                  disabled={requestUpgradeSupport.status === 'submitting'}
                >
                  Load supported targets
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => handleRequest('ADVISORY')}
                  disabled={requestUpgradeSupport.status === 'submitting'}
                >
                  Review advisory
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => handleRequest('EVALUATE')}
                  disabled={requestUpgradeSupport.status === 'submitting'}
                >
                  Evaluate upgrade
                </Button>
              </div>

              <div className="space-y-3 rounded-[18px] border border-border/60 bg-muted/24 p-4 text-sm text-muted-foreground">
                <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                  Dry-run upgrade plan
                </div>
                <div className="space-y-2">
                  <Label htmlFor={`project-upgrade-target-${project.id}`}>
                    Supported target release
                  </Label>
                  <select
                    id={`project-upgrade-target-${project.id}`}
                    className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none ring-0 transition-colors focus-visible:border-ring"
                    value={targetReleaseValue}
                    onChange={(event) => setSelectedTargetReleaseId(event.target.value)}
                    disabled={availableTargets.length === 0}
                  >
                    {availableTargets.length === 0 ? (
                      <option value="">Load supported targets first</option>
                    ) : (
                      availableTargets.map((entry) => {
                        return (
                          <option key={entry.releaseId} value={entry.releaseId}>
                            {entry.releaseId}
                          </option>
                        )
                      })
                    )}
                  </select>
                </div>
                <Button
                  type="button"
                  onClick={() => handleRequest('DRY_RUN_EXECUTE')}
                  disabled={
                    requestUpgradeSupport.status === 'submitting' ||
                    availableTargets.length === 0 ||
                    targetReleaseValue.trim() === ''
                  }
                >
                  Generate dry-run plan
                </Button>
              </div>
            </>
          ) : null}

          {latestOutcome ? (
            <div className="space-y-3 rounded-[18px] border border-border/60 bg-muted/24 p-4 text-sm text-muted-foreground">
              <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                Latest visible upgrade outcome
              </div>
              <div>
                Request{' '}
                <span className="font-medium text-foreground">
                  {formatUpgradeSupportRequestLabel(latestOutcome.requestType)}
                </span>
              </div>
              <div>
                Outcome{' '}
                <span className="font-medium text-foreground">
                  {latestOutcome.status}
                </span>
                {' · '}
                <span className="font-medium text-foreground">
                  {latestOutcome.category}
                </span>
              </div>
              <div>{latestOutcome.message}</div>
              {latestOutcome.targetReleaseId ? (
                <div>
                  Target release{' '}
                  <span className="font-medium text-foreground break-all">
                    {latestOutcome.targetReleaseId}
                  </span>
                </div>
              ) : null}
              {latestOutcome.targetOutputDirectory ? (
                <div>
                  Derived-app output{' '}
                  <span className="font-medium text-foreground break-all">
                    {latestOutcome.targetOutputDirectory}
                  </span>
                </div>
              ) : null}

              {latestOutcome.requestType === 'SUPPORTED_TARGETS' &&
              latestResult &&
              'availableTargetReleases' in latestResult &&
              Array.isArray(latestResult.availableTargetReleases) ? (
                <div className="space-y-2">
                  <div className="font-medium text-foreground">Available target releases</div>
                  {latestResult.availableTargetReleases.map((entry, index) => {
                    if (typeof entry !== 'object' || entry === null) {
                      return null
                    }
                    return (
                      <div
                        key={
                          'releaseId' in entry && typeof entry.releaseId === 'string'
                            ? entry.releaseId
                            : `target-${index}`
                        }
                      >
                        <span className="font-medium text-foreground">
                          {'releaseId' in entry ? String(entry.releaseId) : 'Unknown target'}
                        </span>
                        {' · '}
                        {'supportStatus' in entry
                          ? String(entry.supportStatus ?? 'unknown')
                          : 'unknown'}
                      </div>
                    )
                  })}
                </div>
              ) : null}

              {latestOutcome.requestType === 'ADVISORY' &&
              latestResult &&
              'summary' in latestResult ? (
                <>
                  <div>
                    Advisory summary{' '}
                    <span className="font-medium text-foreground">
                      {String(latestResult.summary)}
                    </span>
                  </div>
                  {'recommendedChecks' in latestResult &&
                  Array.isArray(latestResult.recommendedChecks) ? (
                    <div>
                      Recommended checks{' '}
                      <span className="font-medium text-foreground">
                        {latestResult.recommendedChecks.join(', ')}
                      </span>
                    </div>
                  ) : null}
                </>
              ) : null}

              {latestOutcome.requestType === 'EVALUATE' &&
              latestResult &&
              'compatible' in latestResult ? (
                <>
                  <div>
                    Compatibility{' '}
                    <span className="font-medium text-foreground">
                      {String(latestResult.compatible)}
                    </span>
                  </div>
                  {'recommendedAction' in latestResult ? (
                    <div>
                      Recommended action{' '}
                      <span className="font-medium text-foreground">
                        {String(latestResult.recommendedAction)}
                      </span>
                    </div>
                  ) : null}
                </>
              ) : null}

              {latestOutcome.requestType === 'DRY_RUN_EXECUTE' &&
              latestResult &&
              'planVersion' in latestResult ? (
                <>
                  <div>
                    Dry run{' '}
                    <span className="font-medium text-foreground">
                      {String(latestResult.dryRun)}
                    </span>
                    {' · '}Compatible{' '}
                    <span className="font-medium text-foreground">
                      {String(latestResult.compatible)}
                    </span>
                  </div>
                  {'autoApplyItems' in latestResult &&
                  Array.isArray(latestResult.autoApplyItems) ? (
                    <div>
                      Auto-apply items{' '}
                      <span className="font-medium text-foreground">
                        {latestResult.autoApplyItems.length}
                      </span>
                    </div>
                  ) : null}
                  {'manualInterventionItems' in latestResult &&
                  Array.isArray(latestResult.manualInterventionItems) ? (
                    <div>
                      Manual intervention items{' '}
                      <span className="font-medium text-foreground">
                        {latestResult.manualInterventionItems.length}
                      </span>
                    </div>
                  ) : null}
                </>
              ) : null}
            </div>
          ) : null}
        </div>
      </ResourceState>
    </div>
  )
}

function ProjectDerivedAppAssemblyCard({
  project,
  onAssemblyChanged,
}: {
  project: SoftwareProject
  onAssemblyChanged: () => void
}) {
  const assembly = useProjectDerivedAppAssemblyResource(
    project.id,
    project.repository?.rootPath ?? null,
  )
  const requestAssembly = useRequestProjectDerivedAppAssemblyAction()
  const [manifestJson, setManifestJson] = useState(`{
  "schemaVersion": "fsp-app-manifest/v1",
  "application": {
    "id": "${project.key.toLowerCase()}-admin-console",
    "name": "${project.name}",
    "packagePrefix": "com.fastservice.platform.derived"
  },
  "modules": [
    "admin-shell",
    "user-management",
    "role-permission-management",
    "project-management"
  ]
}`)
  const [outputDirectory, setOutputDirectory] = useState('')

  async function handleAssemblyRequest(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      await requestAssembly.submit({
        projectId: project.id,
        manifestJson,
        outputDirectory,
      })
      assembly.reload()
      onAssemblyChanged()
    } catch {
      assembly.reload()
      onAssemblyChanged()
    }
  }

  const context = assembly.data
  const latestOutcome = context?.latestOutcome ?? null

  return (
    <div className="space-y-4 rounded-[20px] border border-border/60 bg-background/80 p-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
            Derived-app assembly
          </div>
          <div className="mt-2 text-sm text-muted-foreground">
            Trigger repository-owned app assembly from this project&apos;s bound main repository context.
          </div>
        </div>
        <Badge className={assemblyOutcomeTone(context)}>
          {formatAssemblyOutcomeLabel(context)}
        </Badge>
      </div>

      <ResourceState
        status={assembly.status}
        error={assembly.error}
        empty={false}
        emptyTitle="Assembly context unavailable"
        emptyMessage="Refresh the project assembly state to inspect derived-app assembly availability."
        onRetry={assembly.reload}
        skeletonCount={2}
      >
        <div className="space-y-4">
          {context?.restricted ? (
            <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
              {context.restriction}
            </div>
          ) : context ? (
            <>
              <div className="rounded-[18px] border border-border/60 bg-muted/24 p-3 text-sm text-muted-foreground">
                Source context{' '}
                <span className="font-medium text-foreground">
                  {context.sourceContext.type}
                </span>
                <br />
                Repository root{' '}
                <span className="font-medium text-foreground">
                  {context.sourceRepositoryPath}
                </span>
              </div>

              <form className="space-y-4" onSubmit={handleAssemblyRequest}>
                <div className="space-y-2">
                  <Label htmlFor={`project-assembly-manifest-${project.id}`}>
                    App manifest JSON
                  </Label>
                  <textarea
                    id={`project-assembly-manifest-${project.id}`}
                    className="min-h-48 w-full rounded-lg border border-input bg-background px-3 py-2 font-mono text-sm outline-none transition-colors focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50"
                    value={manifestJson}
                    onChange={(event) => setManifestJson(event.target.value)}
                    spellCheck={false}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor={`project-assembly-output-${project.id}`}>
                    Output directory
                  </Label>
                  <Input
                    id={`project-assembly-output-${project.id}`}
                    value={outputDirectory}
                    onChange={(event) => setOutputDirectory(event.target.value)}
                    placeholder="/absolute/path/to/generated-app"
                    required
                  />
                </div>

                <MutationStatus
                  status={requestAssembly.status}
                  error={requestAssembly.error}
                  submittingMessage="Running repository-owned derived-app assembly for this project..."
                  successMessage="Derived-app assembly completed and the latest project outcome has been refreshed."
                />

                <Button
                  type="submit"
                  disabled={requestAssembly.status === 'submitting'}
                >
                  Run derived-app assembly
                </Button>
              </form>
            </>
          ) : null}

          {latestOutcome ? (
            <div className="space-y-3 rounded-[18px] border border-border/60 bg-muted/24 p-4 text-sm text-muted-foreground">
              <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                Latest visible outcome
              </div>
              <div>
                Outcome{' '}
                <span className="font-medium text-foreground">
                  {latestOutcome.status}
                </span>
                {' · '}
                <span className="font-medium text-foreground">
                  {latestOutcome.category}
                </span>
              </div>
              <div>{latestOutcome.message}</div>
              {latestOutcome.manifestAppId ? (
                <div>
                  App id{' '}
                  <span className="font-medium text-foreground">
                    {latestOutcome.manifestAppId}
                  </span>
                </div>
              ) : null}
              {latestOutcome.outputDirectory ? (
                <div>
                  Output directory{' '}
                  <span className="font-medium text-foreground break-all">
                    {latestOutcome.outputDirectory}
                  </span>
                </div>
              ) : null}
            </div>
          ) : null}
        </div>
      </ResourceState>
    </div>
  )
}

function ProjectDerivedAppVerificationCard({
  project,
  refreshNonce,
}: {
  project: SoftwareProject
  refreshNonce: number
}) {
  const verification = useProjectDerivedAppVerificationResource(
    project.id,
    project.repository?.rootPath ?? null,
    refreshNonce,
  )
  const requestVerification = useRequestProjectDerivedAppVerificationAction()

  async function handleVerificationRequest() {
    try {
      await requestVerification.submit({
        projectId: project.id,
      })
      verification.reload()
    } catch {
      verification.reload()
    }
  }

  const context = verification.data
  const latestOutcome = context?.latestOutcome ?? null

  return (
    <div className="space-y-4 rounded-[20px] border border-border/60 bg-background/80 p-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
            Derived-app verification
          </div>
          <div className="mt-2 text-sm text-muted-foreground">
            Run repository-owned generated-app verification and runtime smoke against this project&apos;s latest successful derived-app assembly output.
          </div>
        </div>
        <Badge className={verificationOutcomeTone(context)}>
          {formatVerificationOutcomeLabel(context)}
        </Badge>
      </div>

      <ResourceState
        status={verification.status}
        error={verification.error}
        empty={false}
        emptyTitle="Verification context unavailable"
        emptyMessage="Refresh the project verification state to inspect derived-app validation availability."
        onRetry={verification.reload}
        skeletonCount={2}
      >
        <div className="space-y-4">
          {context?.restricted ? (
            <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
              {context.restriction}
            </div>
          ) : context ? (
            <>
              <div className="rounded-[18px] border border-border/60 bg-muted/24 p-3 text-sm text-muted-foreground">
                Source context{' '}
                <span className="font-medium text-foreground">
                  {context.sourceContext.type}
                </span>
                <br />
                Verification target{' '}
                <span className="font-medium text-foreground">
                  {context.targetContext.type}
                </span>
                <br />
                Latest successful assembly output{' '}
                <span className="font-medium text-foreground break-all">
                  {context.targetContext.outputDirectory}
                </span>
              </div>

              <MutationStatus
                status={requestVerification.status}
                error={requestVerification.error}
                submittingMessage="Running repository-owned generated-app verification and runtime smoke for this project..."
                successMessage="Derived-app verification completed and the latest project validation outcome has been refreshed."
              />

              <Button
                type="button"
                onClick={handleVerificationRequest}
                disabled={requestVerification.status === 'submitting'}
              >
                Run derived-app verification
              </Button>
            </>
          ) : null}

          {latestOutcome ? (
            <div className="space-y-3 rounded-[18px] border border-border/60 bg-muted/24 p-4 text-sm text-muted-foreground">
              <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                Latest visible validation outcome
              </div>
              <div>
                Outcome{' '}
                <span className="font-medium text-foreground">
                  {latestOutcome.status}
                </span>
                {' · '}
                <span className="font-medium text-foreground">
                  {latestOutcome.category}
                </span>
              </div>
              <div>{latestOutcome.message}</div>
              {latestOutcome.targetOutputDirectory ? (
                <div>
                  Verified output{' '}
                  <span className="font-medium text-foreground break-all">
                    {latestOutcome.targetOutputDirectory}
                  </span>
                </div>
              ) : null}
              <div>
                Generated-app verification{' '}
                <span className="font-medium text-foreground">
                  {latestOutcome.generatedAppVerification.status}
                </span>
                {' · '}
                {latestOutcome.generatedAppVerification.message}
              </div>
              <div>
                Runtime smoke{' '}
                <span className="font-medium text-foreground">
                  {latestOutcome.runtimeSmoke.status}
                </span>
                {' · '}
                {latestOutcome.runtimeSmoke.message}
              </div>
            </div>
          ) : null}
        </div>
      </ResourceState>
    </div>
  )
}

function ProjectRepositoryCard({
  project,
  onRepositoryBound,
}: ProjectRepositoryCardProps) {
  const bindProjectRepository = useBindProjectRepositoryAction()
  const switchProjectBranch = useSwitchProjectBranchAction()
  const createProjectWorktree = useCreateProjectWorktreeAction()
  const mergeProjectWorktree = useMergeProjectWorktreeAction()
  const deleteProjectWorktree = useDeleteProjectWorktreeAction()
  const createProjectSandboxImage = useCreateProjectSandboxImageAction()
  const createProjectSandboxContainer = useCreateProjectSandboxContainerAction()
  const deleteProjectSandboxContainer = useDeleteProjectSandboxContainerAction()
  const repairProjectWorktrees = useRepairProjectWorktreesAction()
  const pruneProjectWorktrees = usePruneProjectWorktreesAction()
  const [repositoryPath, setRepositoryPath] = useState(
    project.repository?.rootPath ?? '',
  )
  const [targetBranch, setTargetBranch] = useState(
    project.repository?.branch ?? project.repository?.availableBranches[0] ?? '',
  )
  const [worktreeBranch, setWorktreeBranch] = useState(
    project.repository ? availableWorktreeBranches(project.repository)[0] ?? '' : '',
  )
  const [mergeTargets, setMergeTargets] = useState<Record<string, string>>({})
  const [activeMergeWorktreePath, setActiveMergeWorktreePath] = useState<string | null>(
    null,
  )
  const [activeSandboxImageWorktreePath, setActiveSandboxImageWorktreePath] = useState<
    string | null
  >(null)
  const [activeSandboxContainerWorktreePath, setActiveSandboxContainerWorktreePath] =
    useState<string | null>(null)
  const [activeSandboxDeleteWorktreePath, setActiveSandboxDeleteWorktreePath] = useState<
    string | null
  >(null)
  const [derivedAppLifecycleRefreshVersion, setDerivedAppLifecycleRefreshVersion] =
    useState(0)
  const repositoryPathValue = repositoryPath || project.repository?.rootPath || ''
  const targetBranchValue =
    project.repository && project.repository.availableBranches.includes(targetBranch)
      ? targetBranch
      : project.repository?.branch ?? project.repository?.availableBranches[0] ?? ''
  const creatableWorktreeBranches = project.repository
    ? availableWorktreeBranches(project.repository)
    : []
  const worktreeBranchValue = creatableWorktreeBranches.includes(worktreeBranch)
    ? worktreeBranch
    : (creatableWorktreeBranches[0] ?? '')

  async function handleBindRepository(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    try {
      const boundRootPath = await bindProjectRepository.submit({
        projectId: project.id,
        repositoryPath: repositoryPathValue.trim(),
      })
      setRepositoryPath(boundRootPath)
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleSwitchBranch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!targetBranchValue.trim()) {
      return
    }

    try {
      await switchProjectBranch.submit({
        projectId: project.id,
        branchName: targetBranchValue,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleCreateWorktree(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!worktreeBranchValue.trim()) {
      return
    }

    try {
      await createProjectWorktree.submit({
        projectId: project.id,
        branchName: worktreeBranchValue,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleDeleteWorktree(worktreePath: string) {
    try {
      await deleteProjectWorktree.submit({
        projectId: project.id,
        worktreePath,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleMergeWorktree(
    event: FormEvent<HTMLFormElement>,
    worktreePath: string,
    targetBranchValue: string,
  ) {
    event.preventDefault()

    if (!targetBranchValue.trim()) {
      return
    }

    setActiveMergeWorktreePath(worktreePath)

    try {
      await mergeProjectWorktree.submit({
        projectId: project.id,
        worktreePath,
        targetBranch: targetBranchValue,
      })
      onRepositoryBound()
    } catch {
      onRepositoryBound()
      return
    }
  }

  async function handleRepairWorktrees() {
    try {
      await repairProjectWorktrees.submit({
        projectId: project.id,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handlePruneWorktrees() {
    try {
      await pruneProjectWorktrees.submit({
        projectId: project.id,
      })
      onRepositoryBound()
    } catch {
      return
    }
  }

  async function handleCreateSandboxImage(worktreePath: string) {
    setActiveSandboxImageWorktreePath(worktreePath)
    try {
      await createProjectSandboxImage.submit({
        projectId: project.id,
        worktreePath,
      })
      onRepositoryBound()
    } catch {
      onRepositoryBound()
      return
    }
  }

  async function handleCreateSandboxContainer(worktreePath: string) {
    setActiveSandboxContainerWorktreePath(worktreePath)
    try {
      await createProjectSandboxContainer.submit({
        projectId: project.id,
        worktreePath,
      })
      onRepositoryBound()
    } catch {
      onRepositoryBound()
      return
    }
  }

  async function handleDeleteSandboxContainer(worktreePath: string) {
    setActiveSandboxDeleteWorktreePath(worktreePath)
    try {
      await deleteProjectSandboxContainer.submit({
        projectId: project.id,
        worktreePath,
      })
      onRepositoryBound()
    } catch {
      onRepositoryBound()
      return
    }
  }

  const branchRestriction = branchSwitchRestriction(project)
  const currentRef = currentRefLabel(project)
  const createRestriction = worktreeCreationRestriction(project.repository)

  return (
    <Card className="overflow-hidden border-border/70 bg-[linear-gradient(155deg,rgba(255,255,255,0.96),rgba(246,245,238,0.92))]">
      <CardContent className="space-y-6 p-6">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
              Project key
            </div>
            <div className="mt-2 text-2xl font-semibold tracking-tight">
              {project.key}
            </div>
            <div className="mt-1 text-base text-muted-foreground">
              {project.name}
            </div>
          </div>
          <Badge
            className={
              project.active
                ? 'bg-primary/12 text-primary'
                : 'bg-muted text-muted-foreground'
            }
          >
            {project.active ? 'Active' : 'Inactive'}
          </Badge>
        </div>

        <div className="rounded-[22px] border border-border/60 bg-muted/35 p-4 text-sm leading-6 text-muted-foreground">
          Project id <span className="font-semibold text-foreground">{project.id}</span>{' '}
          is used by the current ticket and kanban endpoints as the project scope
          boundary.
        </div>

        {project.repository ? (
          <div className="rounded-[22px] border border-border/60 bg-background/80 p-4">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div>
                <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                  Bound repository
                </div>
                <div className="mt-2 text-sm font-medium text-foreground">
                  {project.repository.rootPath}
                </div>
              </div>
              <Badge
                className={repositoryStateTone(
                  project.repository.workingTreeState,
                  project.repository.headState,
                )}
              >
                {project.repository.headState === 'DETACHED'
                  ? 'DETACHED HEAD'
                  : project.repository.workingTreeState}
              </Badge>
            </div>

            <div className="mt-4 grid gap-3 text-sm text-muted-foreground">
              <div>
                Current ref{' '}
                <span className="font-medium text-foreground">
                  {currentRef}
                </span>
              </div>
              <div>
                Latest commit{' '}
                <span className="font-medium text-foreground">
                  {project.repository.latestCommitSummary}
                </span>
              </div>
            </div>

            <div className="mt-5 space-y-3 rounded-[20px] border border-border/60 bg-muted/28 p-4">
              <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                Recent commits
              </div>
              {project.repository.recentCommits.length > 0 ? (
                <div className="space-y-2">
                  {project.repository.recentCommits.map((commit) => (
                    <div
                      key={`${project.id}:${commit.hash}`}
                      className="flex flex-wrap items-center gap-2 text-sm"
                    >
                      <span className="rounded-full bg-background px-2 py-0.5 font-mono text-xs text-foreground">
                        {commit.hash}
                      </span>
                      <span className="text-muted-foreground">{commit.summary}</span>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-sm text-muted-foreground">
                  No commits available for this repository yet.
                </div>
              )}
            </div>

            <div className="space-y-4 rounded-[20px] border border-border/60 bg-background/80 p-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                    Local branches
                  </div>
                  <div className="mt-2 text-sm text-muted-foreground">
                    Select an existing local branch to move this project&apos;s Git
                    context.
                  </div>
                </div>
                <Badge className="bg-primary/10 text-primary">
                  {project.repository.availableBranches.length} branches
                </Badge>
              </div>

              <div className="flex flex-wrap gap-2">
                {project.repository.availableBranches.map((branch) => (
                  <Badge
                    key={`${project.id}:${branch}`}
                    className={
                      branch === project.repository?.branch
                        ? 'bg-primary/12 text-primary'
                        : 'bg-muted text-muted-foreground'
                    }
                  >
                    {branch}
                  </Badge>
                ))}
              </div>

              {branchRestriction ? (
                <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                  {branchRestriction}
                </div>
              ) : (
                <form className="space-y-4" onSubmit={handleSwitchBranch}>
                  <div className="space-y-2">
                    <Label htmlFor={`switch-project-branch-${project.id}`}>
                      Local branch
                    </Label>
                    <select
                      id={`switch-project-branch-${project.id}`}
                      className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none ring-0 transition-colors focus-visible:border-ring"
                      value={targetBranchValue}
                      onChange={(event) => setTargetBranch(event.target.value)}
                    >
                      {project.repository.availableBranches.map((branch) => (
                        <option key={branch} value={branch}>
                          {branch}
                        </option>
                      ))}
                    </select>
                  </div>

                  <MutationStatus
                    status={switchProjectBranch.status}
                    error={switchProjectBranch.error}
                    submittingMessage="Switching project branch through the backend project service..."
                    successMessage="Branch switched and the project list has been refreshed."
                  />

                  <Button
                    type="submit"
                    disabled={switchProjectBranch.status === 'submitting'}
                  >
                    Switch branch
                  </Button>
                </form>
              )}
            </div>

            <div className="space-y-4 rounded-[20px] border border-border/60 bg-background/80 p-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                    Worktree inventory
                  </div>
                  <div className="mt-2 text-sm text-muted-foreground">
                    Inspect linked worktrees, create branch-specific workspaces,
                    and run repair or prune from the current Projects experience.
                  </div>
                </div>
                <Badge className="bg-primary/10 text-primary">
                  {project.repository.worktrees.length} worktrees
                </Badge>
              </div>

              <div className="space-y-3">
                {project.repository.worktrees.map((worktree) => (
                  <div
                    key={`${project.id}:${worktree.path}`}
                    className="rounded-[18px] border border-border/60 bg-muted/24 p-4"
                  >
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div>
                        <div className="text-sm font-medium text-foreground">
                          {worktreeRefLabel(worktree)}
                        </div>
                        <div className="mt-1 break-all text-sm text-muted-foreground">
                          {worktree.path}
                        </div>
                      </div>
                      <Badge className={worktreeTone(worktree)}>
                        {worktreeStatusLabel(worktree)}
                      </Badge>
                    </div>

                    <div className="mt-3 grid gap-2 text-sm text-muted-foreground">
                      <div>
                        Working tree{' '}
                        <span className="font-medium text-foreground">
                          {worktree.workingTreeState}
                        </span>
                      </div>
                      <div>
                        Upstream{' '}
                        <span className="font-medium text-foreground">
                          {worktree.hasUpstream ? 'Configured' : 'Missing'}
                        </span>
                      </div>
                      <div>
                        Push state{' '}
                        <span className="font-medium text-foreground">
                          {worktree.hasUnpushedCommits ? 'Ahead of upstream' : 'Fully pushed'}
                        </span>
                      </div>
                    </div>

                    {worktree.main ? (
                      <div className="mt-3 rounded-[16px] border border-dashed border-border/70 bg-background/60 px-3 py-2 text-sm text-muted-foreground">
                        The main repository worktree stays attached to the project and
                        cannot be removed from this view.
                      </div>
                    ) : worktree.deletionAllowed ? (
                      <div className="mt-3 flex flex-wrap items-center justify-between gap-3">
                        <div className="text-sm text-muted-foreground">
                          This linked worktree is clean and fully pushed.
                        </div>
                        <Button
                          variant="outline"
                          onClick={() => handleDeleteWorktree(worktree.path)}
                          disabled={deleteProjectWorktree.status === 'submitting'}
                        >
                          Delete worktree
                        </Button>
                      </div>
                    ) : (
                      <div className="mt-3 flex flex-wrap items-center justify-between gap-3">
                        <div className="text-sm text-muted-foreground">
                          {worktree.deletionRestriction ??
                            'This linked worktree must be handled manually before deletion.'}
                        </div>
                        <Button variant="outline" disabled>
                          Delete worktree
                        </Button>
                      </div>
                    )}

                    <div className="mt-4 space-y-3 rounded-[16px] border border-border/60 bg-background/65 p-3">
                      <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                        Merge support
                      </div>
                      <div className="text-sm text-muted-foreground">
                        Merge this linked worktree branch into another existing local branch.
                      </div>

                      {worktree.mergeAllowed ? (
                        <form
                          className="space-y-4"
                          onSubmit={(event) =>
                            handleMergeWorktree(
                              event,
                              worktree.path,
                              worktree.mergeTargetBranches.includes(
                                mergeTargets[worktree.path] ?? '',
                              )
                                ? (mergeTargets[worktree.path] ?? '')
                                : (worktree.mergeTargetBranches[0] ?? ''),
                            )}
                        >
                          <div className="space-y-2">
                            <Label htmlFor={`merge-project-worktree-${project.id}-${worktree.path}`}>
                              Target branch
                            </Label>
                            <select
                              id={`merge-project-worktree-${project.id}-${worktree.path}`}
                              className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none ring-0 transition-colors focus-visible:border-ring"
                              value={
                                worktree.mergeTargetBranches.includes(
                                  mergeTargets[worktree.path] ?? '',
                                )
                                  ? (mergeTargets[worktree.path] ?? '')
                                  : (worktree.mergeTargetBranches[0] ?? '')
                              }
                              onChange={(event) =>
                                setMergeTargets((current) => ({
                                  ...current,
                                  [worktree.path]: event.target.value,
                                }))
                              }
                            >
                              {worktree.mergeTargetBranches.map((branch) => (
                                <option key={`${worktree.path}:${branch}`} value={branch}>
                                  {branch}
                                </option>
                              ))}
                            </select>
                          </div>

                          {activeMergeWorktreePath === worktree.path ? (
                            <MutationStatus
                              status={mergeProjectWorktree.status}
                              error={mergeProjectWorktree.error}
                              submittingMessage="Merging linked worktree through the backend project service..."
                              successMessage="Worktree branch merged and the project list has been refreshed."
                            />
                          ) : null}

                          <Button
                            type="submit"
                            variant="outline"
                            disabled={mergeProjectWorktree.status === 'submitting'}
                          >
                            Merge worktree
                          </Button>
                        </form>
                      ) : (
                        <div className="rounded-[16px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                          {worktree.mergeRestriction ??
                            'Merge is unavailable for this worktree.'}
                        </div>
                      )}
                    </div>

                    <div className="mt-4 space-y-3 rounded-[16px] border border-border/60 bg-background/65 p-3">
                      <div className="flex flex-wrap items-start justify-between gap-3">
                        <div>
                          <div className="text-xs font-medium uppercase tracking-[0.2em] text-muted-foreground">
                            Sandbox environment
                          </div>
                          <div className="mt-2 text-sm text-muted-foreground">
                            Manage the persistent podman image and the temporary container for this linked worktree.
                          </div>
                        </div>
                        <Badge className={sandboxTone(worktree)}>
                          {sandboxStatusLabel(worktree)}
                        </Badge>
                      </div>

                      <div className="grid gap-2 text-sm text-muted-foreground">
                        <div>
                          Image state{' '}
                          <span className="font-medium text-foreground">
                            {worktree.sandbox.imageStatus}
                          </span>
                        </div>
                        <div>
                          Image ref{' '}
                          <span className="font-mono text-xs text-foreground">
                            {worktree.sandbox.imageReference}
                          </span>
                        </div>
                        <div>
                          Image init{' '}
                          <span className="font-medium text-foreground">
                            {worktree.sandbox.imageInitScriptPath}
                          </span>{' '}
                          <span className="text-xs">
                            · {scriptSourceLabel(worktree.sandbox.imageInitScriptSource)}
                          </span>
                        </div>
                        <div>
                          Container state{' '}
                          <span className="font-medium text-foreground">
                            {worktree.sandbox.containerStatus}
                          </span>
                        </div>
                        <div>
                          Container name{' '}
                          <span className="font-mono text-xs text-foreground">
                            {worktree.sandbox.containerName}
                          </span>
                        </div>
                        <div>
                          Project init{' '}
                          <span className="font-medium text-foreground">
                            {worktree.sandbox.projectInitScriptPath}
                          </span>{' '}
                          <span className="text-xs">
                            · {scriptSourceLabel(worktree.sandbox.projectInitScriptSource)}
                          </span>
                        </div>
                      </div>

                      {worktree.sandbox.imageFailureMessage ? (
                        <div className="rounded-[16px] border border-destructive/25 bg-destructive/8 px-3 py-2 text-sm text-destructive">
                          {worktree.sandbox.imageFailureMessage}
                        </div>
                      ) : null}

                      {worktree.sandbox.containerFailureMessage ? (
                        <div className="rounded-[16px] border border-destructive/25 bg-destructive/8 px-3 py-2 text-sm text-destructive">
                          {worktree.sandbox.containerFailureMessage}
                        </div>
                      ) : null}

                      {!worktree.sandbox.supported ? (
                        <div className="rounded-[16px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                          {worktree.sandbox.restriction ??
                            'Sandbox is unavailable for this worktree.'}
                        </div>
                      ) : (
                        <div className="space-y-3">
                          <div className="flex flex-wrap gap-3">
                            <Button
                              variant="outline"
                              onClick={() => handleCreateSandboxImage(worktree.path)}
                              disabled={
                                !worktree.sandbox.imageActionAllowed ||
                                createProjectSandboxImage.status === 'submitting'
                              }
                            >
                              {worktree.sandbox.imageStatus === 'READY'
                                ? 'Rebuild image'
                                : 'Create image'}
                            </Button>
                            <Button
                              variant="outline"
                              onClick={() => handleCreateSandboxContainer(worktree.path)}
                              disabled={
                                !worktree.sandbox.containerCreateAllowed ||
                                createProjectSandboxContainer.status === 'submitting'
                              }
                            >
                              Create container
                            </Button>
                            <Button
                              variant="outline"
                              onClick={() => handleDeleteSandboxContainer(worktree.path)}
                              disabled={
                                !worktree.sandbox.containerDeleteAllowed ||
                                deleteProjectSandboxContainer.status === 'submitting'
                              }
                            >
                              Destroy container
                            </Button>
                          </div>

                          {worktree.sandbox.imageActionAllowed ? null : (
                            <div className="rounded-[16px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                              {worktree.sandbox.imageActionRestriction ??
                                'Image creation is unavailable for this worktree.'}
                            </div>
                          )}

                          {worktree.sandbox.containerCreateAllowed ? null : (
                            <div className="rounded-[16px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                              {worktree.sandbox.containerCreateRestriction ??
                                'Container creation is unavailable for this worktree.'}
                            </div>
                          )}

                          {worktree.sandbox.containerDeleteAllowed ? null : (
                            <div className="rounded-[16px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                              {worktree.sandbox.containerDeleteRestriction ??
                                'Container destruction is unavailable for this worktree.'}
                            </div>
                          )}

                          {activeSandboxImageWorktreePath === worktree.path ? (
                            <MutationStatus
                              status={createProjectSandboxImage.status}
                              error={createProjectSandboxImage.error}
                              submittingMessage="Creating sandbox image through the backend project service..."
                              successMessage="Sandbox image created and the project list has been refreshed."
                            />
                          ) : null}

                          {activeSandboxContainerWorktreePath === worktree.path ? (
                            <MutationStatus
                              status={createProjectSandboxContainer.status}
                              error={createProjectSandboxContainer.error}
                              submittingMessage="Creating sandbox container through the backend project service..."
                              successMessage="Sandbox container created and the project list has been refreshed."
                            />
                          ) : null}

                          {activeSandboxDeleteWorktreePath === worktree.path ? (
                            <MutationStatus
                              status={deleteProjectSandboxContainer.status}
                              error={deleteProjectSandboxContainer.error}
                              submittingMessage="Destroying sandbox container through the backend project service..."
                              successMessage="Sandbox container destroyed and the project list has been refreshed."
                            />
                          ) : null}
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              <div className="flex flex-wrap gap-3">
                <Button
                  variant="outline"
                  onClick={handleRepairWorktrees}
                  disabled={
                    repairProjectWorktrees.status === 'submitting' ||
                    project.repository.headState === 'DETACHED'
                  }
                >
                  Repair metadata
                </Button>
                <Button
                  variant="outline"
                  onClick={handlePruneWorktrees}
                  disabled={pruneProjectWorktrees.status === 'submitting'}
                >
                  Prune stale records
                </Button>
              </div>

              {project.repository.headState === 'DETACHED' ? (
                <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                  Repair is unavailable while the repository is in detached HEAD
                  state, but prune remains available for stale records.
                </div>
              ) : null}

              <MutationStatus
                status={repairProjectWorktrees.status}
                error={repairProjectWorktrees.error}
                submittingMessage="Repairing project worktree metadata through the backend project service..."
                successMessage="Worktree metadata repaired and the project list has been refreshed."
              />

              <MutationStatus
                status={pruneProjectWorktrees.status}
                error={pruneProjectWorktrees.error}
                submittingMessage="Pruning stale worktree records through the backend project service..."
                successMessage="Stale worktree records pruned and the project list has been refreshed."
              />

              {createRestriction ? (
                <div className="rounded-[18px] border border-dashed border-border/70 bg-muted/24 px-3 py-2 text-sm text-muted-foreground">
                  {createRestriction}
                </div>
              ) : (
                <form className="space-y-4" onSubmit={handleCreateWorktree}>
                  <div className="space-y-2">
                    <Label htmlFor={`create-project-worktree-${project.id}`}>
                      Worktree branch
                    </Label>
                    <select
                      id={`create-project-worktree-${project.id}`}
                      className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none ring-0 transition-colors focus-visible:border-ring"
                      value={worktreeBranchValue}
                      onChange={(event) => setWorktreeBranch(event.target.value)}
                    >
                      {creatableWorktreeBranches.map((branch) => (
                        <option key={branch} value={branch}>
                          {branch}
                        </option>
                      ))}
                    </select>
                  </div>

                  <MutationStatus
                    status={createProjectWorktree.status}
                    error={createProjectWorktree.error}
                    submittingMessage="Creating project worktree through the backend project service..."
                    successMessage="Worktree created and the project list has been refreshed."
                  />

                  <MutationStatus
                    status={deleteProjectWorktree.status}
                    error={deleteProjectWorktree.error}
                    submittingMessage="Deleting project worktree through the backend project service..."
                    successMessage="Worktree removed and the project list has been refreshed."
                  />

                  <Button
                    type="submit"
                    disabled={createProjectWorktree.status === 'submitting'}
                  >
                    Create worktree
                  </Button>
                </form>
              )}
            </div>
          </div>
        ) : (
          <div className="rounded-[22px] border border-dashed border-border/70 bg-background/55 p-4 text-sm leading-6 text-muted-foreground">
            No repository bound. Connect an absolute local Git repository path to
            link this project to a real engineering workspace.
          </div>
        )}

        <form className="space-y-4" onSubmit={handleBindRepository}>
          <div className="space-y-2">
            <Label htmlFor={`bind-project-repository-${project.id}`}>
              Repository path
            </Label>
            <Input
              id={`bind-project-repository-${project.id}`}
              value={repositoryPathValue}
              onChange={(event) => setRepositoryPath(event.target.value)}
              placeholder="/absolute/path/to/repository"
              required
            />
          </div>

          <MutationStatus
            status={bindProjectRepository.status}
            error={bindProjectRepository.error}
            submittingMessage="Binding project repository through the backend project service..."
            successMessage="Repository bound and the project list has been refreshed."
          />

          <Button
            type="submit"
            disabled={bindProjectRepository.status === 'submitting'}
          >
            Bind repository
          </Button>
        </form>

        <ProjectDerivedAppAssemblyCard
          project={project}
          onAssemblyChanged={() =>
            setDerivedAppLifecycleRefreshVersion((current) => current + 1)
          }
        />
        <ProjectDerivedAppVerificationCard
          project={project}
          refreshNonce={derivedAppLifecycleRefreshVersion}
        />
        <ProjectDerivedAppUpgradeSupportCard
          project={project}
          refreshNonce={derivedAppLifecycleRefreshVersion}
        />
      </CardContent>
    </Card>
  )
}

const createProjectDescriptor: FormDescriptor = {
  entityName: 'Project',
  fields: [
    { key: 'projectKey', label: 'Project key', widget: 'text', required: true },
    { key: 'projectName', label: 'Project name', widget: 'text', required: true },
    { key: 'description', label: 'Description', widget: 'textarea', required: true },
  ],
}

export function ProjectsPage() {
  const projects = useProjectsResource()
  const createProject = useCreateProjectAction()
  const [formKey, setFormKey] = useState(0)

  async function handleCreateProject(values: Record<string, string | number | boolean>) {
    await createProject.submit({
      projectKey: String(values.projectKey),
      projectName: String(values.projectName),
      description: String(values.description),
    })
    setFormKey((k) => k + 1)
    projects.reload()
  }

  return (
    <div className="space-y-8">
      <PageHeader
        eyebrow="Delivery"
        title="Software project management"
        description="Projects are displayed from the backend project service and provide the scope anchor for kanban and ticket pages."
        actions={
          <Button variant="outline" onClick={projects.reload}>
            <RefreshCcw className="mr-2 size-4" />
            Refresh
          </Button>
        }
      />

      <div className="grid gap-4 xl:grid-cols-[0.82fr_1.18fr]">
        <Card className="bg-card/96">
          <CardHeader>
            <CardTitle className="text-lg">Create project</CardTitle>
          </CardHeader>
          <CardContent>
            <DynamicForm
              key={formKey}
              descriptor={createProjectDescriptor}
              onSubmit={handleCreateProject}
              mutationStatus={createProject.status}
              mutationError={createProject.error}
              submittingMessage="Creating project through the backend service..."
              successMessage="Project created and the project list has been refreshed."
              submitLabel="Create project"
            />
          </CardContent>
        </Card>

        <ResourceState
          status={projects.status}
          error={projects.error}
          empty={projects.data.length === 0}
          emptyTitle="No projects returned"
          emptyMessage="Add a software project through the backend core or enable demo data to seed the first project."
          onRetry={projects.reload}
        >
          <div className="grid gap-4 xl:grid-cols-2">
            {projects.data.map((project) => (
              <ProjectRepositoryCard
                key={project.id}
                project={project}
                onRepositoryBound={projects.reload}
              />
            ))}
          </div>
        </ResourceState>
      </div>
    </div>
  )
}
