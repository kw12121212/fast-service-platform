import { CheckCircle2, LoaderCircle, TriangleAlert } from 'lucide-react'

type MutationStatusProps = {
  status: 'idle' | 'submitting' | 'success' | 'error'
  error: string | null
  submittingMessage: string
  successMessage: string
}

export function MutationStatus({
  status,
  error,
  submittingMessage,
  successMessage,
}: MutationStatusProps) {
  if (status === 'idle') {
    return null
  }

  if (status === 'submitting') {
    return (
      <div className="flex items-center gap-2 rounded-[18px] border border-border/60 bg-muted/35 px-3 py-2 text-sm text-muted-foreground">
        <LoaderCircle className="size-4 animate-spin" />
        {submittingMessage}
      </div>
    )
  }

  if (status === 'error') {
    return (
      <div className="flex items-start gap-2 rounded-[18px] border border-destructive/25 bg-destructive/8 px-3 py-2 text-sm text-destructive">
        <TriangleAlert className="mt-0.5 size-4 shrink-0" />
        <span>{error ?? 'Request failed'}</span>
      </div>
    )
  }

  return (
    <div className="flex items-center gap-2 rounded-[18px] border border-emerald-500/20 bg-emerald-500/8 px-3 py-2 text-sm text-emerald-700">
      <CheckCircle2 className="size-4" />
      {successMessage}
    </div>
  )
}
