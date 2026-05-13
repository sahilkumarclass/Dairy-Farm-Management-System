import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { KeyRound, PlayCircle, Plus, Search, StopCircle } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import {
  createCustomer,
  createCustomerLogin,
  deleteCustomer,
  listCustomers,
  reactivateCustomer,
  type CreateCustomerLoginInput,
  type CustomerInput,
} from "@/features/customers/api";
import { extractErrorMessage } from "@/lib/api";
import { cn, formatCurrency } from "@/lib/utils";
import type { Customer, CustomerStatus } from "@/types/api";

type StatusFilter = CustomerStatus | "ALL";

export function CustomersPage() {
  const qc = useQueryClient();
  const [q, setQ] = useState("");
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("ACTIVE");
  const [open, setOpen] = useState(false);
  const [loginTarget, setLoginTarget] = useState<Customer | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ["customers", q, statusFilter],
    queryFn: () =>
      listCustomers({
        q: q || undefined,
        status: statusFilter === "ALL" ? undefined : statusFilter,
        size: 50,
      }),
  });

  const createMut = useMutation({
    mutationFn: createCustomer,
    onSuccess: () => {
      toast.success("Customer added");
      qc.invalidateQueries({ queryKey: ["customers"] });
      setOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const loginMut = useMutation({
    mutationFn: (input: CreateCustomerLoginInput) => createCustomerLogin(loginTarget!.id, input),
    onSuccess: () => {
      toast.success("Portal login created");
      qc.invalidateQueries({ queryKey: ["customers"] });
      setLoginTarget(null);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const stopMut = useMutation({
    mutationFn: deleteCustomer,
    onSuccess: () => {
      toast.success("Service stopped");
      qc.invalidateQueries({ queryKey: ["customers"] });
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const reactivateMut = useMutation({
    mutationFn: reactivateCustomer,
    onSuccess: () => {
      toast.success("Customer reactivated");
      qc.invalidateQueries({ queryKey: ["customers"] });
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  function confirmStop(c: Customer) {
    if (window.confirm(`Stop service for ${c.name}? They'll be marked inactive but their history is kept.`)) {
      stopMut.mutate(c.id);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Customers</h1>
          <p className="text-sm text-muted-foreground">Manage milk customers, rates, and portal logins</p>
        </div>
        <Button onClick={() => setOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" /> Add Customer
        </Button>
      </div>

      <div className="flex flex-wrap items-end gap-3">
        <div className="relative max-w-sm flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search name or phone"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            className="pl-9"
          />
        </div>
        <div className="w-40">
          <Label className="mb-1 block text-xs text-muted-foreground">Show</Label>
          <Select value={statusFilter} onValueChange={(v) => setStatusFilter(v as StatusFilter)}>
            <SelectTrigger><SelectValue /></SelectTrigger>
            <SelectContent>
              <SelectItem value="ACTIVE">Active only</SelectItem>
              <SelectItem value="INACTIVE">Inactive only</SelectItem>
              <SelectItem value="ALL">All</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Phone</TableHead>
                <TableHead>Address</TableHead>
                <TableHead>Custom Rate</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Actions</TableHead>
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
                  <TableCell colSpan={6} className="text-center text-muted-foreground">No customers match</TableCell>
                </TableRow>
              )}
              {data?.content.map((c: Customer) => {
                const inactive = c.status === "INACTIVE";
                return (
                  <TableRow key={c.id} className={cn(inactive && "opacity-60")}>
                    <TableCell className="font-medium">{c.name}</TableCell>
                    <TableCell>{c.phone}</TableCell>
                    <TableCell className="text-muted-foreground">{c.address ?? "—"}</TableCell>
                    <TableCell>{c.customMilkRate ? formatCurrency(c.customMilkRate) : "—"}</TableCell>
                    <TableCell>
                      <Badge variant={inactive ? "outline" : "success"}>{c.status}</Badge>
                    </TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button size="sm" variant="outline" className="gap-1" onClick={() => setLoginTarget(c)}>
                          <KeyRound className="h-3.5 w-3.5" /> Create login
                        </Button>
                        {inactive ? (
                          <Button
                            size="sm"
                            variant="outline"
                            className="gap-1"
                            onClick={() => reactivateMut.mutate(c.id)}
                            disabled={reactivateMut.isPending}
                          >
                            <PlayCircle className="h-3.5 w-3.5" /> Reactivate
                          </Button>
                        ) : (
                          <Button
                            size="sm"
                            variant="outline"
                            className="gap-1 text-destructive hover:bg-destructive/10"
                            onClick={() => confirmStop(c)}
                            disabled={stopMut.isPending}
                          >
                            <StopCircle className="h-3.5 w-3.5" /> Stop service
                          </Button>
                        )}
                      </div>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <CustomerDialog
        open={open}
        onOpenChange={setOpen}
        onSubmit={(v) => createMut.mutate(v)}
        submitting={createMut.isPending}
      />

      <CustomerLoginDialog
        open={!!loginTarget}
        onOpenChange={(v) => !v && setLoginTarget(null)}
        customer={loginTarget}
        onSubmit={(v) => loginMut.mutate(v)}
        submitting={loginMut.isPending}
      />
    </div>
  );
}

interface DialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  onSubmit: (v: CustomerInput) => void;
  submitting: boolean;
}

function CustomerDialog({ open, onOpenChange, onSubmit, submitting }: DialogProps) {
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");
  const [rate, setRate] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({
      name,
      phone,
      address: address || null,
      customMilkRate: rate ? Number(rate) : null,
      status: "ACTIVE",
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>New customer</DialogTitle>
          <DialogDescription>Add a customer to start logging milk entries</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">Name</Label>
            <Input id="name" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="phone">Phone</Label>
            <Input id="phone" value={phone} onChange={(e) => setPhone(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="address">Address</Label>
            <Input id="address" value={address} onChange={(e) => setAddress(e.target.value)} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="rate">Custom rate per liter (₹)</Label>
            <Input id="rate" type="number" step="0.01" min="0" value={rate}
                   onChange={(e) => setRate(e.target.value)} />
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

interface LoginDialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  customer: Customer | null;
  onSubmit: (v: CreateCustomerLoginInput) => void;
  submitting: boolean;
}

function CustomerLoginDialog({ open, onOpenChange, customer, onSubmit, submitting }: LoginDialogProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({ username, password });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Create portal login</DialogTitle>
          {customer && (
            <DialogDescription>
              Issue portal credentials for <strong>{customer.name}</strong>. They'll be able to log in at the customer portal app.
            </DialogDescription>
          )}
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="cu">Username</Label>
            <Input id="cu" value={username} onChange={(e) => setUsername(e.target.value)} required />
          </div>
          <div className="space-y-2">
            <Label htmlFor="cp">Password</Label>
            <Input id="cp" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button type="submit" disabled={submitting}>{submitting ? "Creating…" : "Create login"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
