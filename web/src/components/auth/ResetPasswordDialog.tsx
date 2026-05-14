import { useState } from "react";
import { Button } from "@/components/ui/button";
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

interface ResetPasswordDialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  targetName: string | null;
  targetUsername?: string | null;
  onSubmit: (password: string) => void;
  submitting: boolean;
}

export function ResetPasswordDialog({
  open,
  onOpenChange,
  targetName,
  targetUsername,
  onSubmit,
  submitting,
}: ResetPasswordDialogProps) {
  const [password, setPassword] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit(password);
    setPassword("");
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(v) => {
        if (!v) setPassword("");
        onOpenChange(v);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Reset password</DialogTitle>
          {targetName && (
            <DialogDescription>
              Set a new password for <strong>{targetName}</strong>
              {targetUsername ? <> (<code>{targetUsername}</code>)</> : null}. Share it with them —
              they'll use it on their next login.
            </DialogDescription>
          )}
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="rp">New password</Label>
            <Input
              id="rp"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              minLength={6}
              required
              autoFocus
            />
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? "Resetting…" : "Reset password"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
