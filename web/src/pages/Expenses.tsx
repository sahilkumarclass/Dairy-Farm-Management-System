import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus } from "lucide-react";
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
import { createExpense, listExpenses, type ExpenseInput } from "@/features/expenses/api";
import { listCows } from "@/features/herd/api";
import { extractErrorMessage } from "@/lib/api";
import { formatCurrency, formatDate } from "@/lib/utils";
import type { ExpenseCategory } from "@/types/api";

const CATEGORIES: ExpenseCategory[] = [
  "FEED", "VETERINARY", "LABOR", "UTILITIES", "EQUIPMENT", "TRANSPORT", "OTHER",
];

export function ExpensesPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ["expenses"],
    queryFn: () => listExpenses({ size: 50 }),
  });

  const createMut = useMutation({
    mutationFn: createExpense,
    onSuccess: () => {
      toast.success("Expense recorded");
      qc.invalidateQueries({ queryKey: ["expenses"] });
      qc.invalidateQueries({ queryKey: ["dashboard-summary"] });
      setOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Expenses</h1>
          <p className="text-sm text-muted-foreground">Track operational spend by category</p>
        </div>
        <Button onClick={() => setOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" /> New Expense
        </Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Date</TableHead>
                <TableHead>Category</TableHead>
                <TableHead>Cow</TableHead>
                <TableHead>Notes</TableHead>
                <TableHead className="text-right">Amount</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow>
                  <TableCell colSpan={5} className="text-center text-muted-foreground">Loading…</TableCell>
                </TableRow>
              )}
              {data?.content.length === 0 && !isLoading && (
                <TableRow>
                  <TableCell colSpan={5} className="text-center text-muted-foreground">No expenses yet</TableCell>
                </TableRow>
              )}
              {data?.content.map((e) => (
                <TableRow key={e.id}>
                  <TableCell>{formatDate(e.expenseDate)}</TableCell>
                  <TableCell><Badge variant="outline">{e.category}</Badge></TableCell>
                  <TableCell className="text-muted-foreground">{e.cowTagNo ?? "—"}</TableCell>
                  <TableCell className="text-muted-foreground">{e.notes ?? "—"}</TableCell>
                  <TableCell className="text-right font-semibold">{formatCurrency(e.amount)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <ExpenseDialog
        open={open}
        onOpenChange={setOpen}
        onSubmit={(v) => createMut.mutate(v)}
        submitting={createMut.isPending}
      />
    </div>
  );
}

interface DialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  onSubmit: (v: ExpenseInput) => void;
  submitting: boolean;
}

function ExpenseDialog({ open, onOpenChange, onSubmit, submitting }: DialogProps) {
  const [category, setCategory] = useState<ExpenseCategory>("FEED");
  const [amount, setAmount] = useState("");
  const [notes, setNotes] = useState("");
  const [expenseDate, setExpenseDate] = useState(new Date().toISOString().slice(0, 10));
  const [cowId, setCowId] = useState<string>("NONE");

  const cows = useQuery({
    queryKey: ["cows", "for-expense"],
    queryFn: () => listCows({ size: 200 }),
  });

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({
      category,
      amount: Number(amount),
      notes: notes || null,
      expenseDate,
      cowId: cowId === "NONE" ? null : cowId,
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>New expense</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label>Category</Label>
            <Select value={category} onValueChange={(v) => setCategory(v as ExpenseCategory)}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                {CATEGORIES.map((c) => <SelectItem key={c} value={c}>{c}</SelectItem>)}
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="amount">Amount (₹)</Label>
            <Input id="amount" type="number" step="0.01" min="0" value={amount}
                   onChange={(e) => setAmount(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="date">Date</Label>
            <Input id="date" type="date" value={expenseDate}
                   onChange={(e) => setExpenseDate(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label>Tag to a cow (optional)</Label>
            <Select value={cowId} onValueChange={setCowId}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="NONE">— Not tagged —</SelectItem>
                {cows.data?.content.map((c) => (
                  <SelectItem key={c.id} value={c.id}>
                    {c.tagNo}{c.name ? ` · ${c.name}` : ""}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="notes">Notes</Label>
            <Input id="notes" value={notes} onChange={(e) => setNotes(e.target.value)} />
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
