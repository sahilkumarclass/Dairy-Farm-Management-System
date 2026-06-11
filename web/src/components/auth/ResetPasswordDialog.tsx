import { useState } from "react";
import { useTranslation } from "react-i18next";
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
  const { t } = useTranslation();
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
          <DialogTitle>{t("resetPassword.title")}</DialogTitle>
          {targetName && (
            <DialogDescription>
              {t("resetPassword.description", { name: targetName })}
              {targetUsername ? <> (<code>{targetUsername}</code>)</> : null}
            </DialogDescription>
          )}
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="rp">{t("resetPassword.newPassword")}</Label>
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
              {t("common.cancel")}
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? t("resetPassword.resetting") : t("resetPassword.submit")}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
