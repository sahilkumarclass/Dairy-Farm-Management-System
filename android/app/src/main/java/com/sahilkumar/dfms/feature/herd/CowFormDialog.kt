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
import com.sahilkumar.dfms.model.CowRequest
import com.sahilkumar.dfms.model.CowResponse
import com.sahilkumar.dfms.model.Gender
import com.sahilkumar.dfms.model.HealthStatus

@Composable
fun CowFormDialog(
    existing: CowResponse?,
    onDismiss: () -> Unit,
    onSave: (CowRequest) -> Unit,
) {
    var tagNo by remember { mutableStateOf(existing?.tagNo.orEmpty()) }
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var breed by remember { mutableStateOf(existing?.breed.orEmpty()) }
    var gender by remember { mutableStateOf(existing?.gender ?: Gender.FEMALE) }
    var age by remember { mutableStateOf(existing?.ageMonths?.toString().orEmpty()) }
    var health by remember { mutableStateOf(existing?.healthStatus ?: HealthStatus.HEALTHY) }
    var yield by remember { mutableStateOf(existing?.dailyYieldEstimateLiters?.toPlainString().orEmpty()) }
    var dateAcquired by remember { mutableStateOf(existing?.dateAcquired.orEmpty()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }

    FormDialog(
        title = stringResource(if (existing == null) R.string.cow_new else R.string.cow_edit),
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                CowRequest(
                    tagNo = tagNo.trim(),
                    name = name.ifBlank { null },
                    breed = breed.ifBlank { null },
                    gender = gender,
                    ageMonths = age.toIntOrNull(),
                    healthStatus = health,
                    dailyYieldEstimateLiters = parseMoneyOrNull(yield),
                    dateAcquired = dateAcquired.ifBlank { null },
                    notes = notes.ifBlank { null },
                ),
            )
        },
        confirmEnabled = tagNo.isNotBlank(),
    ) {
        AppTextField(tagNo, { tagNo = it }, stringResource(R.string.cow_tag))
        AppTextField(name, { name = it }, stringResource(R.string.common_name))
        AppTextField(breed, { breed = it }, stringResource(R.string.cow_breed))
        EnumDropdown(stringResource(R.string.cow_gender), Gender.entries, gender, { gender = it }, display = { it.name })
        AppTextField(age, { age = it.filter(Char::isDigit).take(3) }, stringResource(R.string.cow_age_months), keyboardType = KeyboardType.Number)
        EnumDropdown(stringResource(R.string.cow_health), HealthStatus.entries, health, { health = it }, display = { it.name })
        AppTextField(yield, { yield = it }, stringResource(R.string.cow_yield), keyboardType = KeyboardType.Decimal)
        DateField(stringResource(R.string.cow_date_acquired), dateAcquired, { dateAcquired = it })
        AppTextField(notes, { notes = it }, stringResource(R.string.common_notes_optional))
    }
}
