import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { WorkflowPanel, type WorkflowDescriptor, type WorkflowInstance } from '@/components/admin/workflow-panel'

const descriptor: WorkflowDescriptor = {
  entityName: 'Ticket workflow',
  stateLabel: 'Current state',
  assigneeLabel: 'Current assignee',
  commentLabel: 'Comment',
  commentPlaceholder: 'Explain the workflow action',
  historyTitle: 'Workflow history',
  actions: [
    { action: 'submit', label: 'Submit' },
    { action: 'approve', label: 'Approve' },
    { action: 'reject', label: 'Reject' },
    { action: 'reassign', label: 'Reassign', requiresAssignee: true },
  ],
}

const instance: WorkflowInstance = {
  ticketId: 1000,
  ticketKey: 'FSP-1',
  title: 'Bootstrap backend core',
  state: 'IN_PROGRESS',
  assignee: {
    userId: 100,
    displayName: 'Administrator',
    username: 'admin',
  },
  availableActions: ['approve', 'reject', 'reassign'],
  history: [
    {
      id: 1,
      action: 'SUBMIT',
      fromState: 'TODO',
      toState: 'IN_PROGRESS',
      actorDisplayName: 'Administrator',
      previousAssigneeDisplayName: 'Administrator',
      nextAssigneeDisplayName: 'Administrator',
      comment: 'Submitting for review',
    },
  ],
}

describe('WorkflowPanel', () => {
  it('renders descriptor-driven workflow state, assignee, actions, comment, and history', () => {
    render(
      <WorkflowPanel
        descriptor={descriptor}
        instance={instance}
        availableAssignees={[
          { userId: 100, displayName: 'Administrator', username: 'admin' },
          { userId: 101, displayName: 'Reviewer', username: 'reviewer' },
        ]}
        onAction={vi.fn()}
      />,
    )

    expect(screen.getByText('FSP-1 workflow')).toBeInTheDocument()
    expect(screen.getByText('IN_PROGRESS')).toBeInTheDocument()
    expect(screen.getByText('Administrator')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Approve' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Reject' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Reassign' })).toBeInTheDocument()
    expect(screen.getByLabelText('Comment')).toBeInTheDocument()
    expect(screen.getByText('Workflow history')).toBeInTheDocument()
    expect(screen.getByText('Submitting for review')).toBeInTheDocument()
  })

  it('hands selected action, required comment, and caller-owned reassignment payload to onAction', async () => {
    const onAction = vi.fn()
    render(
      <WorkflowPanel
        descriptor={descriptor}
        instance={instance}
        availableAssignees={[
          { userId: 100, displayName: 'Administrator', username: 'admin' },
          { userId: 101, displayName: 'Reviewer', username: 'reviewer' },
        ]}
        onAction={onAction}
      />,
    )

    fireEvent.click(screen.getByRole('button', { name: 'Reassign' }))
    fireEvent.change(screen.getByLabelText('Reassign to'), {
      target: { value: '101' },
    })
    fireEvent.change(screen.getByLabelText('Comment'), {
      target: { value: 'Need a reviewer sign-off' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Run selected action' }))

    await waitFor(() => {
      expect(onAction).toHaveBeenCalledWith({
        action: 'reassign',
        comment: 'Need a reviewer sign-off',
        assigneeUserId: 101,
      })
    })
  })

  it('blocks action submission when required comment input is empty', async () => {
    const onAction = vi.fn()
    render(
      <WorkflowPanel descriptor={descriptor} instance={instance} onAction={onAction} />,
    )

    fireEvent.click(screen.getByRole('button', { name: 'Approve' }))
    fireEvent.click(screen.getByRole('button', { name: 'Run selected action' }))

    expect(await screen.findByText('Comment is required')).toBeInTheDocument()
    expect(onAction).not.toHaveBeenCalled()
  })

  it('renders from caller-provided workflow data and does not fetch by itself', () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch')

    render(<WorkflowPanel descriptor={descriptor} instance={instance} onAction={vi.fn()} />)

    expect(screen.getByText('FSP-1 workflow')).toBeInTheDocument()
    expect(fetchSpy).not.toHaveBeenCalled()
    fetchSpy.mockRestore()
  })

  it('displays workflow backend errors via existing mutation feedback convention', () => {
    render(
      <WorkflowPanel
        descriptor={descriptor}
        instance={instance}
        onAction={vi.fn()}
        mutationStatus="error"
        mutationError="Backend request failed with 409: Workflow comment is required"
      />,
    )

    expect(
      screen.getByText('Backend request failed with 409: Workflow comment is required'),
    ).toBeInTheDocument()
  })
})
