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

export interface StaffUser {
  id: string;
  username: string;
  fullName: string;
  phone: string | null;
  role: Role;
  enabled: boolean;
  createdAt: string;
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
  userId: string | null;
  username: string | null;
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
  cowId: string | null;
  cowTagNo: string | null;
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

// ----- Herd -----
export type HealthStatus = "HEALTHY" | "UNDER_TREATMENT" | "DRY" | "SOLD" | "DECEASED";
export type Gender = "FEMALE" | "MALE";
export type HealthEventType = "VACCINATION" | "VET_VISIT" | "TREATMENT" | "CHECKUP" | "OTHER";

export interface Cow {
  id: string;
  tagNo: string;
  name: string | null;
  breed: string | null;
  gender: Gender;
  ageMonths: number | null;
  healthStatus: HealthStatus;
  dailyYieldEstimateLiters: string | null;
  dateAcquired: string | null;
  notes: string | null;
}

export interface CowDetail {
  cow: Cow;
  monthLiters: string;
  lifetimeLiters: string;
  monthHealthCost: string;
  monthExpenseTotal: string;
}

export interface CowSummary {
  totalCows: number;
  healthyCount: number;
  underTreatmentCount: number;
  dryCount: number;
  monthLiters: string;
  monthHealthCost: string;
  monthFeedAndOtherCost: string;
}

export interface CowMilkProductionEntry {
  id: string;
  cowId: string;
  cowTagNo: string;
  productionDate: string;
  session: MilkSession;
  liters: string;
  notes: string | null;
}

export interface CowHealthLog {
  id: string;
  cowId: string;
  cowTagNo: string;
  eventType: HealthEventType;
  eventDate: string;
  nextDueDate: string | null;
  vetName: string | null;
  cost: string | null;
  notes: string | null;
}

// ----- Customer portal -----
export interface CustomerSelfDashboard {
  customer: Customer;
  monthLiters: string;
  monthAmount: string;
  outstandingTotal: string;
  currentMonthStatus: BillStatus | null;
}
