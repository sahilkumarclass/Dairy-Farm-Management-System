import { LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuthStore } from "@/stores/auth";

export function TopBar() {
  const navigate = useNavigate();
  const { user, clear } = useAuthStore();

  function handleLogout() {
    clear();
    navigate("/login", { replace: true });
  }

  return (
    <header className="flex h-16 items-center justify-between border-b bg-card px-6">
      <div>
        <div className="text-sm text-muted-foreground">Welcome back</div>
        <div className="font-semibold">{user?.fullName ?? user?.username}</div>
      </div>
      <Button variant="ghost" size="sm" onClick={handleLogout} className="gap-2">
        <LogOut className="h-4 w-4" />
        Sign out
      </Button>
    </header>
  );
}
