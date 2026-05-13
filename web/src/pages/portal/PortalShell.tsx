import { LogOut, Sprout } from "lucide-react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/auth";
import { cn } from "@/lib/utils";

const nav = [
  { to: "/portal/dashboard", label: "Overview" },
  { to: "/portal/milk", label: "Milk history" },
  { to: "/portal/bills", label: "Bills" },
];

export function PortalShell() {
  const navigate = useNavigate();
  const { user, clear } = useAuthStore();

  function handleLogout() {
    clear();
    navigate("/login", { replace: true });
  }

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b bg-card">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-2">
            <Sprout className="h-6 w-6 text-secondary" />
            <span className="text-lg font-semibold">DairySmart · Customer Portal</span>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-sm text-muted-foreground">Hi, {user?.fullName ?? user?.username}</span>
            <Button variant="ghost" size="sm" onClick={handleLogout} className="gap-2">
              <LogOut className="h-4 w-4" /> Sign out
            </Button>
          </div>
        </div>
        <nav className="container flex gap-4 pb-2">
          {nav.map(({ to, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                cn(
                  "rounded-md px-3 py-1.5 text-sm font-medium transition-colors",
                  isActive ? "bg-primary text-primary-foreground" : "text-muted-foreground hover:bg-accent hover:text-foreground",
                )
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="container py-8">
        <Outlet />
      </main>
    </div>
  );
}
