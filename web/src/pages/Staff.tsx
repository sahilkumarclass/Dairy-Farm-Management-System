import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Lock,
  Pause,
  Pencil,
  PlayCircle,
  Plus,
  Trash2,
  UserCog,
  Wallet,
} from "lucide-react";
import { toast } from "sonner";
import { ResetPasswordDialog } from "@/components/auth/ResetPasswordDialog";
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
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  createStaff,
  deleteStaff,
  disableStaff,
  enableStaff,
  listStaff,
  payStaff,
  updateStaff,
  type PayStaffInput,
  type StaffCreateInput,
  type StaffUpdateInput,
} from "@/features/staff/api";
import { extractErrorMessage } from "@/lib/api";
import { resetUserPassword } from "@/features/customers/api";
import { useAuthStore } from "@/stores/auth";
import { cn, formatCurrency } from "@/lib/utils";
import type { StaffUser } from "@/types/api";

export function StaffPage() {
  const qc = useQueryClient();
  const currentUserId = useAuthStore((s) => s.user?.userId);
  const [addOpen, setAddOpen] = useState(false);
  const [editTarget, setEditTarget] = useState<StaffUser | null>(null);
  const [resetTarget, setResetTarget] = useState<StaffUser | null>(null);
  const [payTarget, setPayTarget] = useState<StaffUser | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ["staff"],
    queryFn: listStaff,
  });

  const invalidate = () => qc.invalidateQueries({ queryKey: ["staff"] });

  const createMut = useMutation({
    mutationFn: createStaff,
    onSuccess: () => {
      toast.success("Staff added");
      invalidate();
      setAddOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const updateMut = useMutation({
    mutationFn: (input: StaffUpdateInput) => updateStaff(editTarget!.id, input),
    onSuccess: () => {
      toast.success("Staff updated");
      invalidate();
      setEditTarget(null);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const enableMut = useMutation({
    mutationFn: enableStaff,
    onSuccess: () => {
      toast.success("Staff enabled");
      invalidate();
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const disableMut = useMutation({
    mutationFn: disableStaff,
    onSuccess: () => {
      toast.success("Staff disabled");
      invalidate();
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const deleteMut = useMutation({
    mutationFn: deleteStaff,
    onSuccess: () => {
      toast.success("Staff deleted");
      invalidate();
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const resetMut = useMutation({
    mutationFn: (password: string) => resetUserPassword(resetTarget!.id, password),
    onSuccess: () => {
      toast.success("Password reset");
      setResetTarget(null);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const payMut = useMutation({
    mutationFn: (input: PayStaffInput) => payStaff(payTarget!, input),
    onSuccess: () => {
      toast.success("Payment recorded");
      qc.invalidateQueries({ queryKey: ["expenses"] });
      setPayTarget(null);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  function confirmDelete(s: StaffUser) {
    if (
      window.confirm(
        `Permanently delete staff "${s.fullName}" (${s.username})? This cannot be undone.`,
      )
    ) {
      deleteMut.mutate(s.id);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col items-start justify-between gap-3 sm:flex-row sm:items-end">
        <div>
          <h1 className="flex items-center gap-2 text-2xl font-bold tracking-tight">
            <UserCog className="h-6 w-6" /> Staff
          </h1>
          <p className="text-sm text-muted-foreground">
            Manage farm hands — add, pay, enable/disable, or remove staff accounts
          </p>
        </div>
        <Button onClick={() => setAddOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" /> Add Staff
        </Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Username</TableHead>
                  <TableHead>Full name</TableHead>
                  <TableHead>Phone</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoading && (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center text-muted-foreground">
                      Loading…
                    </TableCell>
                  </TableRow>
                )}
                {data?.length === 0 && !isLoading && (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center text-muted-foreground">
                      No staff yet. Click <strong>Add Staff</strong> to invite your first hand.
                    </TableCell>
                  </TableRow>
                )}
                {data?.map((s) => {
                  const disabled = !s.enabled;
                  const isSelf = s.id === currentUserId;
                  return (
                    <TableRow key={s.id} className={cn(disabled && "opacity-60")}>
                      <TableCell className="font-medium">{s.username}</TableCell>
                      <TableCell>{s.fullName}</TableCell>
                      <TableCell className="text-muted-foreground">{s.phone ?? "—"}</TableCell>
                      <TableCell>
                        <Badge variant={disabled ? "outline" : "success"}>
                          {disabled ? "DISABLED" : "ACTIVE"}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex flex-wrap justify-end gap-2">
                          <Button
                            size="sm"
                            variant="outline"
                            className="gap-1"
                            onClick={() => setPayTarget(s)}
                          >
                            <Wallet className="h-3.5 w-3.5" /> Pay
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            className="gap-1"
                            onClick={() => setResetTarget(s)}
                            title="Reset password"
                          >
                            <Lock className="h-3.5 w-3.5" /> Reset
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            className="gap-1"
                            onClick={() => setEditTarget(s)}
                          >
                            <Pencil className="h-3.5 w-3.5" /> Edit
                          </Button>
                          {!isSelf && (disabled ? (
                            <Button
                              size="sm"
                              variant="outline"
                              className="gap-1"
                              onClick={() => enableMut.mutate(s.id)}
                              disabled={enableMut.isPending}
                            >
                              <PlayCircle className="h-3.5 w-3.5" /> Enable
                            </Button>
                          ) : (
                            <Button
                              size="sm"
                              variant="outline"
                              className="gap-1"
                              onClick={() => disableMut.mutate(s.id)}
                              disabled={disableMut.isPending}
                            >
                              <Pause className="h-3.5 w-3.5" /> Disable
                            </Button>
                          ))}
                          {!isSelf && (
                            <Button
                              size="sm"
                              variant="outline"
                              className="gap-1 text-destructive hover:bg-destructive/10"
                              onClick={() => confirmDelete(s)}
                              disabled={deleteMut.isPending}
                            >
                              <Trash2 className="h-3.5 w-3.5" /> Delete
                            </Button>
                          )}
                        </div>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>

      <StaffCreateDialog
        open={addOpen}
        onOpenChange={setAddOpen}
        onSubmit={(v) => createMut.mutate(v)}
        submitting={createMut.isPending}
      />

      <StaffEditDialog
        open={!!editTarget}
        onOpenChange={(v) => !v && setEditTarget(null)}
        staff={editTarget}
        onSubmit={(v) => updateMut.mutate(v)}
        submitting={updateMut.isPending}
      />

      <ResetPasswordDialog
        open={!!resetTarget}
        onOpenChange={(v) => !v && setResetTarget(null)}
        targetName={resetTarget?.fullName ?? null}
        targetUsername={resetTarget?.username ?? null}
        onSubmit={(p) => resetMut.mutate(p)}
        submitting={resetMut.isPending}
      />

      <PayStaffDialog
        open={!!payTarget}
        onOpenChange={(v) => !v && setPayTarget(null)}
        staff={payTarget}
        onSubmit={(v) => payMut.mutate(v)}
        submitting={payMut.isPending}
      />
    </div>
  );
}

interface CreateDialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  onSubmit: (v: StaffCreateInput) => void;
  submitting: boolean;
}

function StaffCreateDialog({ open, onOpenChange, onSubmit, submitting }: CreateDialogProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [phone, setPhone] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({ username, password, fullName, phone: phone || null });
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(v) => {
        if (!v) {
          setUsername("");
          setPassword("");
          setFullName("");
          setPhone("");
        }
        onOpenChange(v);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>New staff</DialogTitle>
          <DialogDescription>
            Create a login for a new farm hand. They'll be able to log in to the admin app on web or
            phone.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="s-username">Username</Label>
            <Input
              id="s-username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              minLength={3}
              maxLength={80}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="s-password">Password</Label>
            <Input
              id="s-password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={6}
              maxLength={100}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="s-full">Full name</Label>
            <Input
              id="s-full"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
              maxLength={120}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="s-phone">Phone (optional)</Label>
            <Input
              id="s-phone"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              maxLength={20}
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Adding…" : "Add staff"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

interface EditDialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  staff: StaffUser | null;
  onSubmit: (v: StaffUpdateInput) => void;
  submitting: boolean;
}

function StaffEditDialog({ open, onOpenChange, staff, onSubmit, submitting }: EditDialogProps) {
  const [fullName, setFullName] = useState("");
  const [phone, setPhone] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({ fullName, phone: phone || null });
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(v) => {
        if (v && staff) {
          setFullName(staff.fullName);
          setPhone(staff.phone ?? "");
        }
        onOpenChange(v);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit staff</DialogTitle>
          {staff && (
            <DialogDescription>
              Updating <code>{staff.username}</code>. Username is fixed.
            </DialogDescription>
          )}
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="e-full">Full name</Label>
            <Input
              id="e-full"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
              maxLength={120}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="e-phone">Phone</Label>
            <Input
              id="e-phone"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              maxLength={20}
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Saving…" : "Save"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

interface PayDialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  staff: StaffUser | null;
  onSubmit: (v: PayStaffInput) => void;
  submitting: boolean;
}

function PayStaffDialog({ open, onOpenChange, staff, onSubmit, submitting }: PayDialogProps) {
  const today = new Date().toISOString().slice(0, 10);
  const [amount, setAmount] = useState("");
  const [date, setDate] = useState(today);
  const [notes, setNotes] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const value = Number(amount);
    if (!Number.isFinite(value) || value <= 0) {
      toast.error("Enter a positive amount");
      return;
    }
    onSubmit({ amount: value, expenseDate: date, notes: notes || null });
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(v) => {
        if (!v) {
          setAmount("");
          setDate(today);
          setNotes("");
        }
        onOpenChange(v);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Record payment</DialogTitle>
          {staff && (
            <DialogDescription>
              Paying <strong>{staff.fullName}</strong>. This is recorded as a LABOR expense.
            </DialogDescription>
          )}
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="p-amount">Amount (₹)</Label>
            <Input
              id="p-amount"
              type="number"
              step="0.01"
              min="0"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
              autoFocus
            />
            {amount && Number(amount) > 0 && (
              <p className="text-xs text-muted-foreground">{formatCurrency(amount)}</p>
            )}
          </div>
          <div className="space-y-2">
            <Label htmlFor="p-date">Date</Label>
            <Input
              id="p-date"
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="p-notes">Notes (optional)</Label>
            <Input
              id="p-notes"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              maxLength={400}
              placeholder="e.g. August salary"
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Recording…" : "Record payment"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
