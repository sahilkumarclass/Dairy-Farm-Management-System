import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, Plus, Stethoscope, Droplet } from "lucide-react";
import { Link, useParams } from "react-router-dom";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import {
  getCowDetail,
  listCowHealth,
  listCowProduction,
  recordCowHealth,
  recordCowProduction,
  type CowHealthLogInput,
  type CowMilkProductionInput,
} from "@/features/herd/api";
import { extractErrorMessage } from "@/lib/api";
import { formatCurrency, formatDate, formatLiters } from "@/lib/utils";
import type { HealthEventType, MilkSession } from "@/types/api";

export function CowDetailPage() {
  const { id = "" } = useParams<{ id: string }>();
  const qc = useQueryClient();
  const [milkOpen, setMilkOpen] = useState(false);
  const [healthOpen, setHealthOpen] = useState(false);

  const detail = useQuery({ queryKey: ["cow-detail", id], queryFn: () => getCowDetail(id), enabled: !!id });
  const production = useQuery({
    queryKey: ["cow-production", id],
    queryFn: () => listCowProduction(id, { size: 30 }),
    enabled: !!id,
  });
  const health = useQuery({
    queryKey: ["cow-health", id],
    queryFn: () => listCowHealth(id, { size: 30 }),
    enabled: !!id,
  });

  const milkMut = useMutation({
    mutationFn: recordCowProduction,
    onSuccess: () => {
      toast.success("Production logged");
      qc.invalidateQueries({ queryKey: ["cow-production", id] });
      qc.invalidateQueries({ queryKey: ["cow-detail", id] });
      qc.invalidateQueries({ queryKey: ["herd-summary"] });
      setMilkOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  const healthMut = useMutation({
    mutationFn: recordCowHealth,
    onSuccess: () => {
      toast.success("Health event logged");
      qc.invalidateQueries({ queryKey: ["cow-health", id] });
      qc.invalidateQueries({ queryKey: ["cow-detail", id] });
      qc.invalidateQueries({ queryKey: ["herd-summary"] });
      setHealthOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  if (!detail.data) {
    return <div className="text-muted-foreground">Loading cow…</div>;
  }
  const { cow, monthLiters, lifetimeLiters, monthHealthCost, monthExpenseTotal } = detail.data;

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <Link to="/herd" className="text-sm text-muted-foreground inline-flex items-center gap-1 hover:underline">
            <ArrowLeft className="h-4 w-4" /> Back to herd
          </Link>
          <h1 className="text-2xl font-bold tracking-tight">
            {cow.tagNo}{cow.name ? ` · ${cow.name}` : ""}
          </h1>
          <p className="text-sm text-muted-foreground">
            {cow.breed ?? "—"} · {cow.gender} · {cow.ageMonths != null ? `${cow.ageMonths} mo` : "age unknown"}
            {" · "}<Badge variant="outline">{cow.healthStatus.replace("_", " ")}</Badge>
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" className="gap-2" onClick={() => setHealthOpen(true)}>
            <Stethoscope className="h-4 w-4" /> Log health event
          </Button>
          <Button className="gap-2" onClick={() => setMilkOpen(true)}>
            <Droplet className="h-4 w-4" /> Log production
          </Button>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Stat title="Month yield" value={formatLiters(monthLiters)} />
        <Stat title="Lifetime yield" value={formatLiters(lifetimeLiters)} />
        <Stat title="Vet/health cost (mo)" value={formatCurrency(monthHealthCost)} />
        <Stat title="All expenses (mo)" value={formatCurrency(monthExpenseTotal)} hint="Feed, vet, equipment tagged to this cow" />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader><CardTitle>Recent production</CardTitle></CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Session</TableHead>
                  <TableHead className="text-right">Liters</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {production.data?.content.length === 0 && (
                  <TableRow><TableCell colSpan={3} className="text-center text-muted-foreground">No entries yet</TableCell></TableRow>
                )}
                {production.data?.content.map((p) => (
                  <TableRow key={p.id}>
                    <TableCell>{formatDate(p.productionDate)}</TableCell>
                    <TableCell>{p.session}</TableCell>
                    <TableCell className="text-right font-semibold">{formatLiters(p.liters)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>

        <Card>
          <CardHeader><CardTitle>Health log</CardTitle></CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Date</TableHead>
                  <TableHead>Event</TableHead>
                  <TableHead>Vet</TableHead>
                  <TableHead className="text-right">Cost</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {health.data?.content.length === 0 && (
                  <TableRow><TableCell colSpan={4} className="text-center text-muted-foreground">No events yet</TableCell></TableRow>
                )}
                {health.data?.content.map((h) => (
                  <TableRow key={h.id}>
                    <TableCell>{formatDate(h.eventDate)}</TableCell>
                    <TableCell><Badge variant="outline">{h.eventType.replace("_", " ")}</Badge></TableCell>
                    <TableCell className="text-muted-foreground">{h.vetName ?? "—"}</TableCell>
                    <TableCell className="text-right">{h.cost ? formatCurrency(h.cost) : "—"}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>

      <ProductionDialog
        open={milkOpen}
        onOpenChange={setMilkOpen}
        cowId={id}
        onSubmit={(v) => milkMut.mutate(v)}
        submitting={milkMut.isPending}
      />
      <HealthDialog
        open={healthOpen}
        onOpenChange={setHealthOpen}
        cowId={id}
        onSubmit={(v) => healthMut.mutate(v)}
        submitting={healthMut.isPending}
      />
    </div>
  );
}

function Stat({ title, value, hint }: { title: string; value: string; hint?: string }) {
  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="text-data-metric">{value}</div>
        {hint && <div className="mt-1 text-xs text-muted-foreground">{hint}</div>}
      </CardContent>
    </Card>
  );
}

function ProductionDialog({
  open, onOpenChange, cowId, onSubmit, submitting,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  cowId: string;
  onSubmit: (v: CowMilkProductionInput) => void;
  submitting: boolean;
}) {
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [session, setSession] = useState<MilkSession>("MORNING");
  const [liters, setLiters] = useState("");

  function submit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({ cowId, productionDate: date, session, liters: Number(liters), notes: null });
  }
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader><DialogTitle>Log production</DialogTitle></DialogHeader>
        <form onSubmit={submit} className="space-y-4">
          <div className="space-y-2"><Label htmlFor="d">Date</Label><Input id="d" type="date" value={date} onChange={(e) => setDate(e.target.value)} required /></div>
          <div className="space-y-2">
            <Label>Session</Label>
            <Select value={session} onValueChange={(v) => setSession(v as MilkSession)}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="MORNING">Morning</SelectItem>
                <SelectItem value="EVENING">Evening</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2"><Label htmlFor="l">Liters</Label><Input id="l" type="number" step="0.001" min="0" value={liters} onChange={(e) => setLiters(e.target.value)} required /></div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button type="submit" disabled={submitting}>{submitting ? "Saving…" : "Save"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function HealthDialog({
  open, onOpenChange, cowId, onSubmit, submitting,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  cowId: string;
  onSubmit: (v: CowHealthLogInput) => void;
  submitting: boolean;
}) {
  const [eventType, setEventType] = useState<HealthEventType>("VET_VISIT");
  const [eventDate, setEventDate] = useState(new Date().toISOString().slice(0, 10));
  const [nextDue, setNextDue] = useState("");
  const [vetName, setVetName] = useState("");
  const [cost, setCost] = useState("");
  const [notes, setNotes] = useState("");

  function submit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({
      cowId,
      eventType,
      eventDate,
      nextDueDate: nextDue || null,
      vetName: vetName || null,
      cost: cost ? Number(cost) : null,
      notes: notes || null,
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader><DialogTitle>Log health event</DialogTitle></DialogHeader>
        <form onSubmit={submit} className="space-y-3">
          <div className="space-y-2">
            <Label>Event type</Label>
            <Select value={eventType} onValueChange={(v) => setEventType(v as HealthEventType)}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="VET_VISIT">Vet visit</SelectItem>
                <SelectItem value="VACCINATION">Vaccination</SelectItem>
                <SelectItem value="TREATMENT">Treatment</SelectItem>
                <SelectItem value="CHECKUP">Checkup</SelectItem>
                <SelectItem value="OTHER">Other</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2"><Label htmlFor="ed">Event date</Label><Input id="ed" type="date" value={eventDate} onChange={(e) => setEventDate(e.target.value)} required /></div>
            <div className="space-y-2"><Label htmlFor="nd">Next due (optional)</Label><Input id="nd" type="date" value={nextDue} onChange={(e) => setNextDue(e.target.value)} /></div>
          </div>
          <div className="space-y-2"><Label htmlFor="vet">Vet name</Label><Input id="vet" value={vetName} onChange={(e) => setVetName(e.target.value)} /></div>
          <div className="space-y-2"><Label htmlFor="cost">Cost (₹)</Label><Input id="cost" type="number" step="0.01" min="0" value={cost} onChange={(e) => setCost(e.target.value)} /></div>
          <div className="space-y-2"><Label htmlFor="notes">Notes</Label><Input id="notes" value={notes} onChange={(e) => setNotes(e.target.value)} /></div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button type="submit" disabled={submitting}>{submitting ? "Saving…" : "Save"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
