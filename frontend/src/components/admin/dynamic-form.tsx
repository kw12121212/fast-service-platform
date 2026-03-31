import { type FormEvent, useState } from 'react'

import { MutationStatus } from '@/components/admin/mutation-status'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { cn } from '@/lib/utils'

export type FieldWidget = 'text' | 'number' | 'date' | 'select' | 'textarea' | 'boolean'

export type ValidationRules = {
  minLength?: number
  maxLength?: number
  pattern?: string
  min?: number
  max?: number
}

export type SelectOption = {
  label: string
  value: string
}

export type FieldDescriptor = {
  key: string
  label: string
  widget: FieldWidget
  required?: boolean
  defaultValue?: string | number | boolean
  options?: SelectOption[]
  validationRules?: ValidationRules
}

export type FormDescriptor = {
  entityName: string
  fields: FieldDescriptor[]
}

type FieldValue = string | boolean

type DynamicFormProps = {
  descriptor: FormDescriptor
  initialValues?: Record<string, string | number | boolean>
  onSubmit: (values: Record<string, string | number | boolean>) => void | Promise<void>
  mutationStatus?: 'idle' | 'submitting' | 'success' | 'error'
  mutationError?: string | null
  submittingMessage?: string
  successMessage?: string
  submitLabel?: string
}

function initializeValues(
  descriptor: FormDescriptor,
  initialValues?: Record<string, string | number | boolean>,
): Record<string, FieldValue> {
  const result: Record<string, FieldValue> = {}
  for (const field of descriptor.fields) {
    if (initialValues !== undefined && field.key in initialValues) {
      const v = initialValues[field.key]
      result[field.key] = field.widget === 'boolean' ? Boolean(v) : String(v)
    } else if (field.defaultValue !== undefined) {
      result[field.key] =
        field.widget === 'boolean' ? Boolean(field.defaultValue) : String(field.defaultValue)
    } else {
      result[field.key] = field.widget === 'boolean' ? false : ''
    }
  }
  return result
}

function validateField(field: FieldDescriptor, raw: FieldValue): string | null {
  if (field.widget === 'boolean') return null

  const strValue = String(raw)

  if (field.required && strValue.trim() === '') {
    return `${field.label} is required`
  }

  const rules = field.validationRules
  if (!rules) return null

  if (rules.minLength !== undefined && strValue.length < rules.minLength) {
    return `${field.label} must be at least ${rules.minLength} characters`
  }
  if (rules.maxLength !== undefined && strValue.length > rules.maxLength) {
    return `${field.label} must be at most ${rules.maxLength} characters`
  }
  if (rules.pattern !== undefined && strValue !== '' && !new RegExp(rules.pattern).test(strValue)) {
    return `${field.label} has an invalid format`
  }
  if (field.widget === 'number' && strValue !== '') {
    const num = Number(strValue)
    if (rules.min !== undefined && num < rules.min) {
      return `${field.label} must be at least ${rules.min}`
    }
    if (rules.max !== undefined && num > rules.max) {
      return `${field.label} must be at most ${rules.max}`
    }
  }

  return null
}

function collectSubmitValues(
  descriptor: FormDescriptor,
  values: Record<string, FieldValue>,
): Record<string, string | number | boolean> {
  const result: Record<string, string | number | boolean> = {}
  for (const field of descriptor.fields) {
    const raw = values[field.key]
    if (field.widget === 'number') {
      const str = String(raw)
      result[field.key] = str === '' ? '' : Number(str)
    } else {
      result[field.key] = raw
    }
  }
  return result
}

export function DynamicForm({
  descriptor,
  initialValues,
  onSubmit,
  mutationStatus = 'idle',
  mutationError = null,
  submittingMessage = 'Submitting...',
  successMessage = 'Saved successfully.',
  submitLabel = 'Submit',
}: DynamicFormProps) {
  const [values, setValues] = useState<Record<string, FieldValue>>(() =>
    initializeValues(descriptor, initialValues),
  )
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})

  function setValue(key: string, value: FieldValue) {
    setValues((prev) => ({ ...prev, [key]: value }))
    setFieldErrors((prev) => {
      if (!prev[key]) return prev
      const next = { ...prev }
      delete next[key]
      return next
    })
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const errors: Record<string, string> = {}
    for (const field of descriptor.fields) {
      const error = validateField(field, values[field.key])
      if (error) errors[field.key] = error
    }

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors)
      return
    }

    await onSubmit(collectSubmitValues(descriptor, values))
  }

  const isSubmitting = mutationStatus === 'submitting'

  return (
    <form className="space-y-4" onSubmit={handleSubmit} noValidate>
      {descriptor.fields.map((field) => (
        <FieldRow
          key={field.key}
          field={field}
          value={values[field.key]}
          error={fieldErrors[field.key]}
          disabled={isSubmitting}
          onChange={(value) => setValue(field.key, value)}
        />
      ))}

      <MutationStatus
        status={mutationStatus}
        error={mutationError}
        submittingMessage={submittingMessage}
        successMessage={successMessage}
      />

      <Button type="submit" disabled={isSubmitting}>
        {submitLabel}
      </Button>
    </form>
  )
}

type FieldRowProps = {
  field: FieldDescriptor
  value: FieldValue
  error?: string
  disabled: boolean
  onChange: (value: FieldValue) => void
}

function FieldRow({ field, value, error, disabled, onChange }: FieldRowProps) {
  const inputId = `dynamic-field-${field.key}`
  const hasError = Boolean(error)

  if (field.widget === 'boolean') {
    return (
      <div className="flex items-center gap-3">
        <button
          type="button"
          role="switch"
          aria-checked={value === true}
          aria-label={field.label}
          disabled={disabled}
          onClick={() => onChange(!value)}
          className={cn(
            'relative inline-flex h-5 w-9 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50',
            value ? 'bg-primary' : 'bg-input',
          )}
        >
          <span
            className={cn(
              'pointer-events-none inline-block h-4 w-4 rounded-full bg-background shadow-lg transition-transform',
              value ? 'translate-x-4' : 'translate-x-0',
            )}
          />
        </button>
        <span className="text-sm font-medium leading-none">{field.label}</span>
      </div>
    )
  }

  return (
    <div className="space-y-2">
      <Label htmlFor={inputId}>
        {field.label}
        {field.required && (
          <span className="ml-1 text-destructive" aria-hidden="true">
            *
          </span>
        )}
      </Label>

      {field.widget === 'textarea' ? (
        <textarea
          id={inputId}
          value={String(value)}
          disabled={disabled}
          aria-invalid={hasError ? true : undefined}
          onChange={(e) => onChange(e.target.value)}
          className="h-auto min-h-[80px] w-full resize-y rounded-lg border border-input bg-transparent px-2.5 py-2 text-base outline-none transition-colors placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50 aria-invalid:border-destructive md:text-sm dark:bg-input/30"
        />
      ) : field.widget === 'select' ? (
        <select
          id={inputId}
          value={String(value)}
          disabled={disabled}
          aria-invalid={hasError ? true : undefined}
          onChange={(e) => onChange(e.target.value)}
          className="h-8 w-full cursor-pointer rounded-lg border border-input bg-transparent px-2.5 py-1 text-base outline-none transition-colors focus-visible:border-ring focus-visible:ring-2 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50 aria-invalid:border-destructive md:text-sm dark:bg-input/30"
        >
          <option value="">Select...</option>
          {(field.options ?? []).map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      ) : (
        <Input
          id={inputId}
          type={field.widget}
          value={String(value)}
          disabled={disabled}
          aria-invalid={hasError ? true : undefined}
          onChange={(e) => onChange(e.target.value)}
        />
      )}

      {error && <p className="text-sm text-destructive">{error}</p>}
    </div>
  )
}
