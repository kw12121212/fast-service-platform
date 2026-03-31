import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

// --- Types ---

export type ReportChartType = 'bar' | 'line' | 'pie'

export type SummaryCardItem = {
  label: string
  value: string
  detail?: string
}

export type ReportTableColumn = {
  key: string
  label: string
}

export type ReportTableRow = Record<string, unknown>

export type ReportChartDataPoint = {
  label: string
  value: number
}

export type SummaryCardsDescriptor = {
  type: 'summary-cards'
  sectionKey: string
  title?: string
}

export type ReportTableDescriptor = {
  type: 'table'
  sectionKey: string
  title?: string
  columns: ReportTableColumn[]
}

export type ReportChartDescriptor = {
  type: 'chart'
  sectionKey: string
  chartType: ReportChartType
  title?: string
}

export type ReportSectionDescriptor =
  | SummaryCardsDescriptor
  | ReportTableDescriptor
  | ReportChartDescriptor

export type ReportDescriptor = {
  sections: ReportSectionDescriptor[]
}

export type ReportSectionData = SummaryCardItem[] | ReportTableRow[] | ReportChartDataPoint[]

export type ReportResults = Record<string, ReportSectionData>

// --- Component ---

type DynamicReportProps = {
  descriptor: ReportDescriptor
  results: ReportResults
}

export function DynamicReport({ descriptor, results }: DynamicReportProps) {
  return (
    <div className="space-y-6">
      {descriptor.sections.map((section) => (
        <ReportSection
          key={section.sectionKey}
          section={section}
          data={results[section.sectionKey] ?? []}
        />
      ))}
    </div>
  )
}

type ReportSectionProps = {
  section: ReportSectionDescriptor
  data: ReportSectionData
}

function ReportSection({ section, data }: ReportSectionProps) {
  return (
    <Card className="bg-card/95">
      {section.title && (
        <CardHeader>
          <CardTitle className="text-base">{section.title}</CardTitle>
        </CardHeader>
      )}
      <CardContent className={section.title ? '' : 'pt-6'}>
        {section.type === 'summary-cards' && (
          <SummaryCardsSection cards={data as SummaryCardItem[]} />
        )}
        {section.type === 'table' && (
          <TableSection columns={section.columns} rows={data as ReportTableRow[]} />
        )}
        {section.type === 'chart' && (
          <ChartSection chartType={section.chartType} data={data as ReportChartDataPoint[]} />
        )}
      </CardContent>
    </Card>
  )
}

// --- Summary Cards ---

function SummaryCardsSection({ cards }: { cards: SummaryCardItem[] }) {
  if (cards.length === 0) {
    return <p className="text-sm text-muted-foreground">No data available.</p>
  }
  return (
    <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
      {cards.map((card) => (
        <div
          key={card.label}
          className="rounded-[18px] border border-border/60 bg-background/70 p-4"
        >
          <div className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
            {card.label}
          </div>
          <div className="mt-2 text-2xl font-semibold tracking-tight">{card.value}</div>
          {card.detail && <p className="mt-1.5 text-xs text-muted-foreground">{card.detail}</p>}
        </div>
      ))}
    </div>
  )
}

// --- Table ---

function TableSection({ columns, rows }: { columns: ReportTableColumn[]; rows: ReportTableRow[] }) {
  if (rows.length === 0) {
    return <p className="text-sm text-muted-foreground">No data available.</p>
  }
  return (
    <Table>
      <TableHeader>
        <TableRow>
          {columns.map((col) => (
            <TableHead key={col.key}>{col.label}</TableHead>
          ))}
        </TableRow>
      </TableHeader>
      <TableBody>
        {rows.map((row, i) => (
          <TableRow key={i}>
            {columns.map((col) => (
              <TableCell key={col.key}>{String(row[col.key] ?? '')}</TableCell>
            ))}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}

// --- Charts ---

const CHART_COLORS = [
  'var(--chart-1)',
  'var(--chart-2)',
  'var(--chart-3)',
  'var(--chart-4)',
  'var(--chart-5)',
]

function ChartSection({
  chartType,
  data,
}: {
  chartType: ReportChartType
  data: ReportChartDataPoint[]
}) {
  if (data.length === 0) {
    return <p className="text-sm text-muted-foreground">No data available.</p>
  }
  if (chartType === 'bar') return <BarChart data={data} />
  if (chartType === 'line') return <LineChart data={data} />
  return <PieChart data={data} />
}

function BarChart({ data }: { data: ReportChartDataPoint[] }) {
  const maxValue = Math.max(...data.map((d) => d.value))
  const barW = 36
  const barGap = 14
  const chartH = 100
  const labelH = 20
  const totalW = data.length * (barW + barGap) - barGap

  return (
    <svg
      viewBox={`0 0 ${totalW} ${chartH + labelH}`}
      className="w-full"
      aria-label="Bar chart"
      role="img"
    >
      {data.map((point, i) => {
        const barH = maxValue === 0 ? 0 : (point.value / maxValue) * chartH
        const x = i * (barW + barGap)
        return (
          <g key={point.label}>
            <rect
              x={x}
              y={chartH - barH}
              width={barW}
              height={barH}
              fill={CHART_COLORS[i % CHART_COLORS.length]}
              rx="4"
            />
            <text
              x={x + barW / 2}
              y={chartH + labelH - 2}
              textAnchor="middle"
              fontSize="9"
              fill="currentColor"
              opacity="0.6"
            >
              {point.label}
            </text>
          </g>
        )
      })}
    </svg>
  )
}

function LineChart({ data }: { data: ReportChartDataPoint[] }) {
  const maxValue = Math.max(...data.map((d) => d.value))
  const w = 300
  const h = 100
  const padL = 8
  const padR = 8
  const padT = 8
  const padB = 20
  const innerW = w - padL - padR
  const innerH = h - padT - padB

  const pts = data.map((d, i) => ({
    x: padL + (data.length === 1 ? innerW / 2 : (i / (data.length - 1)) * innerW),
    y: padT + (maxValue === 0 ? innerH : (1 - d.value / maxValue) * innerH),
    label: d.label,
  }))

  const pathD = pts.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ')

  return (
    <svg viewBox={`0 0 ${w} ${h}`} className="w-full" aria-label="Line chart" role="img">
      {pts.length > 1 && (
        <path d={pathD} fill="none" stroke={CHART_COLORS[0]} strokeWidth="2" />
      )}
      {pts.map((p, i) => (
        <g key={data[i].label}>
          <circle cx={p.x} cy={p.y} r="3.5" fill={CHART_COLORS[0]} />
          <text
            x={p.x}
            y={h - 4}
            textAnchor="middle"
            fontSize="9"
            fill="currentColor"
            opacity="0.6"
          >
            {p.label}
          </text>
        </g>
      ))}
    </svg>
  )
}

function PieChart({ data }: { data: ReportChartDataPoint[] }) {
  const total = data.reduce((sum, d) => sum + d.value, 0)
  const cx = 55
  const cy = 55
  const r = 48

  // Pre-compute start angles to avoid mutation inside map
  const sweeps = data.map((d) =>
    total === 0 ? (2 * Math.PI) / data.length : (d.value / total) * 2 * Math.PI,
  )
  const startAngles = sweeps.reduce<number[]>(
    (acc, _sweep, i) => [...acc, i === 0 ? -Math.PI / 2 : acc[i - 1] + sweeps[i - 1]],
    [],
  )

  const slices = data.map((d, i) => {
    const start = startAngles[i]
    const end = start + sweeps[i]
    const x1 = cx + r * Math.cos(start)
    const y1 = cy + r * Math.sin(start)
    const x2 = cx + r * Math.cos(end)
    const y2 = cy + r * Math.sin(end)
    return {
      path: `M ${cx} ${cy} L ${x1} ${y1} A ${r} ${r} 0 ${sweeps[i] > Math.PI ? 1 : 0} 1 ${x2} ${y2} Z`,
      color: CHART_COLORS[i % CHART_COLORS.length],
      label: d.label,
      value: d.value,
    }
  })

  return (
    <div className="flex flex-wrap items-center gap-6">
      <svg
        viewBox="0 0 110 110"
        className="h-28 w-28 shrink-0"
        aria-label="Pie chart"
        role="img"
      >
        {slices.map((s) => (
          <path key={s.label} d={s.path} fill={s.color} />
        ))}
      </svg>
      <div className="space-y-1.5">
        {slices.map((s) => (
          <div key={s.label} className="flex items-center gap-2 text-sm">
            <span
              className="inline-block h-2.5 w-2.5 shrink-0 rounded-full"
              style={{ backgroundColor: s.color }}
            />
            <span className="text-muted-foreground">{s.label}</span>
            <span className="font-medium">{s.value}</span>
          </div>
        ))}
      </div>
    </div>
  )
}
