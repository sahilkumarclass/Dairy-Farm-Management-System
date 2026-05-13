import { useQuery } from "@tanstack/react-query";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getMyBills } from "@/features/portal/api";
import { formatCurrency, formatLiters } from "@/lib/utils";
import type { BillStatus } from "@/types/api";

const MONTH_NAMES = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

const STATUS_VARIANT: Record<BillStatus, "success" | "warning" | "destructive"> = {
  PAID: "success",
  PARTIAL: "warning",
  UNPAID: "destructive",
};

export function PortalBills() {
  const { data, isLoading } = useQuery({ queryKey: ["portal-bills"], queryFn: getMyBills });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Bills</h1>
        <p className="text-sm text-muted-foreground">All your monthly bills and dues</p>
      </div>
      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Period</TableHead>
                <TableHead>Liters</TableHead>
                <TableHead>Total</TableHead>
                <TableHead>Paid</TableHead>
                <TableHead>Remaining</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">Loading…</TableCell></TableRow>
              )}
              {data?.length === 0 && !isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">No bills yet</TableCell></TableRow>
              )}
              {data?.map((b) => (
                <TableRow key={b.id}>
                  <TableCell className="font-medium">
                    {MONTH_NAMES[b.periodMonth - 1]} {b.periodYear}
                  </TableCell>
                  <TableCell>{formatLiters(b.totalLiters)}</TableCell>
                  <TableCell>{formatCurrency(b.totalAmount)}</TableCell>
                  <TableCell>{formatCurrency(b.paidAmount)}</TableCell>
                  <TableCell className="font-semibold">{formatCurrency(b.remainingAmount)}</TableCell>
                  <TableCell><Badge variant={STATUS_VARIANT[b.status]}>{b.status}</Badge></TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
