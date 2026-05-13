import { api } from "@/lib/api";
import type {
  Cow,
  CowDetail,
  CowHealthLog,
  CowMilkProductionEntry,
  CowSummary,
  Gender,
  HealthEventType,
  HealthStatus,
  MilkSession,
  Page,
} from "@/types/api";

export interface CowInput {
  tagNo: string;
  name?: string | null;
  breed?: string | null;
  gender: Gender;
  ageMonths?: number | null;
  healthStatus: HealthStatus;
  dailyYieldEstimateLiters?: number | null;
  dateAcquired?: string | null;
  notes?: string | null;
}

export async function listCows(params: { q?: string; status?: HealthStatus; page?: number; size?: number }) {
  const { data } = await api.get<Page<Cow>>("/api/cows", { params });
  return data;
}

export async function getCowDetail(id: string) {
  const { data } = await api.get<CowDetail>(`/api/cows/${id}/detail`);
  return data;
}

export async function getHerdSummary() {
  const { data } = await api.get<CowSummary>("/api/cows/summary");
  return data;
}

export async function createCow(input: CowInput) {
  const { data } = await api.post<Cow>("/api/cows", input);
  return data;
}

export async function updateCow(id: string, input: CowInput) {
  const { data } = await api.put<Cow>(`/api/cows/${id}`, input);
  return data;
}

export async function deleteCow(id: string) {
  await api.delete(`/api/cows/${id}`);
}

// ----- Milk production -----
export interface CowMilkProductionInput {
  cowId: string;
  productionDate: string;
  session: MilkSession;
  liters: number;
  notes?: string | null;
}

export async function listCowProduction(cowId: string, params: { from?: string; to?: string; size?: number } = {}) {
  const { data } = await api.get<Page<CowMilkProductionEntry>>("/api/cow-production", {
    params: { cowId, ...params },
  });
  return data;
}

export async function recordCowProduction(input: CowMilkProductionInput) {
  const { data } = await api.post<CowMilkProductionEntry>("/api/cow-production", input);
  return data;
}

// ----- Health log -----
export interface CowHealthLogInput {
  cowId: string;
  eventType: HealthEventType;
  eventDate: string;
  nextDueDate?: string | null;
  vetName?: string | null;
  cost?: number | null;
  notes?: string | null;
}

export async function listCowHealth(cowId: string, params: { size?: number } = {}) {
  const { data } = await api.get<Page<CowHealthLog>>("/api/cow-health", {
    params: { cowId, ...params },
  });
  return data;
}

export async function recordCowHealth(input: CowHealthLogInput) {
  const { data } = await api.post<CowHealthLog>("/api/cow-health", input);
  return data;
}

export async function listUpcomingHealth(daysAhead = 14) {
  const { data } = await api.get<CowHealthLog[]>("/api/cow-health/upcoming", { params: { daysAhead } });
  return data;
}
