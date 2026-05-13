import { api } from "@/lib/api";
import type { Customer, CustomerStatus, Page } from "@/types/api";

export interface CustomerInput {
  name: string;
  phone: string;
  address?: string | null;
  customMilkRate?: number | null;
  status?: CustomerStatus;
}

export interface CreateCustomerLoginInput {
  username: string;
  password: string;
}

export async function listCustomers(params: {
  q?: string;
  status?: CustomerStatus;
  page?: number;
  size?: number;
}) {
  const { data } = await api.get<Page<Customer>>("/api/customers", { params });
  return data;
}

export async function createCustomer(input: CustomerInput) {
  const { data } = await api.post<Customer>("/api/customers", input);
  return data;
}

export async function updateCustomer(id: string, input: CustomerInput) {
  const { data } = await api.put<Customer>(`/api/customers/${id}`, input);
  return data;
}

export async function deleteCustomer(id: string) {
  await api.delete(`/api/customers/${id}`);
}

export async function createCustomerLogin(id: string, input: CreateCustomerLoginInput) {
  const { data } = await api.post<Customer>(`/api/customers/${id}/login`, input);
  return data;
}
