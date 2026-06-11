package com.sahilkumar.dfms.feature.milk

import androidx.compose.material3.Text
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
import com.sahilkumar.dfms.model.CustomerResponse
import com.sahilkumar.dfms.model.MilkEntryRequest
import com.sahilkumar.dfms.model.MilkEntryResponse
import com.sahilkumar.dfms.model.MilkSession
import com.sahilkumar.dfms.model.MilkType

@Composable
fun MilkFormDialog(
    existing: MilkEntryResponse?,
    customers: List<CustomerResponse>,
    onDismiss: () -> Unit,
    onSave: (MilkEntryRequest) -> Unit,
) {
    var customer by remember {
        mutableStateOf(
            existing?.let { e -> customers.firstOrNull { it.id == e.customerId } } ?: customers.firstOrNull(),
        )
    }
    var milkType by remember { mutableStateOf(existing?.milkType ?: MilkType.COW) }
    var session by remember { mutableStateOf(existing?.session ?: MilkSession.MORNING) }
    var quantity by remember { mutableStateOf(existing?.quantityLiters?.toPlainString().orEmpty()) }
    var rate by remember { mutableStateOf(existing?.ratePerLiter?.toPlainString().orEmpty()) }
    var date by remember { mutableStateOf(existing?.entryDate ?: todayIso()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }

    val qty = parseMoneyOrNull(quantity)
    val canSave = customer != null && qty != null && qty.signum() > 0

    FormDialog(
        title = stringResource(if (existing == null) R.string.milk_new else R.string.milk_edit),
        onDismiss = onDismiss,
        onConfirm = {
            val c = customer ?: return@FormDialog
            onSave(
                MilkEntryRequest(
                    customerId = c.id,
                    milkType = milkType,
                    quantityLiters = qty!!,
                    ratePerLiter = parseMoneyOrNull(rate),
                    session = session,
                    entryDate = date,
                    notes = notes.ifBlank { null },
                ),
            )
        },
        confirmEnabled = canSave,
    ) {
        if (customers.isEmpty()) {
            Text(stringResource(R.string.customers_add))
        } else {
            EnumDropdown(
                label = stringResource(R.string.milk_customer),
                options = customers,
                selected = customer ?: customers.first(),
                onSelected = { customer = it },
                display = { it.name },
            )
        }
        EnumDropdown(stringResource(R.string.milk_type), MilkType.entries, milkType, { milkType = it }, display = { it.name })
        EnumDropdown(stringResource(R.string.milk_session), MilkSession.entries, session, { session = it }, display = { it.name })
        AppTextField(quantity, { quantity = it }, stringResource(R.string.milk_quantity), keyboardType = KeyboardType.Decimal)
        AppTextField(rate, { rate = it }, stringResource(R.string.milk_rate), keyboardType = KeyboardType.Decimal)
        DateField(stringResource(R.string.common_date), date, { date = it })
        AppTextField(notes, { notes = it }, stringResource(R.string.common_notes_optional))
    }
}
