import { api } from "@/lib/api";
import type { Expense, ExpenseCategory, Page } from "@/types/api";

export interface ExpenseInput {
  category: ExpenseCategory;
  amount: number;
  notes?: string | null;
  expenseDate: string;
  cowId?: string | null;
}

export async function listExpenses(params: {
  category?: ExpenseCategory;
  from?: string;
  to?: string;
  page?: number;
  size?: number;
}) {
  const { data } = await api.get<Page<Expense>>("/api/expenses", { params });
  return data;
}

export async function createExpense(input: ExpenseInput) {
  const { data } = await api.post<Expense>("/api/expenses", input);
  return data;
}

export async function deleteExpense(id: string) {
  await api.delete(`/api/expenses/${id}`);
}
