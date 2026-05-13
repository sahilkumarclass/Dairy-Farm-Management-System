import { api } from "@/lib/api";
import type { AuthResponse } from "@/types/api";

export async function login(username: string, password: string) {
  const { data } = await api.post<AuthResponse>("/api/auth/login", { username, password });
  return data;
}
