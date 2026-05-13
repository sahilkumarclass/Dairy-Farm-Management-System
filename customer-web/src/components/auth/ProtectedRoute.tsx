import { Navigate, useLocation } from "react-router-dom";
import { useAuthStore } from "@/stores/auth";

interface Props {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: Props) {
  const { token, user } = useAuthStore();
  const location = useLocation();

  if (!token || !user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  if (user.role !== "CUSTOMER") {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
}
