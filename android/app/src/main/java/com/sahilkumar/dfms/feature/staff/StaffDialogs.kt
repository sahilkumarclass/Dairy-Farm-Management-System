package com.sahilkumar.dfms.feature.staff

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.core.ui.components.AppTextField
import com.sahilkumar.dfms.core.ui.components.FormDialog
import com.sahilkumar.dfms.core.util.digitsOnly
import com.sahilkumar.dfms.core.util.isValidPhone
import com.sahilkumar.dfms.model.StaffCreateRequest
import com.sahilkumar.dfms.model.StaffUpdateRequest
import com.sahilkumar.dfms.model.UserResponse

@Composable
fun StaffFormDialog(
    existing: UserResponse?,
    onDismiss: () -> Unit,
    onCreate: (StaffCreateRequest) -> Unit,
    onUpdate: (StaffUpdateRequest) -> Unit,
) {
    val isEdit = existing != null
    var username by remember { mutableStateOf(existing?.username.orEmpty()) }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf(existing?.fullName.orEmpty()) }
    var phone by remember { mutableStateOf(existing?.phone.orEmpty()) }

    val phoneOk = phone.isEmpty() || isValidPhone(phone)
    val canSave = fullName.isNotBlank() && phoneOk &&
        (isEdit || (username.length >= 3 && password.length >= 6))

    FormDialog(
        title = stringResource(if (isEdit) R.string.staff_edit else R.string.staff_new),
        onDismiss = onDismiss,
        onConfirm = {
            if (isEdit) {
                onUpdate(StaffUpdateRequest(fullName = fullName.trim(), phone = phone.ifBlank { null }))
            } else {
                onCreate(
                    StaffCreateRequest(
                        username = username.trim(),
                        password = password,
                        fullName = fullName.trim(),
                        phone = phone.ifBlank { null },
                    ),
                )
            }
        },
        confirmEnabled = canSave,
    ) {
        if (!isEdit) {
            AppTextField(username, { username = it }, stringResource(R.string.staff_username))
            AppTextField(password, { password = it }, stringResource(R.string.login_password), isPassword = true, keyboardType = KeyboardType.Password)
        }
        AppTextField(fullName, { fullName = it }, stringResource(R.string.staff_full_name))
        AppTextField(
            value = phone,
            onValueChange = { phone = digitsOnly(it) },
            label = stringResource(R.string.staff_phone_optional),
            keyboardType = KeyboardType.Phone,
            isError = phone.isNotEmpty() && !phoneOk,
            supportingText = stringResource(R.string.phone_helper_10),
        )
    }
}

@Composable
fun StaffResetDialog(
    name: String,
    onDismiss: () -> Unit,
    onSubmit: (password: String) -> Unit,
) {
    var password by remember { mutableStateOf("") }
    FormDialog(
        title = stringResource(R.string.staff_reset),
        onDismiss = onDismiss,
        onConfirm = { onSubmit(password) },
        confirmEnabled = password.length >= 6,
    ) {
        AppTextField(password, { password = it }, stringResource(R.string.staff_new_password), isPassword = true, keyboardType = KeyboardType.Password)
    }
}
