import { api } from "@/lib/api";
import type { Bill, BillStatus, Page, Payment, PaymentMethod } from "@/types/api";

export interface PaymentInput {
  amount: number;
  paymentMethod: PaymentMethod;
  paymentDate: string;
  reference?: string | null;
  notes?: string | null;
}

export async function listBills(params: {
  customerId?: string;
  status?: BillStatus;
  year?: number;
  month?: number;
  page?: number;
  size?: number;
}) {
  const { data } = await api.get<Page<Bill>>("/api/bills", { params });
  return data;
}

export async function generateBills(month: number, year: number) {
  const { data } = await api.post<Bill[]>("/api/bills/generate", { month, year });
  return data;
}

export async function recordPayment(billId: string, input: PaymentInput) {
  const { data } = await api.post<Payment>(`/api/bills/${billId}/payments`, input);
  return data;
}

export async function listPayments(billId: string) {
  const { data } = await api.get<Payment[]>(`/api/bills/${billId}/payments`);
  return data;
}
