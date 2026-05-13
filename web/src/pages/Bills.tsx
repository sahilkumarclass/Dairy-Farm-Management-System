import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { FileText, IndianRupee } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { generateBills, listBills, recordPayment, type PaymentInput } from "@/features/billing/api";
import { extractErrorMessage } from "@/lib/api";
import { formatCurrency, formatLiters } from "@/lib/utils";
import type { Bill, BillStatus, PaymentMethod } from "@/types/api";

const MONTH_NAMES = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

const STATUS_VARIANT: Record<BillStatus, "success" | "warning" | "destructive"> = {
  PAID: "success",
  PARTIAL: "warning",
  UNPAID: "destructive",
};

export function BillsPage() {
  const qc = useQueryClient();
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [payOpen, setPayOpen] = useState(false);
  const [target, setTarget] = useState<Bill | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ["bills", year, month],
    queryFn: () => listBills({ year, month, size: 100 }),
  });

  const generateMut = useMutation({
    mutationFn: () => generateBills(month, year),
    onSuccess: (rows) => {
      toast.success(`Generated ${rows.length} bills for ${MONTH_NAMES[month - 1]} ${year}`);
      qc.invalidateQueries({ queryKey: ["bills"] });
      qc.invalidateQueries({ queryKey: ["dashboard-summary"] });
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const payMut = useMutation({
    mutationFn: (input: PaymentInput) => recordPayment(target!.id, input),
    onSuccess: () => {
      toast.success("Payment recorded");
      qc.invalidateQueries({ queryKey: ["bills"] });
      qc.invalidateQueries({ queryKey: ["dashboard-summary"] });
      setPayOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Bills</h1>
          <p className="text-sm text-muted-foreground">Monthly customer bills and payments</p>
        </div>
        <Button onClick={() => generateMut.mutate()} disabled={generateMut.isPending} className="gap-2">
          <FileText className="h-4 w-4" />
          {generateMut.isPending ? "Generating…" : "Generate bills"}
        </Button>
      </div>

      <div className="flex gap-3">
        <div className="w-40">
          <Label className="mb-1 block text-xs text-muted-foreground">Month</Label>
          <Select value={String(month)} onValueChange={(v) => setMonth(Number(v))}>
            <SelectTrigger><SelectValue /></SelectTrigger>
            <SelectContent>
              {MONTH_NAMES.map((m, i) => <SelectItem key={i} value={String(i + 1)}>{m}</SelectItem>)}
            </SelectContent>
          </Select>
        </div>
        <div className="w-32">
          <Label className="mb-1 block text-xs text-muted-foreground">Year</Label>
          <Input type="number" value={year} onChange={(e) => setYear(Number(e.target.value))} />
        </div>
      </div>

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Customer</TableHead>
                <TableHead>Liters</TableHead>
                <TableHead>Total</TableHead>
                <TableHead>Paid</TableHead>
                <TableHead>Remaining</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow><TableCell colSpan={7} className="text-center text-muted-foreground">Loading…</TableCell></TableRow>
              )}
              {data?.content.length === 0 && !isLoading && (
                <TableRow>
                  <TableCell colSpan={7} className="text-center text-muted-foreground">
                    No bills for this period — click "Generate bills" to create them from milk entries.
                  </TableCell>
                </TableRow>
              )}
              {data?.content.map((b) => (
                <TableRow key={b.id}>
                  <TableCell className="font-medium">{b.customerName}</TableCell>
                  <TableCell>{formatLiters(b.totalLiters)}</TableCell>
                  <TableCell>{formatCurrency(b.totalAmount)}</TableCell>
                  <TableCell>{formatCurrency(b.paidAmount)}</TableCell>
                  <TableCell className="font-semibold">{formatCurrency(b.remainingAmount)}</TableCell>
                  <TableCell><Badge variant={STATUS_VARIANT[b.status]}>{b.status}</Badge></TableCell>
                  <TableCell className="text-right">
                    {b.status !== "PAID" && (
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => { setTarget(b); setPayOpen(true); }}
                        className="gap-1"
                      >
                        <IndianRupee className="h-3.5 w-3.5" /> Record payment
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <PaymentDialog
        open={payOpen}
        onOpenChange={setPayOpen}
        bill={target}
        onSubmit={(v) => payMut.mutate(v)}
        submitting={payMut.isPending}
      />
    </div>
  );
}

interface PaymentDialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  bill: Bill | null;
  onSubmit: (v: PaymentInput) => void;
  submitting: boolean;
}

function PaymentDialog({ open, onOpenChange, bill, onSubmit, submitting }: PaymentDialogProps) {
  const [amount, setAmount] = useState("");
  const [method, setMethod] = useState<PaymentMethod>("CASH");
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [reference, setReference] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({
      amount: Number(amount),
      paymentMethod: method,
      paymentDate: date,
      reference: reference || null,
      notes: null,
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Record payment</DialogTitle>
        </DialogHeader>
        {bill && (
          <div className="rounded-md border bg-muted/40 p-3 text-sm">
            <div><span className="text-muted-foreground">Customer:</span> <strong>{bill.customerName}</strong></div>
            <div><span className="text-muted-foreground">Remaining:</span> <strong>{formatCurrency(bill.remainingAmount)}</strong></div>
          </div>
        )}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="amt">Amount</Label>
            <Input id="amt" type="number" step="0.01" min="0.01" value={amount}
                   onChange={(e) => setAmount(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label>Method</Label>
            <Select value={method} onValueChange={(v) => setMethod(v as PaymentMethod)}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="CASH">Cash</SelectItem>
                <SelectItem value="UPI">UPI</SelectItem>
                <SelectItem value="BANK_TRANSFER">Bank transfer</SelectItem>
                <SelectItem value="CARD">Card</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="pay-date">Date</Label>
            <Input id="pay-date" type="date" value={date} onChange={(e) => setDate(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="ref">Reference ID (optional)</Label>
            <Input
              id="ref"
              value={reference}
              onChange={(e) => setReference(e.target.value)}
              placeholder="UPI ref / txn id — leave blank if none"
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button type="submit" disabled={submitting}>{submitting ? "Saving…" : "Save"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
