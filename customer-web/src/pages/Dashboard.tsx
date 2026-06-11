import { useQuery } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
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
  const { t } = useTranslation();
  const { data, isLoading, error } = useQuery({
    queryKey: ["portal-dashboard"],
    queryFn: getMyDashboard,
  });

  if (isLoading) return <div className="text-muted-foreground">{t("common.loading")}</div>;
  if (error || !data) return <div className="text-destructive">{t("dashboard.loadError")}</div>;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">{t("dashboard.welcome", { name: data.customer.name })}</h1>
        <p className="text-sm text-muted-foreground">{t("dashboard.subtitle")}</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Metric title={t("dashboard.milkThisMonth")} value={formatLiters(data.monthLiters)} />
        <Metric title={t("dashboard.estimatedBill")} value={formatCurrency(data.monthAmount)} />
        <Metric title={t("dashboard.outstandingTotal")} value={formatCurrency(data.outstandingTotal)} />
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">{t("dashboard.currentMonthStatus")}</CardTitle>
          </CardHeader>
          <CardContent>
            {data.currentMonthStatus ? (
              <Badge variant={STATUS_VARIANT[data.currentMonthStatus]}>{t(`status.${data.currentMonthStatus}`)}</Badge>
            ) : (
              <span className="text-sm text-muted-foreground">{t("dashboard.billNotGenerated")}</span>
            )}
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader><CardTitle>{t("dashboard.yourDetails")}</CardTitle></CardHeader>
        <CardContent className="space-y-2 text-sm">
          <div><span className="text-muted-foreground">{t("dashboard.phone")}</span> {data.customer.phone}</div>
          <div><span className="text-muted-foreground">{t("dashboard.address")}</span> {data.customer.address ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("dashboard.ratePerLiter")}</span> {data.customer.customMilkRate ? formatCurrency(data.customer.customMilkRate) : t("dashboard.usesDefault")}</div>
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
