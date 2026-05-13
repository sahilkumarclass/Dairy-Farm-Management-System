import { useEffect, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, Trash2 } from "lucide-react";
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
import {
  createExpense,
  deleteExpense,
  listExpenses,
  updateExpense,
  type ExpenseInput,
} from "@/features/expenses/api";
import { listCows } from "@/features/herd/api";
import { extractErrorMessage } from "@/lib/api";
import { formatCurrency, formatDate } from "@/lib/utils";
import type { Expense, ExpenseCategory } from "@/types/api";

const CATEGORIES: ExpenseCategory[] = [
  "FEED", "VETERINARY", "LABOR", "UTILITIES", "EQUIPMENT", "TRANSPORT", "OTHER",
];

type Editing = Expense | "new" | null;

export function ExpensesPage() {
  const qc = useQueryClient();
  const [editing, setEditing] = useState<Editing>(null);

  const { data, isLoading } = useQuery({
    queryKey: ["expenses"],
    queryFn: () => listExpenses({ size: 50 }),
  });

  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ["expenses"] });
    qc.invalidateQueries({ queryKey: ["dashboard-summary"] });
    qc.invalidateQueries({ queryKey: ["herd-summary"] });
  };

  const createMut = useMutation({
    mutationFn: createExpense,
    onSuccess: () => {
      toast.success("Expense recorded");
      invalidate();
      setEditing(null);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const updateMut = useMutation({
    mutationFn: ({ id, input }: { id: string; input: ExpenseInput }) => updateExpense(id, input),
    onSuccess: () => {
      toast.success("Expense updated");
      invalidate();
      setEditing(null);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const deleteMut = useMutation({
    mutationFn: deleteExpense,
    onSuccess: () => {
      toast.success("Expense deleted");
      invalidate();
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  function handleSubmit(values: ExpenseInput) {
    if (editing && editing !== "new") {
      updateMut.mutate({ id: editing.id, input: values });
    } else {
      createMut.mutate(values);
    }
  }

  function handleDelete(e: React.MouseEvent, exp: Expense) {
    e.stopPropagation();
    if (window.confirm(`Delete ${exp.category.toLowerCase()} expense of ${formatCurrency(exp.amount)} on ${formatDate(exp.expenseDate)}?`)) {
      deleteMut.mutate(exp.id);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Expenses</h1>
          <p className="text-sm text-muted-foreground">Click any row to edit. Use the trash icon to delete.</p>
        </div>
        <Button onClick={() => setEditing("new")} className="gap-2">
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
                <TableHead className="w-12"></TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow>
                  <TableCell colSpan={6} className="text-center text-muted-foreground">Loading…</TableCell>
                </TableRow>
              )}
              {data?.content.length === 0 && !isLoading && (
                <TableRow>
                  <TableCell colSpan={6} className="text-center text-muted-foreground">No expenses yet</TableCell>
                </TableRow>
              )}
              {data?.content.map((e) => (
                <TableRow
                  key={e.id}
                  onClick={() => setEditing(e)}
                  className="cursor-pointer"
                >
                  <TableCell>{formatDate(e.expenseDate)}</TableCell>
                  <TableCell><Badge variant="outline">{e.category}</Badge></TableCell>
                  <TableCell className="text-muted-foreground">{e.cowTagNo ?? "—"}</TableCell>
                  <TableCell className="text-muted-foreground">{e.notes ?? "—"}</TableCell>
                  <TableCell className="text-right font-semibold">{formatCurrency(e.amount)}</TableCell>
                  <TableCell className="text-right">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={(ev) => handleDelete(ev, e)}
                      className="h-8 w-8 text-muted-foreground hover:text-destructive"
                      aria-label="Delete expense"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <ExpenseDialog
        editing={editing}
        onOpenChange={(v) => !v && setEditing(null)}
        onSubmit={handleSubmit}
        submitting={createMut.isPending || updateMut.isPending}
      />
    </div>
  );
}

interface DialogProps {
  editing: Editing;
  onOpenChange: (v: boolean) => void;
  onSubmit: (v: ExpenseInput) => void;
  submitting: boolean;
}

function ExpenseDialog({ editing, onOpenChange, onSubmit, submitting }: DialogProps) {
  const isEdit = editing !== null && editing !== "new";
  const initial = isEdit ? (editing as Expense) : null;

  const [category, setCategory] = useState<ExpenseCategory>("FEED");
  const [amount, setAmount] = useState("");
  const [notes, setNotes] = useState("");
  const [expenseDate, setExpenseDate] = useState(new Date().toISOString().slice(0, 10));
  const [cowId, setCowId] = useState<string>("NONE");

  const cows = useQuery({
    queryKey: ["cows", "for-expense"],
    queryFn: () => listCows({ size: 200 }),
  });

  // Seed form whenever the dialog target changes
  useEffect(() => {
    if (initial) {
      setCategory(initial.category);
      setAmount(initial.amount);
      setNotes(initial.notes ?? "");
      setExpenseDate(initial.expenseDate);
      setCowId(initial.cowId ?? "NONE");
    } else if (editing === "new") {
      setCategory("FEED");
      setAmount("");
      setNotes("");
      setExpenseDate(new Date().toISOString().slice(0, 10));
      setCowId("NONE");
    }
  }, [editing, initial]);

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
    <Dialog open={editing !== null} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{isEdit ? "Edit expense" : "New expense"}</DialogTitle>
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
            <Button type="submit" disabled={submitting}>{submitting ? "Saving…" : isEdit ? "Update" : "Save"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
