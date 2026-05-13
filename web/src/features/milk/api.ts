import { api } from "@/lib/api";
import type { MilkEntry, MilkSession, MilkType, Page } from "@/types/api";

export interface MilkEntryInput {
  customerId: string;
  milkType: MilkType;
  quantityLiters: number;
  ratePerLiter?: number | null;
  session: MilkSession;
  entryDate: string;
  notes?: string | null;
}

export async function listMilkEntries(params: {
  customerId?: string;
  from?: string;
  to?: string;
  page?: number;
  size?: number;
}) {
  const { data } = await api.get<Page<MilkEntry>>("/api/milk-entries", { params });
  return data;
}

export async function createMilkEntry(input: MilkEntryInput) {
  const { data } = await api.post<MilkEntry>("/api/milk-entries", input);
  return data;
}

export async function updateMilkEntry(id: string, input: MilkEntryInput) {
  const { data } = await api.put<MilkEntry>(`/api/milk-entries/${id}`, input);
  return data;
}

export async function deleteMilkEntry(id: string) {
  await api.delete(`/api/milk-entries/${id}`);
}
