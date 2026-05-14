import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Users,
  Milk,
  Receipt,
  FileText,
  Sprout,
  Beef,
  UserCog,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import { useAuthStore } from "@/stores/auth";
import { cn } from "@/lib/utils";
import type { Role } from "@/types/api";

interface NavItem {
  to: string;
  icon: React.ComponentType<{ className?: string }>;
  i18nKey: string;
  roles: Role[];
}

const nav: NavItem[] = [
  { to: "/dashboard", icon: LayoutDashboard, i18nKey: "nav.dashboard", roles: ["OWNER", "STAFF"] },
  { to: "/customers", icon: Users, i18nKey: "nav.customers", roles: ["OWNER", "STAFF"] },
  { to: "/milk-entries", icon: Milk, i18nKey: "nav.milkEntries", roles: ["OWNER", "STAFF"] },
  { to: "/herd", icon: Beef, i18nKey: "nav.herd", roles: ["OWNER"] },
  { to: "/expenses", icon: Receipt, i18nKey: "nav.expenses", roles: ["OWNER"] },
  { to: "/bills", icon: FileText, i18nKey: "nav.bills", roles: ["OWNER"] },
  { to: "/staff", icon: UserCog, i18nKey: "nav.staff", roles: ["OWNER"] },
];

interface SidebarProps {
  onNavigate?: () => void;
  className?: string;
}

export function Sidebar({ onNavigate, className }: SidebarProps = {}) {
  const { t } = useTranslation();
  const role = useAuthStore((s) => s.user?.role);
  const items = nav.filter((n) => !role || n.roles.includes(role));

  return (
    <aside
      className={cn(
        "flex h-full w-64 flex-col border-r bg-card",
        className,
      )}
    >
      <div className="flex h-16 items-center gap-2 border-b px-6">
        <Sprout className="h-6 w-6 text-secondary" />
        <span className="text-lg font-semibold">DairySmart Pro</span>
      </div>
      <nav className="flex-1 space-y-1 p-4">
        {items.map(({ to, icon: Icon, i18nKey }) => (
          <NavLink
            key={to}
            to={to}
            onClick={() => onNavigate?.()}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium transition-colors",
                isActive
                  ? "bg-primary text-primary-foreground"
                  : "text-muted-foreground hover:bg-accent hover:text-foreground",
              )
            }
          >
            <Icon className="h-5 w-5" />
            {t(i18nKey)}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
