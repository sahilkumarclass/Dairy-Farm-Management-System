import { useQuery } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { Moon, Sun } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getMyMilkEntries } from "@/features/portal/api";
import { formatCurrency, formatDate, formatLiters } from "@/lib/utils";

export function MilkPage() {
  const { t } = useTranslation();
  const { data, isLoading } = useQuery({
    queryKey: ["portal-milk"],
    queryFn: () => getMyMilkEntries({ size: 100 }),
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">{t("milk.title")}</h1>
        <p className="text-sm text-muted-foreground">{t("milk.subtitle")}</p>
      </div>
      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>{t("milk.date")}</TableHead>
                <TableHead>{t("milk.session")}</TableHead>
                <TableHead>{t("milk.type")}</TableHead>
                <TableHead>{t("milk.liters")}</TableHead>
                <TableHead>{t("milk.rate")}</TableHead>
                <TableHead className="text-right">{t("milk.amount")}</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">{t("common.loading")}</TableCell></TableRow>
              )}
              {data?.content.length === 0 && !isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">{t("milk.empty")}</TableCell></TableRow>
              )}
              {data?.content.map((m) => (
                <TableRow key={m.id}>
                  <TableCell>{formatDate(m.entryDate)}</TableCell>
                  <TableCell>
                    <span className="inline-flex items-center gap-1">
                      {m.session === "MORNING" ? <Sun className="h-4 w-4 text-amber-600" /> : <Moon className="h-4 w-4 text-indigo-600" />}
                      {m.session}
                    </span>
                  </TableCell>
                  <TableCell>{m.milkType}</TableCell>
                  <TableCell>{formatLiters(m.quantityLiters)}</TableCell>
                  <TableCell>{formatCurrency(m.ratePerLiter)}</TableCell>
                  <TableCell className="text-right font-semibold">{formatCurrency(m.totalAmount)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
