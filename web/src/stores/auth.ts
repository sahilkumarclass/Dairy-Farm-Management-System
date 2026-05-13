import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { Role } from "@/types/api";

interface AuthUser {
  userId: string;
  username: string;
  fullName: string;
  role: Role;
}

interface AuthState {
  token: string | null;
  user: AuthUser | null;
  setSession: (token: string, user: AuthUser) => void;
  clear: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      setSession: (token, user) => set({ token, user }),
      clear: () => set({ token: null, user: null }),
    }),
    { name: "dfms-auth" },
  ),
);
