import { LogOut, Sprout } from "lucide-react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { LanguageSwitcher } from "@/components/layout/LanguageSwitcher";
import { useAuthStore } from "@/stores/auth";
import { cn } from "@/lib/utils";

const nav = [
  { to: "/dashboard", labelKey: "nav.overview" },
  { to: "/milk", labelKey: "nav.milkHistory" },
  { to: "/bills", labelKey: "nav.bills" },
];

export function PortalShell() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { user, clear } = useAuthStore();

  function handleLogout() {
    clear();
    navigate("/login", { replace: true });
  }

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b bg-card">
        <div className="container flex h-16 items-center justify-between gap-2 px-4 sm:px-6">
          <div className="flex min-w-0 items-center gap-2">
            <Sprout className="h-6 w-6 shrink-0 text-secondary" />
            <span className="truncate text-lg font-semibold">{t("common.appName")}</span>
          </div>
          <div className="flex items-center gap-2 sm:gap-3">
            <span className="hidden max-w-[10rem] truncate text-sm text-muted-foreground sm:block">
              {t("shell.greeting", { name: user?.fullName ?? user?.username })}
            </span>
            <LanguageSwitcher />
            <Button variant="ghost" size="sm" onClick={handleLogout} className="gap-2">
              <LogOut className="h-4 w-4" />
              <span className="hidden sm:inline">{t("shell.signOut")}</span>
            </Button>
          </div>
        </div>
        <nav className="container flex flex-wrap gap-2 px-4 pb-2 sm:gap-4 sm:px-6">
          {nav.map(({ to, labelKey }) => (
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
              {t(labelKey)}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="container px-4 py-6 sm:px-6 sm:py-8">
        <Outlet />
      </main>
    </div>
  );
}
