import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus } from "lucide-react";
import { toast } from "sonner";
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
import { listCustomers } from "@/features/customers/api";
import { createMilkEntry, listMilkEntries, type MilkEntryInput } from "@/features/milk/api";
import { extractErrorMessage } from "@/lib/api";
import { formatCurrency, formatDate, formatLiters } from "@/lib/utils";

export function MilkEntriesPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ["milk-entries"],
    queryFn: () => listMilkEntries({ size: 50 }),
  });

  const customers = useQuery({
    queryKey: ["customers", "for-milk"],
    queryFn: () => listCustomers({ status: "ACTIVE", size: 200 }),
  });

  const createMut = useMutation({
    mutationFn: createMilkEntry,
    onSuccess: () => {
      toast.success("Milk entry logged");
      qc.invalidateQueries({ queryKey: ["milk-entries"] });
      qc.invalidateQueries({ queryKey: ["dashboard-summary"] });
      setOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Milk Entries</h1>
          <p className="text-sm text-muted-foreground">Log each customer's milk pickup</p>
        </div>
        <Button onClick={() => setOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" /> New Entry
        </Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Date</TableHead>
                <TableHead>Customer</TableHead>
                <TableHead>Session</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Liters</TableHead>
                <TableHead>Rate</TableHead>
                <TableHead>Total</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow>
                  <TableCell colSpan={7} className="text-center text-muted-foreground">
                    Loading…
                  </TableCell>
                </TableRow>
              )}
              {data?.content.length === 0 && !isLoading && (
                <TableRow>
                  <TableCell colSpan={7} className="text-center text-muted-foreground">
                    No entries yet
                  </TableCell>
                </TableRow>
              )}
              {data?.content.map((m) => (
                <TableRow key={m.id}>
                  <TableCell>{formatDate(m.entryDate)}</TableCell>
                  <TableCell className="font-medium">{m.customerName}</TableCell>
                  <TableCell>{m.session}</TableCell>
                  <TableCell>{m.milkType}</TableCell>
                  <TableCell>{formatLiters(m.quantityLiters)}</TableCell>
                  <TableCell>{formatCurrency(m.ratePerLiter)}</TableCell>
                  <TableCell className="font-semibold">{formatCurrency(m.totalAmount)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <MilkEntryDialog
        open={open}
        onOpenChange={setOpen}
        customers={customers.data?.content ?? []}
        onSubmit={(v) => createMut.mutate(v)}
        submitting={createMut.isPending}
      />
    </div>
  );
}

interface DialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  customers: { id: string; name: string }[];
  onSubmit: (v: MilkEntryInput) => void;
  submitting: boolean;
}

function MilkEntryDialog({ open, onOpenChange, customers, onSubmit, submitting }: DialogProps) {
  const [customerId, setCustomerId] = useState("");
  const [milkType, setMilkType] = useState<MilkEntryInput["milkType"]>("COW");
  const [session, setSession] = useState<MilkEntryInput["session"]>("MORNING");
  const [quantity, setQuantity] = useState("");
  const [rate, setRate] = useState("");
  const [entryDate, setEntryDate] = useState(new Date().toISOString().slice(0, 10));

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!customerId) {
      toast.error("Pick a customer");
      return;
    }
    onSubmit({
      customerId,
      milkType,
      session,
      quantityLiters: Number(quantity),
      ratePerLiter: rate ? Number(rate) : null,
      entryDate,
      notes: null,
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>New milk entry</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label>Customer</Label>
            <Select value={customerId} onValueChange={setCustomerId}>
              <SelectTrigger><SelectValue placeholder="Select customer" /></SelectTrigger>
              <SelectContent>
                {customers.map((c) => (
                  <SelectItem key={c.id} value={c.id}>{c.name}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label>Session</Label>
              <Select value={session} onValueChange={(v) => setSession(v as MilkEntryInput["session"])}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="MORNING">Morning</SelectItem>
                  <SelectItem value="EVENING">Evening</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Type</Label>
              <Select value={milkType} onValueChange={(v) => setMilkType(v as MilkEntryInput["milkType"])}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="COW">Cow</SelectItem>
                  <SelectItem value="BUFFALO">Buffalo</SelectItem>
                  <SelectItem value="MIXED">Mixed</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="qty">Liters</Label>
              <Input id="qty" type="number" step="0.001" min="0.001" value={quantity}
                     onChange={(e) => setQuantity(e.target.value)} required />
            </div>
            <div className="space-y-2">
              <Label htmlFor="rate">Rate (optional — uses customer default)</Label>
              <Input id="rate" type="number" step="0.01" min="0" value={rate}
                     onChange={(e) => setRate(e.target.value)} />
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="date">Date</Label>
            <Input id="date" type="date" value={entryDate} onChange={(e) => setEntryDate(e.target.value)} required />
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
