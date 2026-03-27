import type { ReactNode } from 'react'
import { AlertCircle, RefreshCcw } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'

type ResourceStateProps = {
  status: 'idle' | 'loading' | 'refreshing' | 'success' | 'error'
  error: string | null
  empty: boolean
  emptyTitle: string
  emptyMessage: string
  onRetry?: () => void
  children: ReactNode
  skeletonCount?: number
}

export function ResourceState({
  status,
  error,
  empty,
  emptyTitle,
  emptyMessage,
  onRetry,
  children,
  skeletonCount = 3,
}: ResourceStateProps) {
  if (status === 'loading') {
    return (
      <div className="space-y-3">
        {Array.from({ length: skeletonCount }).map((_, index) => (
          <Skeleton key={index} className="h-16 rounded-2xl" />
        ))}
      </div>
    )
  }

  if (status === 'error') {
    return (
      <Card className="border-destructive/20 bg-destructive/5">
        <CardHeader className="pb-2">
          <CardTitle className="flex items-center gap-2 text-base text-destructive">
            <AlertCircle className="size-4" />
            Backend data request failed
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4 text-sm text-muted-foreground">
          <p>{error ?? 'Unknown request error'}</p>
          {onRetry ? (
            <Button variant="outline" onClick={onRetry}>
              <RefreshCcw className="mr-2 size-4" />
              Retry
            </Button>
          ) : null}
        </CardContent>
      </Card>
    )
  }

  if (status === 'idle' || empty) {
    return (
      <Card className="border-dashed">
        <CardHeader className="pb-2">
          <CardTitle className="text-base">{emptyTitle}</CardTitle>
        </CardHeader>
        <CardContent className="text-sm leading-6 text-muted-foreground">
          {emptyMessage}
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="space-y-4">
      {status === 'refreshing' ? (
        <div className="flex items-center gap-2 text-xs font-medium uppercase tracking-[0.16em] text-muted-foreground">
          <RefreshCcw className="size-3.5 animate-spin" />
          Refreshing backend data
        </div>
      ) : null}
      {children}
    </div>
  )
}
