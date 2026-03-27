import { Label } from '@/components/ui/label'
import type { SoftwareProject } from '@/lib/api/types'

type ProjectScopeSelectProps = {
  projects: SoftwareProject[]
  projectId: number | null
  onProjectIdChange: (projectId: number) => void
  label?: string
}

export function ProjectScopeSelect({
  projects,
  projectId,
  onProjectIdChange,
  label = 'Project scope',
}: ProjectScopeSelectProps) {
  return (
    <div className="flex min-w-[240px] flex-col gap-2">
      <Label htmlFor="project-scope">{label}</Label>
      <select
        id="project-scope"
        className="h-10 rounded-lg border border-input bg-background px-3 text-sm outline-none ring-0 transition-colors focus-visible:border-ring"
        value={projectId ?? ''}
        onChange={(event) => onProjectIdChange(Number(event.target.value))}
      >
        {projects.map((project) => (
          <option key={project.id} value={project.id}>
            {project.key} · {project.name}
          </option>
        ))}
      </select>
    </div>
  )
}
