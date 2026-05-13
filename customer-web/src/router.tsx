import { createBrowserRouter, Navigate } from "react-router-dom";
import { PortalShell } from "@/components/layout/PortalShell";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { LoginPage } from "@/pages/Login";
import { DashboardPage } from "@/pages/Dashboard";
import { MilkPage } from "@/pages/Milk";
import { BillsPage } from "@/pages/Bills";

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <PortalShell />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <DashboardPage /> },
      { path: "milk", element: <MilkPage /> },
      { path: "bills", element: <BillsPage /> },
    ],
  },
  { path: "*", element: <Navigate to="/dashboard" replace /> },
]);
