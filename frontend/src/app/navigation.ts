import {
  FolderKanban,
  KanbanSquare,
  LayoutDashboard,
  ShieldCheck,
  Ticket,
  Users,
} from 'lucide-react'
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
      title: 'Enterprise Dashboard',
      description:
        'Inspect the first end-to-end baseline across users, project delivery, permissions, tickets, and boards.',
    },
  },
  {
    to: '/users',
    label: 'Users',
    eyebrow: 'Identity',
    icon: Users,
    meta: {
      title: 'User Management',
      description:
        'Review the current application users exposed by the backend user service.',
    },
  },
  {
    to: '/roles',
    label: 'Roles & Permissions',
    eyebrow: 'Identity',
    icon: ShieldCheck,
    meta: {
      title: 'Role Permission Management',
      description:
        'Inspect role-based permission assignments using the backend access control service.',
    },
  },
  {
    to: '/projects',
    label: 'Projects',
    eyebrow: 'Delivery',
    icon: FolderKanban,
    meta: {
      title: 'Software Project Management',
      description:
        'Track the software projects currently available from the backend project service.',
    },
  },
  {
    to: '/tickets',
    label: 'Tickets',
    eyebrow: 'Delivery',
    icon: Ticket,
    meta: {
      title: 'Ticket Management',
      description:
        'Browse project tickets and their current delivery states through the backend ticket service.',
    },
  },
  {
    to: '/kanban',
    label: 'Kanban',
    eyebrow: 'Delivery',
    icon: KanbanSquare,
    meta: {
      title: 'Kanban Management',
      description:
        'Inspect project boards and minimal state flow using the current backend kanban model.',
    },
  },
]

export function findNavigationItem(pathname: string) {
  return adminNavigation.find((item) => pathname === item.to)
}
