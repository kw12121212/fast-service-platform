import { NavLink, Outlet, useLocation } from 'react-router-dom'

import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'

import { adminNavigation, findNavigationItem } from './navigation'

function AdminShell() {
  const location = useLocation()
  const currentItem = findNavigationItem(location.pathname) ?? adminNavigation[0]

  return (
    <div className="min-h-screen bg-transparent">
      <div className="mx-auto flex min-h-screen max-w-[1680px] flex-col gap-4 p-4 lg:flex-row lg:p-6">
        <aside className="w-full shrink-0 rounded-[30px] border border-sidebar-border bg-sidebar text-sidebar-foreground shadow-[0_24px_80px_rgba(28,39,52,0.24)] lg:w-[292px]">
          <div className="flex h-full flex-col p-5">
            <div className="rounded-[24px] border border-white/10 bg-white/6 p-5">
              <div className="flex items-center justify-between gap-3">
                <Badge className="rounded-full bg-sidebar-primary px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.22em] text-sidebar-primary-foreground">
                  AI-first
                </Badge>
                <Badge
                  variant="outline"
                  className="rounded-full border-white/12 bg-white/8 px-3 py-1 text-[11px] text-sidebar-foreground"
                >
                  V1 shell
                </Badge>
              </div>
              <div className="mt-6 space-y-3">
                <div className="text-xs font-medium uppercase tracking-[0.3em] text-sidebar-foreground/62">
                  Fast Service Platform
                </div>
                <div className="text-2xl font-semibold tracking-tight">
                  Enterprise Admin Console
                </div>
                <p className="text-sm leading-6 text-sidebar-foreground/74">
                  A direct frontend for the current Lealone-backed enterprise core.
                </p>
              </div>
            </div>

            <nav className="mt-6 flex-1 space-y-2">
              {adminNavigation.map((item) => {
                const Icon = item.icon

                return (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    className={({ isActive }) =>
                      cn(
                        'group flex items-center gap-3 rounded-[22px] border px-4 py-3 transition-colors',
                        isActive
                          ? 'border-white/12 bg-white/12 text-sidebar-foreground'
                          : 'border-transparent bg-transparent text-sidebar-foreground/72 hover:border-white/10 hover:bg-white/8 hover:text-sidebar-foreground',
                      )
                    }
                  >
                    {({ isActive }) => (
                      <>
                        <div
                          className={cn(
                            'flex size-11 items-center justify-center rounded-2xl border',
                            isActive
                              ? 'border-sidebar-primary bg-sidebar-primary text-sidebar-primary-foreground'
                              : 'border-white/8 bg-white/6 text-sidebar-foreground/78 group-hover:border-white/12 group-hover:bg-white/12',
                          )}
                        >
                          <Icon className="size-5" />
                        </div>
                        <div className="min-w-0 flex-1">
                          <div className="text-[11px] font-medium uppercase tracking-[0.18em] text-inherit/58">
                            {item.eyebrow}
                          </div>
                          <div className="truncate text-sm font-medium">{item.label}</div>
                        </div>
                      </>
                    )}
                  </NavLink>
                )
              })}
            </nav>

            <div className="mt-6 rounded-[24px] border border-white/10 bg-white/6 p-4 text-sm text-sidebar-foreground/76">
              <div className="text-xs font-medium uppercase tracking-[0.24em] text-sidebar-foreground/58">
                Current page
              </div>
              <div className="mt-3 text-base font-semibold text-sidebar-foreground">
                {currentItem.label}
              </div>
              <p className="mt-2 leading-6">{currentItem.meta.description}</p>
            </div>
          </div>
        </aside>

        <main className="flex min-h-[calc(100vh-2rem)] flex-1 flex-col rounded-[34px] border border-white/55 bg-white/78 shadow-[0_28px_90px_rgba(32,46,60,0.1)] backdrop-blur-xl">
          <div className="border-b border-border/65 px-6 py-5 lg:px-8">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
              <div>
                <div className="text-xs font-medium uppercase tracking-[0.26em] text-muted-foreground">
                  {currentItem.eyebrow}
                </div>
                <h1 className="mt-2 text-3xl font-semibold tracking-tight text-foreground">
                  {currentItem.meta.title}
                </h1>
                <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
                  {currentItem.meta.description}
                </p>
              </div>

              <div className="flex flex-wrap items-center gap-2">
                <Badge className="rounded-full bg-primary/12 px-3 py-1 text-xs font-medium text-primary">
                  Direct backend path
                </Badge>
                <Badge
                  variant="outline"
                  className="rounded-full bg-background/75 px-3 py-1 text-xs font-medium text-foreground"
                >
                  /service
                </Badge>
              </div>
            </div>
          </div>

          <div className="flex-1 px-6 py-6 lg:px-8 lg:py-8">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}

export default AdminShell
