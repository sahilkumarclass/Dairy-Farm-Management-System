import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, Beef } from "lucide-react";
import { Link } from "react-router-dom";
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
import { createCow, getHerdSummary, listCows, type CowInput } from "@/features/herd/api";
import { extractErrorMessage } from "@/lib/api";
import { formatCurrency, formatLiters } from "@/lib/utils";
import type { HealthStatus } from "@/types/api";

const STATUS_VARIANT: Record<HealthStatus, "success" | "warning" | "outline" | "destructive"> = {
  HEALTHY: "success",
  UNDER_TREATMENT: "warning",
  DRY: "outline",
  SOLD: "outline",
  DECEASED: "destructive",
};

export function HerdPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [statusFilter, setStatusFilter] = useState<HealthStatus | "ALL">("ALL");

  const summary = useQuery({ queryKey: ["herd-summary"], queryFn: getHerdSummary });

  const { data, isLoading } = useQuery({
    queryKey: ["cows", statusFilter],
    queryFn: () => listCows({
      status: statusFilter === "ALL" ? undefined : statusFilter,
      size: 100,
    }),
  });

  const createMut = useMutation({
    mutationFn: createCow,
    onSuccess: () => {
      toast.success("Cow added to herd");
      qc.invalidateQueries({ queryKey: ["cows"] });
      qc.invalidateQueries({ queryKey: ["herd-summary"] });
      setOpen(false);
    },
    onError: (e) => toast.error(extractErrorMessage(e)),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Herd Register</h1>
          <p className="text-sm text-muted-foreground">Track cows, health, and per-cow costs</p>
        </div>
        <Button onClick={() => setOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" /> Add Cow
        </Button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard title="Total Cows" value={String(summary.data?.totalCows ?? "—")} />
        <SummaryCard title="Healthy" value={String(summary.data?.healthyCount ?? "—")} />
        <SummaryCard title="Under Treatment" value={String(summary.data?.underTreatmentCount ?? "—")} />
        <SummaryCard title="Dry" value={String(summary.data?.dryCount ?? "—")} />
        <SummaryCard
          title="Month Production"
          value={summary.data ? formatLiters(summary.data.monthLiters) : "—"}
        />
        <SummaryCard
          title="Vet/Health Cost (mo)"
          value={summary.data ? formatCurrency(summary.data.monthHealthCost) : "—"}
        />
        <SummaryCard
          title="Per-Cow Expenses (mo)"
          value={summary.data ? formatCurrency(summary.data.monthFeedAndOtherCost) : "—"}
          hint="Expenses tagged to a cow (feed, vet, etc.)"
        />
      </div>

      <div className="w-48">
        <Label className="mb-1 block text-xs text-muted-foreground">Filter by status</Label>
        <Select value={statusFilter} onValueChange={(v) => setStatusFilter(v as HealthStatus | "ALL")}>
          <SelectTrigger><SelectValue /></SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">All</SelectItem>
            <SelectItem value="HEALTHY">Healthy</SelectItem>
            <SelectItem value="UNDER_TREATMENT">Under treatment</SelectItem>
            <SelectItem value="DRY">Dry</SelectItem>
            <SelectItem value="SOLD">Sold</SelectItem>
            <SelectItem value="DECEASED">Deceased</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Tag</TableHead>
                <TableHead>Name</TableHead>
                <TableHead>Breed</TableHead>
                <TableHead>Age</TableHead>
                <TableHead>Est. yield</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading && (
                <TableRow><TableCell colSpan={6} className="text-center text-muted-foreground">Loading…</TableCell></TableRow>
              )}
              {data?.content.length === 0 && !isLoading && (
                <TableRow>
                  <TableCell colSpan={6} className="text-center text-muted-foreground">
                    No cows yet — click "Add Cow" to register one.
                  </TableCell>
                </TableRow>
              )}
              {data?.content.map((c) => (
                <TableRow key={c.id}>
                  <TableCell className="font-medium">
                    <Link to={`/herd/${c.id}`} className="text-primary hover:underline inline-flex items-center gap-2">
                      <Beef className="h-4 w-4" /> {c.tagNo}
                    </Link>
                  </TableCell>
                  <TableCell>{c.name ?? "—"}</TableCell>
                  <TableCell>{c.breed ?? "—"}</TableCell>
                  <TableCell>{c.ageMonths != null ? `${c.ageMonths} mo` : "—"}</TableCell>
                  <TableCell>{c.dailyYieldEstimateLiters ? formatLiters(c.dailyYieldEstimateLiters) : "—"}</TableCell>
                  <TableCell>
                    <Badge variant={STATUS_VARIANT[c.healthStatus]}>{c.healthStatus.replace("_", " ")}</Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <CowDialog open={open} onOpenChange={setOpen} onSubmit={(v) => createMut.mutate(v)} submitting={createMut.isPending} />
    </div>
  );
}

function SummaryCard({ title, value, hint }: { title: string; value: string; hint?: string }) {
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

interface DialogProps {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  onSubmit: (v: CowInput) => void;
  submitting: boolean;
}

function CowDialog({ open, onOpenChange, onSubmit, submitting }: DialogProps) {
  const [tagNo, setTagNo] = useState("");
  const [name, setName] = useState("");
  const [breed, setBreed] = useState("");
  const [gender, setGender] = useState<CowInput["gender"]>("FEMALE");
  const [age, setAge] = useState("");
  const [healthStatus, setHealthStatus] = useState<CowInput["healthStatus"]>("HEALTHY");
  const [yieldLiters, setYieldLiters] = useState("");
  const [dateAcquired, setDateAcquired] = useState("");

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onSubmit({
      tagNo,
      name: name || null,
      breed: breed || null,
      gender,
      ageMonths: age ? Number(age) : null,
      healthStatus,
      dailyYieldEstimateLiters: yieldLiters ? Number(yieldLiters) : null,
      dateAcquired: dateAcquired || null,
      notes: null,
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader><DialogTitle>Register cow</DialogTitle></DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="tag">Tag #</Label>
              <Input id="tag" value={tagNo} onChange={(e) => setTagNo(e.target.value)} required />
            </div>
            <div className="space-y-2">
              <Label htmlFor="name">Name</Label>
              <Input id="name" value={name} onChange={(e) => setName(e.target.value)} />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="breed">Breed</Label>
              <Input id="breed" value={breed} onChange={(e) => setBreed(e.target.value)} placeholder="e.g. Gir, Holstein" />
            </div>
            <div className="space-y-2">
              <Label>Gender</Label>
              <Select value={gender} onValueChange={(v) => setGender(v as CowInput["gender"])}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="FEMALE">Female</SelectItem>
                  <SelectItem value="MALE">Male</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="age">Age (months)</Label>
              <Input id="age" type="number" min="0" value={age} onChange={(e) => setAge(e.target.value)} />
            </div>
            <div className="space-y-2">
              <Label>Health status</Label>
              <Select value={healthStatus} onValueChange={(v) => setHealthStatus(v as CowInput["healthStatus"])}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="HEALTHY">Healthy</SelectItem>
                  <SelectItem value="UNDER_TREATMENT">Under treatment</SelectItem>
                  <SelectItem value="DRY">Dry</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="yield">Daily yield estimate (L)</Label>
              <Input id="yield" type="number" step="0.1" min="0" value={yieldLiters}
                     onChange={(e) => setYieldLiters(e.target.value)} />
            </div>
            <div className="space-y-2">
              <Label htmlFor="date">Date acquired</Label>
              <Input id="date" type="date" value={dateAcquired} onChange={(e) => setDateAcquired(e.target.value)} />
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button type="submit" disabled={submitting}>{submitting ? "Saving…" : "Add cow"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
