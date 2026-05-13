import { Navigate, useLocation } from "react-router-dom";
import { useAuthStore } from "@/stores/auth";
import type { Role } from "@/types/api";

interface Props {
  children: React.ReactNode;
  roles?: Role[];
}

export function ProtectedRoute({ children, roles }: Props) {
  const { token, user } = useAuthStore();
  const location = useLocation();

  if (!token || !user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  if (roles && !roles.includes(user.role)) {
    const fallback = user.role === "CUSTOMER" ? "/portal/dashboard" : "/dashboard";
    return <Navigate to={fallback} replace />;
  }
  return <>{children}</>;
}
