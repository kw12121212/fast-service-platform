import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import {
  DynamicForm,
  type FormDescriptor,
} from '@/components/admin/dynamic-form'

const allWidgetsDescriptor: FormDescriptor = {
  entityName: 'Test Entity',
  fields: [
    { key: 'name', label: 'Name', widget: 'text' },
    { key: 'count', label: 'Count', widget: 'number' },
    { key: 'birthday', label: 'Birthday', widget: 'date' },
    {
      key: 'status',
      label: 'Status',
      widget: 'select',
      options: [
        { label: 'Active', value: 'active' },
        { label: 'Inactive', value: 'inactive' },
      ],
    },
    { key: 'notes', label: 'Notes', widget: 'textarea' },
    { key: 'enabled', label: 'Enabled', widget: 'boolean' },
  ],
}

describe('DynamicForm', () => {
  it('renders all 6 V1 widget types from a descriptor', () => {
    render(<DynamicForm descriptor={allWidgetsDescriptor} onSubmit={vi.fn()} />)

    const textInput = screen.getByLabelText(/^name$/i)
    expect(textInput.tagName).toBe('INPUT')
    expect(textInput).toHaveAttribute('type', 'text')

    const numberInput = screen.getByLabelText(/^count$/i)
    expect(numberInput.tagName).toBe('INPUT')
    expect(numberInput).toHaveAttribute('type', 'number')

    const dateInput = screen.getByLabelText(/^birthday$/i)
    expect(dateInput.tagName).toBe('INPUT')
    expect(dateInput).toHaveAttribute('type', 'date')

    const selectInput = screen.getByLabelText(/^status$/i)
    expect(selectInput.tagName).toBe('SELECT')

    const textareaInput = screen.getByLabelText(/^notes$/i)
    expect(textareaInput.tagName).toBe('TEXTAREA')

    expect(screen.getByRole('switch', { name: /^enabled$/i })).toBeInTheDocument()
  })

  it('initializes fields to defaultValue in create mode', () => {
    const descriptor: FormDescriptor = {
      entityName: 'Test',
      fields: [
        { key: 'title', label: 'Title', widget: 'text', defaultValue: 'Draft' },
        { key: 'active', label: 'Active', widget: 'boolean', defaultValue: true },
      ],
    }
    render(<DynamicForm descriptor={descriptor} onSubmit={vi.fn()} />)

    expect(screen.getByLabelText(/^title$/i)).toHaveValue('Draft')
    expect(screen.getByRole('switch', { name: /^active$/i })).toHaveAttribute(
      'aria-checked',
      'true',
    )
  })

  it('initializes fields from initialValues in edit mode', () => {
    const descriptor: FormDescriptor = {
      entityName: 'Test',
      fields: [
        { key: 'title', label: 'Title', widget: 'text', defaultValue: 'Draft' },
        { key: 'active', label: 'Active', widget: 'boolean', defaultValue: false },
      ],
    }
    render(
      <DynamicForm
        descriptor={descriptor}
        initialValues={{ title: 'Edited Title', active: true }}
        onSubmit={vi.fn()}
      />,
    )

    expect(screen.getByLabelText(/^title$/i)).toHaveValue('Edited Title')
    expect(screen.getByRole('switch', { name: /^active$/i })).toHaveAttribute(
      'aria-checked',
      'true',
    )
  })

  it('blocks onSubmit and shows error when required field is empty', async () => {
    const onSubmit = vi.fn()
    render(
      <DynamicForm
        descriptor={{
          entityName: 'Test',
          fields: [{ key: 'title', label: 'Title', widget: 'text', required: true }],
        }}
        onSubmit={onSubmit}
      />,
    )

    fireEvent.click(screen.getByRole('button', { name: /submit/i }))

    expect(await screen.findByText('Title is required')).toBeInTheDocument()
    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('calls onSubmit with correct field values after valid submission', async () => {
    const onSubmit = vi.fn()
    render(
      <DynamicForm
        descriptor={{
          entityName: 'Test',
          fields: [
            { key: 'username', label: 'Username', widget: 'text' },
            { key: 'age', label: 'Age', widget: 'number' },
          ],
        }}
        onSubmit={onSubmit}
      />,
    )

    fireEvent.change(screen.getByLabelText(/^username$/i), {
      target: { value: 'alice' },
    })
    fireEvent.change(screen.getByLabelText(/^age$/i), { target: { value: '30' } })
    fireEvent.click(screen.getByRole('button', { name: /submit/i }))

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith({ username: 'alice', age: 30 })
    })
  })

  it('displays backend error via existing mutation feedback convention', () => {
    render(
      <DynamicForm
        descriptor={{
          entityName: 'Test',
          fields: [{ key: 'name', label: 'Name', widget: 'text' }],
        }}
        onSubmit={vi.fn()}
        mutationStatus="error"
        mutationError="Username already taken"
      />,
    )

    expect(screen.getByText('Username already taken')).toBeInTheDocument()
  })
})
