import {
LayoutDashboard,
  Users,
  ShieldCheck,
  FolderKanban,
  Ticket,
  KanbanSquare} from 'lucide-react'
import type { LucideIcon } from 'lucide-react'

export type AdminRouteMeta = {
  title: string
  description: string
}

export type AdminNavigationItem = {
  to: string
  label: string
  eyebrow: string
  icon: LucideIcon
  meta: AdminRouteMeta
}

export const adminNavigation: AdminNavigationItem[] = [
  {
    to: '/dashboard',
    label: 'Dashboard',
    eyebrow: 'Operations',
    icon: LayoutDashboard,
    meta: {
      title: 'Assembly Dashboard',
      description: 'Inspect the selected module baseline for this derived admin application.',
    },
  },
  {
    to: '/users',
    label: 'Users',
    eyebrow: 'Identity',
    icon: Users,
    meta: {
      title: 'User Management',
      description: 'Manage application users through the backend user service.',
    },
  },
  {
    to: '/roles',
    label: 'Roles & Permissions',
    eyebrow: 'Identity',
    icon: ShieldCheck,
    meta: {
      title: 'Role Permission Management',
      description: 'Manage roles, permissions, and user-role assignments.',
    },
  },
  {
    to: '/projects',
    label: 'Projects',
    eyebrow: 'Delivery',
    icon: FolderKanban,
    meta: {
      title: 'Project Management',
      description: 'Manage software projects and project-scope workflows.',
    },
  },
  {
    to: '/tickets',
    label: 'Tickets',
    eyebrow: 'Delivery',
    icon: Ticket,
    meta: {
      title: 'Ticket Management',
      description: 'Manage tickets and minimal state progression.',
    },
  },
  {
    to: '/kanban',
    label: 'Kanban',
    eyebrow: 'Delivery',
    icon: KanbanSquare,
    meta: {
      title: 'Kanban Management',
      description: 'Manage boards scoped to the selected project baseline.',
    },
  },
]

export function findNavigationItem(pathname: string) {
  return adminNavigation.find((item) => pathname === item.to)
}
