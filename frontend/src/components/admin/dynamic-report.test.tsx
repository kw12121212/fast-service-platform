import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { DynamicReport } from '@/components/admin/dynamic-report'
import type {
  ReportDescriptor,
  ReportResults,
} from '@/components/admin/dynamic-report'

const summaryCardsDescriptor: ReportDescriptor = {
  sections: [
    {
      type: 'summary-cards',
      sectionKey: 'overview',
      title: 'Overview',
    },
  ],
}

const tableDescriptor: ReportDescriptor = {
  sections: [
    {
      type: 'table',
      sectionKey: 'projects',
      title: 'Projects',
      columns: [
        { key: 'name', label: 'Name' },
        { key: 'key', label: 'Key' },
      ],
    },
  ],
}

const allSectionsDescriptor: ReportDescriptor = {
  sections: [
    { type: 'summary-cards', sectionKey: 'cards' },
    { type: 'table', sectionKey: 'rows', columns: [{ key: 'id', label: 'ID' }] },
    { type: 'chart', sectionKey: 'bar-data', chartType: 'bar', title: 'Bar chart' },
    { type: 'chart', sectionKey: 'line-data', chartType: 'line', title: 'Line chart' },
    { type: 'chart', sectionKey: 'pie-data', chartType: 'pie', title: 'Pie chart' },
  ],
}

const allSectionsResults: ReportResults = {
  cards: [
    { label: 'Users', value: '5', detail: 'Total users' },
    { label: 'Projects', value: '3' },
  ],
  rows: [{ id: '1' }, { id: '2' }],
  'bar-data': [
    { label: 'Jan', value: 10 },
    { label: 'Feb', value: 20 },
  ],
  'line-data': [
    { label: 'Jan', value: 5 },
    { label: 'Feb', value: 15 },
    { label: 'Mar', value: 10 },
  ],
  'pie-data': [
    { label: 'TODO', value: 4 },
    { label: 'Done', value: 6 },
  ],
}

describe('DynamicReport', () => {
  it('renders summary cards from descriptor', () => {
    const results: ReportResults = {
      overview: [
        { label: 'Users', value: '42', detail: 'Active accounts' },
        { label: 'Projects', value: '7' },
      ],
    }
    render(<DynamicReport descriptor={summaryCardsDescriptor} results={results} />)

    expect(screen.getByText('Overview')).toBeInTheDocument()
    expect(screen.getByText('Users')).toBeInTheDocument()
    expect(screen.getByText('42')).toBeInTheDocument()
    expect(screen.getByText('Active accounts')).toBeInTheDocument()
    expect(screen.getByText('Projects')).toBeInTheDocument()
    expect(screen.getByText('7')).toBeInTheDocument()
  })

  it('renders table with columns and rows from descriptor', () => {
    const results: ReportResults = {
      projects: [
        { name: 'Platform', key: 'PLAT' },
        { name: 'Demo App', key: 'DEMO' },
      ],
    }
    render(<DynamicReport descriptor={tableDescriptor} results={results} />)

    expect(screen.getByText('Projects')).toBeInTheDocument()
    expect(screen.getByRole('columnheader', { name: 'Name' })).toBeInTheDocument()
    expect(screen.getByRole('columnheader', { name: 'Key' })).toBeInTheDocument()
    expect(screen.getByRole('cell', { name: 'Platform' })).toBeInTheDocument()
    expect(screen.getByRole('cell', { name: 'PLAT' })).toBeInTheDocument()
    expect(screen.getByRole('cell', { name: 'Demo App' })).toBeInTheDocument()
  })

  it('renders bar, line, and pie chart sections from descriptor', () => {
    render(<DynamicReport descriptor={allSectionsDescriptor} results={allSectionsResults} />)

    expect(screen.getByRole('img', { name: 'Bar chart' })).toBeInTheDocument()
    expect(screen.getByRole('img', { name: 'Line chart' })).toBeInTheDocument()
    expect(screen.getByRole('img', { name: 'Pie chart' })).toBeInTheDocument()
  })

  it('renders all 5 V1 section types in a single report', () => {
    render(<DynamicReport descriptor={allSectionsDescriptor} results={allSectionsResults} />)

    expect(screen.getByText('Users')).toBeInTheDocument()
    expect(screen.getByRole('table')).toBeInTheDocument()
    expect(screen.getByRole('img', { name: 'Bar chart' })).toBeInTheDocument()
    expect(screen.getByRole('img', { name: 'Line chart' })).toBeInTheDocument()
    expect(screen.getByRole('img', { name: 'Pie chart' })).toBeInTheDocument()
  })

  it('uses caller-provided aggregated results and does not fetch data itself', () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch')

    const results: ReportResults = {
      cards: [{ label: 'Total', value: '99' }],
    }
    render(
      <DynamicReport
        descriptor={{ sections: [{ type: 'summary-cards', sectionKey: 'cards' }] }}
        results={results}
      />,
    )

    expect(screen.getByText('99')).toBeInTheDocument()
    expect(fetchSpy).not.toHaveBeenCalled()
    fetchSpy.mockRestore()
  })

  it('updates rendered output when descriptor or results change', () => {
    const descriptorV1: ReportDescriptor = {
      sections: [{ type: 'summary-cards', sectionKey: 'stats' }],
    }
    const resultsV1: ReportResults = {
      stats: [{ label: 'Count', value: '10' }],
    }
    const { rerender } = render(
      <DynamicReport descriptor={descriptorV1} results={resultsV1} />,
    )
    expect(screen.getByText('10')).toBeInTheDocument()

    const resultsV2: ReportResults = {
      stats: [{ label: 'Count', value: '25' }],
    }
    rerender(<DynamicReport descriptor={descriptorV1} results={resultsV2} />)
    expect(screen.getByText('25')).toBeInTheDocument()
    expect(screen.queryByText('10')).not.toBeInTheDocument()
  })

  it('renders empty-data message when results for a section are empty', () => {
    const results: ReportResults = {
      projects: [],
    }
    render(<DynamicReport descriptor={tableDescriptor} results={results} />)

    expect(screen.getByText('No data available.')).toBeInTheDocument()
  })
})
