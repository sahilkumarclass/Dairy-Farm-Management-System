export type Role = "OWNER" | "STAFF" | "CUSTOMER";

export interface AuthResponse {
  accessToken: string | null;
  tokenType: string | null;
  expiresInSeconds: number;
  userId: string;
  username: string;
  fullName: string;
  role: Role;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  violations?: { field: string; message: string }[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export type CustomerStatus = "ACTIVE" | "INACTIVE";

export interface Customer {
  id: string;
  name: string;
  phone: string;
  address: string | null;
  customMilkRate: string | null;
  status: CustomerStatus;
  createdAt: string;
}

export type MilkType = "COW" | "BUFFALO" | "MIXED";
export type MilkSession = "MORNING" | "EVENING";

export interface MilkEntry {
  id: string;
  customerId: string;
  customerName: string;
  milkType: MilkType;
  quantityLiters: string;
  ratePerLiter: string;
  totalAmount: string;
  session: MilkSession;
  entryDate: string;
  notes: string | null;
}

export type ExpenseCategory =
  | "FEED"
  | "VETERINARY"
  | "LABOR"
  | "UTILITIES"
  | "EQUIPMENT"
  | "TRANSPORT"
  | "OTHER";

export interface Expense {
  id: string;
  category: ExpenseCategory;
  amount: string;
  notes: string | null;
  expenseDate: string;
}

export type BillStatus = "UNPAID" | "PARTIAL" | "PAID";
export type PaymentMethod = "CASH" | "UPI" | "BANK_TRANSFER" | "CARD";

export interface Bill {
  id: string;
  customerId: string;
  customerName: string;
  periodMonth: number;
  periodYear: number;
  totalLiters: string;
  totalAmount: string;
  paidAmount: string;
  remainingAmount: string;
  status: BillStatus;
  generatedAt: string;
}

export interface Payment {
  id: string;
  billId: string;
  amount: string;
  paymentMethod: PaymentMethod;
  paymentDate: string;
  reference: string | null;
  notes: string | null;
}

export interface DashboardSummary {
  today: string;
  todayLiters: string;
  todaySales: string;
  monthLiters: string;
  monthSales: string;
  monthExpenses: string;
  monthProfit: string;
  outstandingDues: string;
  activeCustomers: number;
}
