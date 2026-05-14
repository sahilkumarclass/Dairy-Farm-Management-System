import { api } from "@/lib/api";
import type { ExpenseCategory, StaffUser } from "@/types/api";

export interface StaffCreateInput {
  username: string;
  password: string;
  fullName: string;
  phone?: string | null;
}

export interface StaffUpdateInput {
  fullName: string;
  phone?: string | null;
}

export interface PayStaffInput {
  amount: number;
  expenseDate: string;
  notes?: string | null;
}

export async function listStaff() {
  const { data } = await api.get<StaffUser[]>("/api/users/staff");
  return data;
}

export async function createStaff(input: StaffCreateInput) {
  const { data } = await api.post<StaffUser>("/api/users/staff", input);
  return data;
}

export async function updateStaff(id: string, input: StaffUpdateInput) {
  const { data } = await api.put<StaffUser>(`/api/users/${id}`, input);
  return data;
}

export async function enableStaff(id: string) {
  await api.post(`/api/users/${id}/enable`);
}

export async function disableStaff(id: string) {
  await api.post(`/api/users/${id}/disable`);
}

export async function deleteStaff(id: string) {
  await api.delete(`/api/users/${id}`);
}

export async function payStaff(staff: StaffUser, input: PayStaffInput) {
  const notes = [`Salary: ${staff.fullName}`, input.notes?.trim()].filter(Boolean).join(" — ");
  const payload = {
    category: "LABOR" as ExpenseCategory,
    amount: input.amount,
    expenseDate: input.expenseDate,
    notes,
    cowId: null,
  };
  await api.post("/api/expenses", payload);
}
