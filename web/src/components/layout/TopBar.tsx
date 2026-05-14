import { LogOut, Menu } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/auth";
import { LanguageSwitcher } from "./LanguageSwitcher";

interface TopBarProps {
  onMobileMenuClick?: () => void;
}

export function TopBar({ onMobileMenuClick }: TopBarProps = {}) {
  const navigate = useNavigate();
  const { user, clear } = useAuthStore();
  const { t } = useTranslation();

  function handleLogout() {
    clear();
    navigate("/login", { replace: true });
  }

  return (
    <header className="flex h-16 items-center justify-between border-b bg-card px-4 sm:px-6">
      <div className="flex items-center gap-3">
        <Button
          variant="ghost"
          size="icon"
          className="lg:hidden"
          onClick={onMobileMenuClick}
          aria-label="Open menu"
        >
          <Menu className="h-5 w-5" />
        </Button>
        <div>
          <div className="text-xs text-muted-foreground sm:text-sm">{t("topbar.welcome")}</div>
          <div className="text-sm font-semibold sm:text-base">{user?.fullName ?? user?.username}</div>
        </div>
      </div>
      <div className="flex items-center gap-1 sm:gap-2">
        <LanguageSwitcher />
        <Button variant="ghost" size="sm" onClick={handleLogout} className="gap-2">
          <LogOut className="h-4 w-4" />
          <span className="hidden sm:inline">{t("topbar.signOut")}</span>
        </Button>
      </div>
    </header>
  );
}
