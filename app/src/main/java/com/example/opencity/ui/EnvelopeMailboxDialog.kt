package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.opencity.data.GameSaveManager
import com.example.opencity.model.Building

@Composable
fun EnvelopeMailboxDialog(
    building: Building,
    envelopes: List<GameSaveManager.HouseEnvelope>,
    onPlaceEnvelope: (title: String, message: String) -> Unit,
    onRemoveEnvelope: (envelopeId: String) -> Unit,
    onDismiss: () -> Unit
) {
    var envelopeTitle by remember { mutableStateOf("") }
    var envelopeMessage by remember { mutableStateOf("") }

    val quickPresets = remember {
        listOf(
            "✉️ رسالة للجار" to "أهلاً جارنا العزيز، أتمنى لك يوماً سعيداً!",
            "💌 دعوة زيارة" to "يسعدني زيارتك لنا اليوم مساءً في البيت.",
            "📦 إشعار طرد" to "يوجد طرد خاص بكم أمام المنزل، يرجى الاستلام.",
            "🔑 ملاحظة مهمة" to "تذكر إغلاق باب الكراج بعد ركن السيارة."
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .widthIn(max = 560.dp)
                    .testTag("envelope_mailbox_dialog"),
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MarkunreadMailbox,
                                contentDescription = "صندوق الظرف",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "صندوق الظروف أمام البيت",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "${building.arabicName} • داخل الصندوق: ${envelopes.size} ظرف",
                                    color = Color(0xFF93C5FD),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("close_mailbox_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    // Quick Preset Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPresets.forEachIndexed { idx, (presetTitle, presetBody) ->
                            OutlinedButton(
                                onClick = {
                                    envelopeTitle = presetTitle
                                    envelopeMessage = presetBody
                                },
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(40.dp)
                                    .testTag("envelope_preset_chip_$idx")
                            ) {
                                Text(
                                    text = presetTitle,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Envelope Title Input
                    OutlinedTextField(
                        value = envelopeTitle,
                        onValueChange = { envelopeTitle = it },
                        label = { Text("عنوان الظرف") },
                        placeholder = { Text("مثال: رسالة ترحيب / دعوة") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF38BDF8),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("envelope_title_input")
                    )

                    // Envelope Message Input
                    OutlinedTextField(
                        value = envelopeMessage,
                        onValueChange = { envelopeMessage = it },
                        label = { Text("محتوى الظرف") },
                        placeholder = { Text("اكتب الرسالة التي تريد وضعها داخل الظرف...") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF38BDF8),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("envelope_message_input")
                    )

                    // Primary Place Envelope Action
                    Button(
                        onClick = {
                            onPlaceEnvelope(envelopeTitle, envelopeMessage)
                            envelopeTitle = ""
                            envelopeMessage = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("place_envelope_button")
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✉️ ضع الظرف في الصندوق",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }

                    // Existing Envelopes List
                    Text(
                        text = "📬 الظروف الموضوعة أمام هذا البيت (${envelopes.size})",
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    if (envelopes.isEmpty()) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "الصندوق فارغ حالياً — اكتب رسالة أو اختر قالباً ثم اضغط لوضع الظرف.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 190.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(envelopes, key = { it.id }) { env ->
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = env.title,
                                                color = Color(0xFF38BDF8),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = env.message,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "المرسل: ${env.sender}",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemoveEnvelope(env.id) },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .testTag("remove_envelope_${env.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "إزالة الظرف",
                                                tint = Color(0xFFFCA5A5)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
