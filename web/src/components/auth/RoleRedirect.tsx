import { Navigate } from "react-router-dom";
import { useAuthStore } from "@/stores/auth";

export function RoleRedirect() {
  const { token, user } = useAuthStore();
  if (!token || !user) return <Navigate to="/login" replace />;
  if (user.role === "CUSTOMER") return <Navigate to="/portal/dashboard" replace />;
  return <Navigate to="/dashboard" replace />;
}
