import { api } from "@/lib/api";
import type { Bill, CustomerSelfDashboard, MilkEntry, Page, Payment } from "@/types/api";

export async function getMyDashboard() {
  const { data } = await api.get<CustomerSelfDashboard>("/api/me/dashboard");
  return data;
}

export async function getMyMilkEntries(params: { page?: number; size?: number } = {}) {
  const { data } = await api.get<Page<MilkEntry>>("/api/me/milk-entries", { params });
  return data;
}

export async function getMyBills() {
  const { data } = await api.get<Bill[]>("/api/me/bills");
  return data;
}

export async function getMyBillPayments(billId: string) {
  const { data } = await api.get<Payment[]>(`/api/me/bills/${billId}/payments`);
  return data;
}
