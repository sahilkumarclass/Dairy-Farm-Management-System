package com.sahilkumar.dfms.feature.customers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.AppTextField
import com.sahilkumar.dfms.core.ui.components.EnumDropdown
import com.sahilkumar.dfms.core.ui.components.FormDialog
import com.sahilkumar.dfms.core.util.digitsOnly
import com.sahilkumar.dfms.core.util.isValidPhone
import com.sahilkumar.dfms.core.util.parseMoneyOrNull
import com.sahilkumar.dfms.model.CustomerRequest
import com.sahilkumar.dfms.model.CustomerResponse
import com.sahilkumar.dfms.model.CustomerStatus

@Composable
fun CustomerFormDialog(
    existing: CustomerResponse?,
    onDismiss: () -> Unit,
    onSave: (CustomerRequest) -> Unit,
) {
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var phone by remember { mutableStateOf(existing?.phone.orEmpty()) }
    var address by remember { mutableStateOf(existing?.address.orEmpty()) }
    var rate by remember { mutableStateOf(existing?.customMilkRate?.toPlainString().orEmpty()) }
    var status by remember { mutableStateOf(existing?.status ?: CustomerStatus.ACTIVE) }

    val phoneValid = isValidPhone(phone)
    val canSave = name.isNotBlank() && phoneValid

    FormDialog(
        title = stringResource(if (existing == null) R.string.customer_new else R.string.customer_edit),
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                CustomerRequest(
                    name = name.trim(),
                    phone = phone,
                    address = address.ifBlank { null },
                    customMilkRate = parseMoneyOrNull(rate),
                    status = status,
                ),
            )
        },
        confirmEnabled = canSave,
    ) {
        AppTextField(name, { name = it }, stringResource(R.string.common_name))
        AppTextField(
            value = phone,
            onValueChange = { phone = digitsOnly(it) },
            label = stringResource(R.string.common_phone),
            keyboardType = KeyboardType.Phone,
            isError = phone.isNotEmpty() && !phoneValid,
            supportingText = stringResource(R.string.phone_helper_10),
        )
        AppTextField(address, { address = it }, stringResource(R.string.common_address))
        AppTextField(rate, { rate = it }, stringResource(R.string.customer_custom_rate), keyboardType = KeyboardType.Decimal)
        if (existing != null) {
            EnumDropdown(
                label = stringResource(R.string.common_status),
                options = CustomerStatus.entries,
                selected = status,
                onSelected = { status = it },
                display = { it.name },
            )
        }
    }
}

@Composable
fun CreateLoginDialog(
    customer: CustomerResponse,
    onDismiss: () -> Unit,
    onSubmit: (username: String, password: String) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val canSave = username.length >= 3 && password.length >= 6

    FormDialog(
        title = stringResource(R.string.customer_create_login),
        onDismiss = onDismiss,
        onConfirm = { onSubmit(username, password) },
        confirmEnabled = canSave,
    ) {
        AppTextField(username, { username = it }, stringResource(R.string.customer_username))
        AppTextField(password, { password = it }, stringResource(R.string.login_password), isPassword = true, keyboardType = KeyboardType.Password)
    }
}

@Composable
fun ResetPasswordDialog(
    name: String,
    onDismiss: () -> Unit,
    onSubmit: (password: String) -> Unit,
) {
    var password by remember { mutableStateOf("") }
    FormDialog(
        title = stringResource(R.string.customer_reset_password),
        onDismiss = onDismiss,
        onConfirm = { onSubmit(password) },
        confirmEnabled = password.length >= 6,
    ) {
        AppTextField(password, { password = it }, stringResource(R.string.staff_new_password), isPassword = true, keyboardType = KeyboardType.Password)
    }
}
