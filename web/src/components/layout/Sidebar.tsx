import { NavLink } from "react-router-dom";
import { LayoutDashboard, Users, Milk, Receipt, FileText, Sprout } from "lucide-react";
import { cn } from "@/lib/utils";

const nav = [
  { to: "/dashboard", icon: LayoutDashboard, label: "Dashboard" },
  { to: "/customers", icon: Users, label: "Customers" },
  { to: "/milk-entries", icon: Milk, label: "Milk Entries" },
  { to: "/expenses", icon: Receipt, label: "Expenses" },
  { to: "/bills", icon: FileText, label: "Bills" },
];

export function Sidebar() {
  return (
    <aside className="hidden w-64 flex-col border-r bg-card lg:flex">
      <div className="flex h-16 items-center gap-2 border-b px-6">
        <Sprout className="h-6 w-6 text-secondary" />
        <span className="text-lg font-semibold">DairySmart Pro</span>
      </div>
      <nav className="flex-1 space-y-1 p-4">
        {nav.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
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
            {label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
