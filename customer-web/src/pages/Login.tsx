import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { Sprout } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuthStore } from "@/stores/auth";
import { login } from "@/features/auth/api";
import { extractErrorMessage } from "@/lib/api";

export function LoginPage() {
  const navigate = useNavigate();
  const setSession = useAuthStore((s) => s.setSession);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);
    try {
      const auth = await login(username, password);
      if (!auth.accessToken) throw new Error("No token returned");
      if (auth.role !== "CUSTOMER") {
        toast.error("This portal is only for customers. Please use the admin app at http://localhost:5173.");
        return;
      }
      setSession(auth.accessToken, {
        userId: auth.userId,
        username: auth.username,
        fullName: auth.fullName,
        role: auth.role,
      });
      toast.success(`Welcome, ${auth.fullName}`);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      toast.error(extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-muted p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-3 text-center">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-secondary/15 text-secondary">
            <Sprout className="h-6 w-6" />
          </div>
          <CardTitle className="text-2xl">DairySmart</CardTitle>
          <CardDescription>Sign in to view your milk history and bills</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="username"
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
            </div>
            <Button type="submit" className="w-full" disabled={submitting}>
              {submitting ? "Signing in…" : "Sign in"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
