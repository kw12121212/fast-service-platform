import { type FormEvent, useState } from 'react'

import { MutationStatus } from '@/components/admin/mutation-status'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Label } from '@/components/ui/label'

export type WorkflowActionType = 'submit' | 'approve' | 'reject' | 'reassign'

export type WorkflowPerson = {
  userId: number
  displayName: string
  username?: string
}

export type WorkflowActionDescriptor = {
  action: WorkflowActionType
  label: string
  requiresAssignee?: boolean
}

export type WorkflowHistoryEntry = {
  id: number
  action: string
  fromState: string
  toState: string
  actorDisplayName: string
  previousAssigneeDisplayName?: string | null
  nextAssigneeDisplayName?: string | null
  comment: string
}

export type WorkflowInstance = {
  ticketId: number
  ticketKey: string
  title: string
  state: string
  assignee: WorkflowPerson
  availableActions: WorkflowActionType[]
  history: WorkflowHistoryEntry[]
}

export type WorkflowDescriptor = {
  entityName: string
  stateLabel: string
  assigneeLabel: string
  commentLabel: string
  commentPlaceholder: string
  historyTitle: string
  actions: WorkflowActionDescriptor[]
}

type WorkflowActionSubmission = {
  action: WorkflowActionType
  comment: string
  assigneeUserId?: number
}

type WorkflowPanelProps = {
  descriptor: WorkflowDescriptor
  instance: WorkflowInstance
  availableAssignees?: WorkflowPerson[]
  onAction: (submission: WorkflowActionSubmission) => void | Promise<void>
  mutationStatus?: 'idle' | 'submitting' | 'success' | 'error'
  mutationError?: string | null
  submittingMessage?: string
  successMessage?: string
}

export function WorkflowPanel({
  descriptor,
  instance,
  availableAssignees = [],
  onAction,
  mutationStatus = 'idle',
  mutationError = null,
  submittingMessage = 'Executing workflow action...',
  successMessage = 'Workflow action completed.',
}: WorkflowPanelProps) {
  const [comment, setComment] = useState('')
  const [selectedAction, setSelectedAction] = useState<WorkflowActionType | null>(null)
  const [selectedAssigneeUserId, setSelectedAssigneeUserId] = useState<string>(
    String(availableAssignees[0]?.userId ?? instance.assignee.userId),
  )
  const [commentError, setCommentError] = useState<string | null>(null)

  const visibleActions = descriptor.actions.filter((action) =>
    instance.availableActions.includes(action.action),
  )
  const isSubmitting = mutationStatus === 'submitting'
  const fallbackAssigneeUserId = String(availableAssignees[0]?.userId ?? instance.assignee.userId)
  const effectiveAssigneeUserId = availableAssignees.some(
    (assignee) => String(assignee.userId) === selectedAssigneeUserId,
  )
    ? selectedAssigneeUserId
    : fallbackAssigneeUserId

  async function handleActionSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!selectedAction) {
      return
    }

    if (comment.trim() === '') {
      setCommentError(`${descriptor.commentLabel} is required`)
      return
    }

    const actionDescriptor = visibleActions.find((entry) => entry.action === selectedAction)
    if (!actionDescriptor) {
      return
    }

    await onAction({
      action: selectedAction,
      comment,
      assigneeUserId: actionDescriptor.requiresAssignee ? Number(effectiveAssigneeUserId) : undefined,
    })
    setComment('')
    setCommentError(null)
    setSelectedAction(null)
  }

  return (
    <Card className="bg-card/95">
      <CardHeader>
        <CardTitle className="text-lg">{instance.ticketKey} workflow</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="grid gap-3 md:grid-cols-2">
          <div className="rounded-[22px] border border-border/60 bg-background/70 p-4">
            <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
              {descriptor.stateLabel}
            </div>
            <Badge className="mt-3 rounded-full bg-primary/12 text-primary">{instance.state}</Badge>
          </div>
          <div className="rounded-[22px] border border-border/60 bg-background/70 p-4">
            <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
              {descriptor.assigneeLabel}
            </div>
            <div className="mt-3 text-sm font-medium">{instance.assignee.displayName}</div>
            <div className="text-sm text-muted-foreground">{instance.assignee.username ?? ''}</div>
          </div>
        </div>

        <form className="space-y-4" onSubmit={handleActionSubmit}>
          <div className="space-y-2">
            <Label>{descriptor.entityName} actions</Label>
            <div className="flex flex-wrap gap-2">
              {visibleActions.map((action) => (
                <Button
                  key={action.action}
                  type="button"
                  variant={selectedAction === action.action ? 'default' : 'outline'}
                  disabled={isSubmitting}
                  onClick={() => setSelectedAction(action.action)}
                >
                  {action.label}
                </Button>
              ))}
            </div>
          </div>

          {visibleActions.some(
            (entry) => entry.action === selectedAction && entry.requiresAssignee,
          ) ? (
            <div className="space-y-2">
              <Label htmlFor="workflow-reassign-assignee">Reassign to</Label>
              <select
                id="workflow-reassign-assignee"
                className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm outline-none transition-colors focus-visible:border-ring"
                value={effectiveAssigneeUserId}
                onChange={(event) => setSelectedAssigneeUserId(event.target.value)}
                disabled={isSubmitting}
              >
                {availableAssignees.map((assignee) => (
                  <option key={assignee.userId} value={assignee.userId}>
                    {assignee.displayName}
                  </option>
                ))}
              </select>
            </div>
          ) : null}

          <div className="space-y-2">
            <Label htmlFor="workflow-comment">{descriptor.commentLabel}</Label>
            <textarea
              id="workflow-comment"
              value={comment}
              disabled={isSubmitting}
              aria-invalid={commentError ? true : undefined}
              onChange={(event) => {
                setComment(event.target.value)
                if (commentError) {
                  setCommentError(null)
                }
              }}
              placeholder={descriptor.commentPlaceholder}
              className="min-h-24 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none transition-colors focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 aria-invalid:border-destructive"
            />
            {commentError ? <p className="text-sm text-destructive">{commentError}</p> : null}
          </div>

          <MutationStatus
            status={mutationStatus}
            error={mutationError}
            submittingMessage={submittingMessage}
            successMessage={successMessage}
          />

          <Button type="submit" disabled={isSubmitting || selectedAction === null}>
            Run selected action
          </Button>
        </form>

        <div className="space-y-3">
          <div className="text-sm font-medium">{descriptor.historyTitle}</div>
          {instance.history.length === 0 ? (
            <div className="rounded-[18px] border border-dashed border-border/70 px-3 py-2 text-sm text-muted-foreground">
              No workflow history is available yet.
            </div>
          ) : (
            <div className="space-y-3">
              {instance.history.map((entry) => (
                <div
                  key={entry.id}
                  className="rounded-[20px] border border-border/60 bg-background/70 p-4"
                >
                  <div className="flex flex-wrap items-center gap-2 text-sm">
                    <Badge variant="outline" className="rounded-full">
                      {entry.action}
                    </Badge>
                    <span className="text-muted-foreground">
                      {entry.fromState} to {entry.toState}
                    </span>
                  </div>
                  <div className="mt-2 text-sm">Actor: {entry.actorDisplayName}</div>
                  {entry.previousAssigneeDisplayName || entry.nextAssigneeDisplayName ? (
                    <div className="mt-1 text-sm text-muted-foreground">
                      Assignee: {entry.previousAssigneeDisplayName ?? 'none'} to{' '}
                      {entry.nextAssigneeDisplayName ?? 'none'}
                    </div>
                  ) : null}
                  <p className="mt-2 text-sm text-muted-foreground">{entry.comment}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}
