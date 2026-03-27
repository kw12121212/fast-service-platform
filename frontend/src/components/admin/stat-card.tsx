import type { ReactNode } from 'react'
import { ArrowUpRight } from 'lucide-react'

import { Badge } from '@/components/ui/badge'
import { Card, CardContent } from '@/components/ui/card'

type StatCardProps = {
  label: string
  value: string
  detail: string
  tone?: 'default' | 'accent'
  icon?: ReactNode
}

export function StatCard({
  label,
  value,
  detail,
  tone = 'default',
  icon,
}: StatCardProps) {
  return (
    <Card
      className={
        tone === 'accent'
          ? 'border-primary/15 bg-[linear-gradient(160deg,rgba(233,244,255,0.95),rgba(255,247,221,0.92))]'
          : 'border-border/70 bg-card/96'
      }
    >
      <CardContent className="space-y-5 p-5">
        <div className="flex items-start justify-between gap-4">
          <div>
            <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
              {label}
            </div>
            <div className="mt-3 text-3xl font-semibold tracking-tight text-foreground">
              {value}
            </div>
          </div>
          <Badge
            variant="outline"
            className="rounded-full border-border/70 bg-background/70 px-2.5 py-1 text-xs text-muted-foreground"
          >
            {icon ?? <ArrowUpRight className="size-3.5" />}
          </Badge>
        </div>
        <p className="text-sm leading-6 text-muted-foreground">{detail}</p>
      </CardContent>
    </Card>
  )
}
