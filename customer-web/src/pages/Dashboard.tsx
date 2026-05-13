import { useQuery } from "@tanstack/react-query";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getMyDashboard } from "@/features/portal/api";
import { formatCurrency, formatLiters } from "@/lib/utils";
import type { BillStatus } from "@/types/api";

const STATUS_VARIANT: Record<BillStatus, "success" | "warning" | "destructive"> = {
  PAID: "success",
  PARTIAL: "warning",
  UNPAID: "destructive",
};

export function DashboardPage() {
  const { data, isLoading, error } = useQuery({
    queryKey: ["portal-dashboard"],
    queryFn: getMyDashboard,
  });

  if (isLoading) return <div className="text-muted-foreground">Loading…</div>;
  if (error || !data) return <div className="text-destructive">Couldn't load your dashboard.</div>;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Welcome, {data.customer.name}</h1>
        <p className="text-sm text-muted-foreground">Your milk delivery and billing at a glance</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Metric title="Milk this month" value={formatLiters(data.monthLiters)} />
        <Metric title="Estimated bill" value={formatCurrency(data.monthAmount)} />
        <Metric title="Outstanding total" value={formatCurrency(data.outstandingTotal)} />
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Current month status</CardTitle>
          </CardHeader>
          <CardContent>
            {data.currentMonthStatus ? (
              <Badge variant={STATUS_VARIANT[data.currentMonthStatus]}>{data.currentMonthStatus}</Badge>
            ) : (
              <span className="text-sm text-muted-foreground">Bill not generated yet</span>
            )}
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader><CardTitle>Your details</CardTitle></CardHeader>
        <CardContent className="space-y-2 text-sm">
          <div><span className="text-muted-foreground">Phone:</span> {data.customer.phone}</div>
          <div><span className="text-muted-foreground">Address:</span> {data.customer.address ?? "—"}</div>
          <div><span className="text-muted-foreground">Rate per liter:</span> {data.customer.customMilkRate ? formatCurrency(data.customer.customMilkRate) : "uses default"}</div>
        </CardContent>
      </Card>
    </div>
  );
}

function Metric({ title, value }: { title: string; value: string }) {
  return (
    <Card>
      <CardHeader className="pb-2"><CardTitle className="text-sm font-medium text-muted-foreground">{title}</CardTitle></CardHeader>
      <CardContent><div className="text-data-metric">{value}</div></CardContent>
    </Card>
  );
}
