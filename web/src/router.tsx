import { createBrowserRouter, Navigate } from "react-router-dom";
import { AppShell } from "@/components/layout/AppShell";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { LoginPage } from "@/pages/Login";
import { DashboardPage } from "@/pages/Dashboard";
import { CustomersPage } from "@/pages/Customers";
import { MilkEntriesPage } from "@/pages/MilkEntries";
import { ExpensesPage } from "@/pages/Expenses";
import { BillsPage } from "@/pages/Bills";
import { HerdPage } from "@/pages/Herd";
import { CowDetailPage } from "@/pages/CowDetail";
import { StaffPage } from "@/pages/Staff";

const ownerOnly = (el: JSX.Element) => (
  <ProtectedRoute roles={["OWNER"]}>{el}</ProtectedRoute>
);

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  {
    path: "/",
    element: (
      <ProtectedRoute roles={["OWNER", "STAFF"]}>
        <AppShell />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <DashboardPage /> },
      { path: "customers", element: <CustomersPage /> },
      { path: "milk-entries", element: <MilkEntriesPage /> },
      { path: "expenses", element: ownerOnly(<ExpensesPage />) },
      { path: "bills", element: ownerOnly(<BillsPage />) },
      { path: "herd", element: ownerOnly(<HerdPage />) },
      { path: "herd/:id", element: ownerOnly(<CowDetailPage />) },
      { path: "staff", element: ownerOnly(<StaffPage />) },
    ],
  },
  { path: "*", element: <Navigate to="/dashboard" replace /> },
]);
