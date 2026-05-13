import { useQuery } from "@tanstack/react-query";
import { Moon, Sun } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getMyMilkEntries } from "@/features/portal/api";
import { formatCurrency, formatDate, formatLiters } from "@/lib/utils";

export function PortalMilk() {
  const { data, isLoading } = useQuery({
    queryKey: ["portal-milk"],
    queryFn: () => getMyMilkEntries({ size: 100 }),
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Milk history</h1>
        <p className="text-sm text-muted-foreground">Every entry recorded for you</p>
      </div>
      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Date</TableHead>
                <TableHead>Session</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Liters</TableHead>
                <TableHead>Rate</TableHead>
                <TableHead className="text-right">Amount</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">Loading…</TableCell></TableRow>
              )}
              {data?.content.length === 0 && !isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">No entries yet</TableCell></TableRow>
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
