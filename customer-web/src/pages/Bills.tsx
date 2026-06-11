import { useQuery } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getMyBills } from "@/features/portal/api";
import { formatCurrency, formatLiters } from "@/lib/utils";
import type { BillStatus } from "@/types/api";

const STATUS_VARIANT: Record<BillStatus, "success" | "warning" | "destructive"> = {
  PAID: "success",
  PARTIAL: "warning",
  UNPAID: "destructive",
};

export function BillsPage() {
  const { t } = useTranslation();
  const months = t("months", { returnObjects: true }) as string[];
  const { data, isLoading } = useQuery({ queryKey: ["portal-bills"], queryFn: getMyBills });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">{t("bills.title")}</h1>
        <p className="text-sm text-muted-foreground">{t("bills.subtitle")}</p>
      </div>
      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>{t("bills.period")}</TableHead>
                <TableHead>{t("bills.liters")}</TableHead>
                <TableHead>{t("bills.total")}</TableHead>
                <TableHead>{t("bills.paid")}</TableHead>
                <TableHead>{t("bills.remaining")}</TableHead>
                <TableHead>{t("bills.status")}</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">{t("common.loading")}</TableCell></TableRow>
              )}
              {data?.length === 0 && !isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">{t("bills.empty")}</TableCell></TableRow>
              )}
              {data?.map((b) => (
                <TableRow key={b.id}>
                  <TableCell className="font-medium">
                    {months[b.periodMonth - 1]} {b.periodYear}
                  </TableCell>
                  <TableCell>{formatLiters(b.totalLiters)}</TableCell>
                  <TableCell>{formatCurrency(b.totalAmount)}</TableCell>
                  <TableCell>{formatCurrency(b.paidAmount)}</TableCell>
                  <TableCell className="font-semibold">{formatCurrency(b.remainingAmount)}</TableCell>
                  <TableCell><Badge variant={STATUS_VARIANT[b.status]}>{t(`status.${b.status}`)}</Badge></TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
