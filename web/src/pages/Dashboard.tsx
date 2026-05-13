import { useQuery } from "@tanstack/react-query";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getDashboardSummary } from "@/features/dashboard/api";
import { formatCurrency, formatLiters } from "@/lib/utils";

interface MetricProps {
  title: string;
  value: string;
  hint?: string;
}

function Metric({ title, value, hint }: MetricProps) {
  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-sm text-muted-foreground font-medium">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-data-metric">{value}</div>
        {hint && <div className="mt-1 text-xs text-muted-foreground">{hint}</div>}
      </CardContent>
    </Card>
  );
}

export function DashboardPage() {
  const { data, isLoading, error } = useQuery({
    queryKey: ["dashboard-summary"],
    queryFn: getDashboardSummary,
  });

  if (isLoading) return <div className="text-muted-foreground">Loading…</div>;
  if (error || !data) return <div className="text-destructive">Failed to load dashboard</div>;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-sm text-muted-foreground">Snapshot of today and this month</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Metric title="Today's Milk" value={formatLiters(data.todayLiters)} />
        <Metric title="Today's Sales" value={formatCurrency(data.todaySales)} />
        <Metric title="Month Liters" value={formatLiters(data.monthLiters)} />
        <Metric title="Month Sales" value={formatCurrency(data.monthSales)} />
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Metric title="Month Expenses" value={formatCurrency(data.monthExpenses)} />
        <Metric
          title="Month Profit"
          value={formatCurrency(data.monthProfit)}
          hint="Sales − Expenses"
        />
        <Metric title="Outstanding Dues" value={formatCurrency(data.outstandingDues)} />
        <Metric title="Active Customers" value={String(data.activeCustomers)} />
      </div>
    </div>
  );
}
