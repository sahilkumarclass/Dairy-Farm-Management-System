package com.sahilkumar.dfms.feature.herd

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.AppTextField
import com.sahilkumar.dfms.core.ui.components.DateField
import com.sahilkumar.dfms.core.ui.components.EnumDropdown
import com.sahilkumar.dfms.core.ui.components.FormDialog
import com.sahilkumar.dfms.core.util.parseMoneyOrNull
import com.sahilkumar.dfms.core.util.todayIso
import com.sahilkumar.dfms.model.CowHealthLogRequest
import com.sahilkumar.dfms.model.CowMilkProductionRequest
import com.sahilkumar.dfms.model.HealthEventType
import com.sahilkumar.dfms.model.MilkSession

@Composable
fun ProductionDialog(
    cowId: String,
    onDismiss: () -> Unit,
    onSave: (CowMilkProductionRequest) -> Unit,
) {
    var date by remember { mutableStateOf(todayIso()) }
    var session by remember { mutableStateOf(MilkSession.MORNING) }
    var liters by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val l = parseMoneyOrNull(liters)
    FormDialog(
        title = stringResource(R.string.cow_add_production),
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                CowMilkProductionRequest(
                    cowId = cowId,
                    productionDate = date,
                    session = session,
                    liters = l!!,
                    notes = notes.ifBlank { null },
                ),
            )
        },
        confirmEnabled = l != null && l.signum() >= 0,
    ) {
        DateField(stringResource(R.string.common_date), date, { date = it })
        EnumDropdown(stringResource(R.string.milk_session), MilkSession.entries, session, { session = it }, display = { it.name })
        AppTextField(liters, { liters = it }, stringResource(R.string.cow_litres), keyboardType = KeyboardType.Decimal)
        AppTextField(notes, { notes = it }, stringResource(R.string.common_notes_optional))
    }
}

@Composable
fun HealthLogDialog(
    cowId: String,
    onDismiss: () -> Unit,
    onSave: (CowHealthLogRequest) -> Unit,
) {
    var eventType by remember { mutableStateOf(HealthEventType.CHECKUP) }
    var eventDate by remember { mutableStateOf(todayIso()) }
    var nextDue by remember { mutableStateOf("") }
    var vetName by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    FormDialog(
        title = stringResource(R.string.cow_add_health),
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                CowHealthLogRequest(
                    cowId = cowId,
                    eventType = eventType,
                    eventDate = eventDate,
                    nextDueDate = nextDue.ifBlank { null },
                    vetName = vetName.ifBlank { null },
                    cost = parseMoneyOrNull(cost),
                    notes = notes.ifBlank { null },
                ),
            )
        },
    ) {
        EnumDropdown(stringResource(R.string.cow_event_type), HealthEventType.entries, eventType, { eventType = it }, display = { it.name })
        DateField(stringResource(R.string.common_date), eventDate, { eventDate = it })
        DateField(stringResource(R.string.cow_next_due), nextDue, { nextDue = it })
        AppTextField(vetName, { vetName = it }, stringResource(R.string.cow_vet_name))
        AppTextField(cost, { cost = it }, stringResource(R.string.cow_cost), keyboardType = KeyboardType.Decimal)
        AppTextField(notes, { notes = it }, stringResource(R.string.common_notes_optional))
    }
}
