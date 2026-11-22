package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R

/**
 * Character profile, portrait & name/motto editor:
 * - Smooth spherical/circular yellow head (zero sharp corners on face)
 * - Messy brown hair, bold dark eyebrows, expressive eyes with white highlights, confident smirk
 * - Customizable Arabic face motto (default: "طموح بلا حدود")
 * - Blue hoodie with white inner shirt, shiny gold chain, and red-and-white "R" emblem
 */
@Composable
fun NameEditDialog(
    currentName: String,
    currentMotto: String = "طموح بلا حدود",
    onSaveName: (newName: String) -> Unit,
    onSaveMotto: (newMotto: String) -> Unit = {},
    onEquipAmbitionOutfit: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var mottoInput by remember { mutableStateOf(currentMotto.ifBlank { "طموح بلا حدود" }) }
    var mainTabMode by remember { mutableIntStateOf(0) } // 0: Ambition Portrait, 1: Classic Poster, 2: Interactive 3D Model
    var selectedViewTab by remember { mutableIntStateOf(0) } // 0: Front, 1: Side, 2: Back

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 800.dp)
                .heightIn(max = 760.dp)
                .padding(horizontal = 12.dp, vertical = 14.dp)
                .testTag("name_edit_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF80B0F19),
            border = BorderStroke(1.5.dp, Color(0xFF0284C7))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with smooth circular character portrait
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_character_ambition),
                            contentDescription = "Character Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFFFFD600), CircleShape)
                        )
                        Column {
                            Text(
                                text = "شخصيتك ومواصفات الوجه والاسم",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "رأس دائري ناعم بدون زوايا حادة • شعر بني • هودي R • «طموح بلا حدود»",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name & Face Motto Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFFD600))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✍️ اسم شخصيتك وعبارة الوجه:",
                                color = Color(0xFFFFD600),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "يظهر فوق رأس الشخصية وفي المعاينة",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("اسمك في المدينة") },
                                placeholder = { Text("مثال: أشرف") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFFFFD600))
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFFFFD600),
                                    unfocusedBorderColor = Color(0xFF475569),
                                    focusedLabelColor = Color(0xFFFFD600),
                                    unfocusedLabelColor = Color(0xFF94A3B8)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("player_name_input")
                            )

                            OutlinedTextField(
                                value = mottoInput,
                                onValueChange = { mottoInput = it },
                                label = { Text("عبارة الوجه / الشعار") },
                                placeholder = { Text("طموح بلا حدود") },
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
                                    .weight(1f)
                                    .testTag("character_motto_input")
                            )
                        }

                        // Quick name presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("أشرف", "Ashraf", "بشير", "Noob", "Gamer Blox").forEach { suggestion ->
                                Surface(
                                    onClick = { nameInput = suggestion },
                                    color = if (nameInput == suggestion) Color(0xFF0284C7) else Color(0xFF0F172A),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (nameInput == suggestion) Color(0xFFFFD600) else Color(0xFF334155)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = suggestion,
                                        fontSize = 11.sp,
                                        color = if (nameInput == suggestion) Color.White else Color(0xFFCBD5E1),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Quick Arabic motto presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("طموح بلا حدود", "ملك المدينة", "سرعة بلا قيود", "نجم OpenCity").forEach { phrase ->
                                Surface(
                                    onClick = { mottoInput = phrase },
                                    color = if (mottoInput == phrase) Color(0xFF0F766E) else Color(0xFF0F172A),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (mottoInput == phrase) Color(0xFF2DD4BF) else Color(0xFF334155)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = phrase,
                                        fontSize = 10.sp,
                                        color = if (mottoInput == phrase) Color.White else Color(0xFFCBD5E1),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Live Preview of in-game overhead tag
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF0B132B),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "شكل الشارة فوق رأسك:",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )

                                Surface(
                                    color = Color(0xD90F172A),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, Color(0xFFFFD600))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFD600))
                                        )
                                        Text(
                                            text = "${nameInput.ifEmpty { "أشرف" }} • ${mottoInput.ifBlank { "طموح بلا حدود" }}",
                                            color = Color(0xFFFFD600),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Bar: Portrait / Classic Poster / Interactive 3D Specs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "✨ بورتريه «طموح بلا حدود»",
                        "🖼️ الملصق الكلاسيكي",
                        "🕹️ المجسم التفاعلي"
                    ).forEachIndexed { idx, tabLabel ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (mainTabMode == idx) Color(0xFF0284C7) else Color.Transparent)
                                .clickable { mainTabMode = idx }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabLabel,
                                color = if (mainTabMode == idx) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (mainTabMode) {
                    0 -> {
                        // Full Character Portrait ("طموح بلا حدود") + 1-click 3D Outfit Equip
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFFFD600))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(270.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF070C18))
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_character_ambition),
                                        contentDescription = "Roblox Character Portrait طموح بلا حدود",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "✨ تصميم الوجه والشخصية المعتمد (بدون زوايا حادة):",
                                            color = Color(0xFFFFD600),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "• رأس أصفر دائري أملس مع شعر بني مموج وعينين بلمعة بيضاء وحواجب واثقة وابتسامة جانبية\n• عبارة «${mottoInput.ifBlank { "طموح بلا حدود" }}» أسفل الابتسامة + هودي أزرق بقميص أبيض داخلي وسلسلة ذهبية وشعار R",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                if (onEquipAmbitionOutfit != null) {
                                    Button(
                                        onClick = { onEquipAmbitionOutfit() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("equip_ambition_hoodie_button")
                                    ) {
                                        Text(
                                            text = "👕 ارتداء هودي «طموح بلا حدود» الأزرق (R) والسلسلة الذهبية باللعبة",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Original Infographic Poster View
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(270.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_roblox_character_spec),
                                        contentDescription = "Roblox Character Specification Poster",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "✅ شخصيتك في اللعبة بوجه دائري ناعم بدون زوايا حادة:",
                                            color = Color(0xFFFFD600),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "• رأس أصفر دائري/كروي أملس تماماً مع عينين وحواجب وابتسامة واثقة\n• شعر بني ناعم وجذع أزرق وهودي R وسلسلة ذهبية وساقين خضراوين\n• نسبة الارتفاع إلى العرض: 5 وحدات طول و 4 وحدات عرض",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Interactive 3D Model Display
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF1E293B))
                                        .padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("المنظر الأمامي", "المنظر الجانبي", "المنظر الخلفي").forEachIndexed { idx, label ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (selectedViewTab == idx) Color(0xFF0284C7) else Color.Transparent)
                                                .clickable { selectedViewTab = idx }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (selectedViewTab == idx) Color.White else Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                                fontWeight = if (selectedViewTab == idx) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .size(width = 122.dp, height = 192.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        color = Color(0xFF1E293B),
                                        border = BorderStroke(1.dp, Color(0xFF334155))
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            when (selectedViewTab) {
                                                0 -> CharacterFrontView(motto = mottoInput.ifBlank { "طموح بلا حدود" })
                                                1 -> CharacterSideView()
                                                else -> CharacterBackView()
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        SpecRow(icon = "🟡", title = "الرأس:", detail = "دائري أملس تماماً بدون زوايا حادة")
                                        SpecRow(icon = "💇", title = "الشعر:", detail = "بني مموج ناعم (Messy Brown)")
                                        SpecRow(icon = "😏", title = "الوجه:", detail = "عيون بلمعة بيضاء + «${mottoInput.ifBlank { "طموح بلا حدود" }}»")
                                        SpecRow(icon = "🧥", title = "الهودي:", detail = "أزرق + قميص أبيض + شعار R")
                                        SpecRow(icon = "🪙", title = "السلسلة:", detail = "سلسلة عنق ذهبية لامعة")
                                        SpecRow(icon = "🟨", title = "الذراعان:", detail = "قطعتان متناسقتان")
                                        SpecRow(icon = "🟩", title = "الساقان:", detail = "قطعتان خضراوان")
                                        SpecRow(icon = "🚫", title = "المظهر:", detail = "بدون زوايا حادة للوجه")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm / Save Button
                Button(
                    onClick = {
                        val trimmedName = nameInput.trim()
                        if (trimmedName.isNotEmpty()) {
                            onSaveName(trimmedName)
                        }
                        val trimmedMotto = mottoInput.trim().ifEmpty { "طموح بلا حدود" }
                        onSaveMotto(trimmedMotto)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_player_name_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "اعتماد «${nameInput.ifEmpty { "أشرف" }}» وشعار «${mottoInput.ifBlank { "طموح بلا حدود" }}»",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecRow(icon: String, title: String, detail: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = icon, fontSize = 12.sp)
        Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Text(text = detail, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CharacterFrontView(motto: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Smooth circular head with brown hair & Arabic face motto (zero sharp corners)
        Box(
            modifier = Modifier
                .size(46.dp),
            contentAlignment = Alignment.Center
        ) {
            // Yellow round sphere head
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFEA55), Color(0xFFFFD600), Color(0xFFEAB308))
                        )
                    )
                    .border(1.dp, Color(0x33000000), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "◡  ◡", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1F160E), lineHeight = 8.sp)
                    Text(text = "◉‿◉", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, lineHeight = 11.sp)
                    Text(
                        text = motto,
                        fontSize = 5.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        lineHeight = 6.sp
                    )
                }
            }
            // Messy brown hair crown (rounded caps)
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(14.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 8.dp, bottomEnd = 10.dp))
                    .background(Color(0xFF4A2C18))
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        // Blue Hoodie Torso + White Collar + Gold Chain + Red R Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp, 38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF145CE0))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(30.dp, 38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF145CE0)),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // White inner shirt collar
                    Box(
                        modifier = Modifier
                            .size(14.dp, 6.dp)
                            .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                            .background(Color.White)
                    )
                    // Gold chain line
                    Box(
                        modifier = Modifier
                            .size(16.dp, 3.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFACC15))
                    )
                    // Red & white R emblem
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "R",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(14.dp, 38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF145CE0))
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row {
            Box(
                modifier = Modifier
                    .size(13.dp, 36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF10A825))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(13.dp, 36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF10A825))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "المنظر الأمامي", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CharacterSideView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFEA55), Color(0xFFFFD600), Color(0xFFEAB308))
                        )
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )
            }
            Box(
                modifier = Modifier
                    .width(34.dp)
                    .height(14.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF4A2C18))
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .size(22.dp, 38.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF145CE0)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp, 34.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1D4ED8))
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .size(18.dp, 36.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF10A825))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "المنظر الجانبي", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CharacterBackView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFEA55), Color(0xFFFFD600), Color(0xFFEAB308))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(22.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF4A2C18))
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row {
            Box(
                modifier = Modifier
                    .size(14.dp, 38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF145CE0))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(30.dp, 38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF145CE0))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(14.dp, 38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF145CE0))
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row {
            Box(
                modifier = Modifier
                    .size(13.dp, 36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF10A825))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(13.dp, 36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF10A825))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "المنظر الخلفي", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
