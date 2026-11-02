package com.walidvoyages.app

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import java.util.Calendar

private val Sky = Color(0xFF49AAE3)
private val Blue = Color(0xFF0789D4)
private val Navy = Color(0xFF083A74)
private val Lime = Color(0xFFB5D33F)
private val Ink = Color(0xFF173354)
private val Surface = Color(0xFFF5F9FD)
private val White = Color.White

class MainActivity: ComponentActivity() {
    private var selectedTab by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedTab = intent?.getIntExtra("tab", 0) ?: 0
        DuaNotifications.createChannel(this)
        DuaNotifications.applySchedule(this)
        setContent { WalidTheme { WalidApplication(selectedTab, onSelect = { selectedTab = it }) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        selectedTab = intent.getIntExtra("tab", 0)
    }
}

@Composable
private fun WalidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Blue, onPrimary = White, primaryContainer = Color(0xFFDDF4FF),
            secondary = Lime, onSecondary = Navy, background = Surface,
            surface = White, onSurface = Ink, surfaceVariant = Color(0xFFEFF7FC)
        ), content = content
    )
}

@Composable
private fun WalidApplication(tab: Int, onSelect: (Int)->Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = Surface,
            topBar = { BrandBar(onNotifications = { onSelect(4) }) },
            bottomBar = {
                NavigationBar(containerColor = White, tonalElevation = 6.dp) {
                    listOf("الرئيسية" to "⌂", "العمرة" to "🕋", "الرحلات" to "✈", "الأدعية" to "☾", "حسابي" to "☷")
                        .forEachIndexed { i, pair ->
                            NavigationBarItem(
                                selected = tab == i,
                                onClick = { onSelect(i) },
                                icon = { Text(pair.second, fontSize=21.sp, color=if(tab==i) Blue else Ink) },
                                label = { Text(pair.first, fontSize=10.sp, maxLines=1) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor=Blue, selectedTextColor=Navy, indicatorColor=Color(0xFFE0F4FC))
                            )
                        }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (tab) {
                    0 -> HomeScreen(onSelect)
                    1 -> OmraScreen()
                    2 -> TravelScreen()
                    3 -> DuaScreen()
                    else -> AccountScreen(onSelect)
                }
            }
        }
    }
}

@Composable
private fun BrandBar(onNotifications:()->Unit) {
    Column(Modifier.fillMaxWidth().background(White)) {
    Row(Modifier.fillMaxWidth().padding(horizontal=17.dp, vertical=8.dp), verticalAlignment=Alignment.CenterVertically) {
        Image(painterResource(R.drawable.walid_logo), contentDescription="شعار Walid Voyages", modifier=Modifier.width(175.dp).height(55.dp), contentScale=ContentScale.Fit)
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment=Alignment.End) {
            Text("وليد للأسفار", fontWeight=FontWeight.Bold, fontSize=12.sp, color=Navy)
            Text("السفر عليك والتفاصيل علينا", fontSize=9.sp, color=Blue)
        }
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(40.dp).background(Color(0xFFE7F6FE), CircleShape).clickable(onClick=onNotifications), contentAlignment=Alignment.Center) {
            Text("🔔",fontSize=20.sp)
        }
    }
    BrandWave(Modifier.fillMaxWidth().height(12.dp))
    }
}

@Composable
private fun BrandWave(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val top = Path().apply {
            moveTo(0f, h * .22f)
            cubicTo(w * .18f, -h * .50f, w * .22f, h * 1.55f, w * .42f, h * .66f)
            cubicTo(w * .61f, -h * .25f, w * .70f, h * .12f, w, h * .60f)
        }
        val base = Path().apply {
            moveTo(0f, h * .70f)
            cubicTo(w * .15f, h * .12f, w * .23f, h * 1.75f, w * .40f, h * 1.02f)
            cubicTo(w * .59f, h * .24f, w * .75f, h * .55f, w, h * .95f)
        }
        drawPath(top, color = Lime, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
        drawPath(base, color = Sky, style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round))
    }
}

private fun external(context:Context, url:String) {
    try {
        context.startActivity(Intent(if (url.startsWith("tel:")) Intent.ACTION_DIAL else Intent.ACTION_VIEW,Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context,"تعذّر فتح الرابط. ثبّت متصفحًا أو تطبيق اتصال.",Toast.LENGTH_LONG).show()
    } catch (_: Exception) {
        Toast.makeText(context,"تعذّر فتح الرابط حاليًا.",Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun NativeButton(label:String, onClick:()->Unit, modifier: Modifier=Modifier, secondary:Boolean=false) {
    Button(
        onClick=onClick, modifier=modifier.height(49.dp),
        colors=ButtonDefaults.buttonColors(containerColor=if(secondary) Lime else Blue, contentColor=if(secondary) Navy else White),
        shape=RoundedCornerShape(17.dp), contentPadding=PaddingValues(horizontal=16.dp)
    ) { Text(label,fontWeight=FontWeight.Bold,fontSize=14.sp,maxLines=1) }
}

@Composable
private fun Heading(title:String, subtitle:String?=null) {
    Column(Modifier.fillMaxWidth().padding(top=6.dp, bottom=13.dp)) {
        Text(title,fontSize=21.sp, fontWeight=FontWeight.ExtraBold, color=Navy)
        if (subtitle!=null) Text(subtitle,fontSize=12.sp,color=Color(0xFF71839A),lineHeight=20.sp)
    }
}

@Composable
private fun FeatureCard(icon:String,title:String,subtitle:String,onClick:()->Unit,modifier:Modifier=Modifier) {
    ElevatedCard(onClick=onClick,modifier=modifier,shape=RoundedCornerShape(21.dp),colors=CardDefaults.elevatedCardColors(containerColor=White),elevation=CardDefaults.elevatedCardElevation(defaultElevation=2.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(45.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFEAF7FC)),contentAlignment=Alignment.Center) { Text(icon,fontSize=25.sp) }
            Text(title,fontSize=16.sp,fontWeight=FontWeight.Bold,color=Navy,maxLines=1)
            Text(subtitle,fontSize=11.sp,color=Color(0xFF637A91),lineHeight=17.sp,minLines=2,maxLines=2)
            Text("اكتشف ←",fontSize=11.sp,color=Blue,fontWeight=FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HeroSection(onBook:()->Unit) {
    Box(Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(25.dp))) {
        Image(painterResource(R.drawable.omra_hero),contentDescription="منظر الكعبة المشرفة والعمرة",modifier=Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Navy.copy(alpha=.97f),Navy.copy(alpha=.81f),Navy.copy(alpha=.13f)))))
        Column(Modifier.align(Alignment.CenterStart).fillMaxWidth(.72f).padding(19.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text("WALID VOYAGES",color=Lime,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.sp)
            Text("رحلتك تبدأ\nمن هنا ✨",color=White,fontWeight=FontWeight.ExtraBold,fontSize=30.sp,lineHeight=36.sp)
            Text("عمرة • فنادق • تذاكر طيران\nرحلات منظّمة داخل تونس وخارجها",color=White,fontSize=12.sp,lineHeight=20.sp)
            NativeButton("اكتشف رحلات العمرة",onBook,secondary=true)
        }
        BrandWave(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(24.dp))
    }
}

@Composable
private fun HomeScreen(onSelect:(Int)->Unit) {
    val ctx=LocalContext.current
    val todays=AppContent.duas[Calendar.getInstance().get(Calendar.DAY_OF_YEAR)%AppContent.duas.size]
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { HeroSection { onSelect(1) } }
        item { Heading("خدماتنا", "اختار الخدمة، وكمل الحجز من موقع Walid Voyages الرسمي") }
        item {
            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                FeatureCard("🕋","العمرة","برامج العمرة ومناسكها",{onSelect(1)},Modifier.weight(1f))
                FeatureCard("✈️","تذاكر الطيران","البحث والحجز عبر الموقع",{external(ctx,BookingLinks.FLIGHTS)},Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                FeatureCard("🏨","الفنادق","فنادق في تونس وخارجها",{external(ctx,BookingLinks.HOTELS)},Modifier.weight(1f))
                FeatureCard("🌍","رحلات منظّمة","اكتشف رحلاتنا القادمة",{onSelect(2)},Modifier.weight(1f))
            }
        }
        item {
            ElevatedCard(onClick={onSelect(3)},modifier=Modifier.fillMaxWidth(),colors=CardDefaults.elevatedCardColors(containerColor=Color(0xFFE9F8FF)),shape=RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(19.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Text("☾",fontSize=28.sp,color=Navy)
                        Spacer(Modifier.width(8.dp))
                        Text("دعاء اليوم",fontSize=20.sp,fontWeight=FontWeight.Bold,color=Navy)
                        Spacer(Modifier.weight(1f))
                        Text("أذكار وتذكير يومي",fontSize=10.sp,color=Blue)
                    }
                    Text(todays.text,color=Ink,fontSize=17.sp,fontWeight=FontWeight.Medium,lineHeight=31.sp)
                    Text(todays.source,fontSize=11.sp,color=Blue)
                    Text("عرض كل الأدعية وتفعيل التنبيهات ←",fontSize=12.sp,fontWeight=FontWeight.Bold,color=Navy)
                }
            }
        }
        item { BookingStrip(onBook={external(ctx,BookingLinks.BASE_URL)},onCall={external(ctx,BookingLinks.PHONE_MAIN)}) }
        item { Text("Walid Voyages  •  السفر عليك والتفاصيل علينا",modifier=Modifier.fillMaxWidth().padding(8.dp),textAlign=TextAlign.Center,fontSize=11.sp,color=Color.Gray) }
    }
}

@Composable
private fun BookingStrip(onBook:()->Unit,onCall:()->Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.horizontalGradient(listOf(Navy,Blue))).padding(18.dp),verticalArrangement=Arrangement.spacedBy(11.dp)) {
        Text("جاهز لرحلتك الجاية؟",fontSize=21.sp,color=White,fontWeight=FontWeight.Bold)
        Text("فريق وليد للأسفار يعاونك في الحجز والاستفسار",fontSize=12.sp,color=White)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            NativeButton("زيارة الموقع",onBook,Modifier.weight(1f),secondary=true)
            OutlinedButton(onClick=onCall,modifier=Modifier.weight(1f).height(49.dp),colors=ButtonDefaults.outlinedButtonColors(contentColor=White),shape=RoundedCornerShape(16.dp)) { Text("اتصل بينا",fontWeight=FontWeight.Bold) }
        }
    }
}

@Composable
private fun ServiceLine(icon:String,title:String,details:String,action:()->Unit) {
    ElevatedCard(onClick=action,modifier=Modifier.fillMaxWidth(),colors=CardDefaults.elevatedCardColors(containerColor=White),shape=RoundedCornerShape(17.dp)) {
        Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(47.dp).background(Color(0xFFE8F7FF),RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center) { Text(icon,fontSize=24.sp) }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title,color=Navy,fontWeight=FontWeight.Bold,fontSize=16.sp)
                Text(details,color=Color(0xFF667B8F),fontSize=12.sp,lineHeight=20.sp)
            }
            Text("‹",fontSize=29.sp,color=Blue)
        }
    }
}

@Composable
private fun OmraScreen() {
    val ctx=LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
        Spacer(Modifier.height(3.dp))
        Heading("رحلات العمرة 🕋","رحلة روحانية من أول خطوة حتى العودة")
        Box(Modifier.fillMaxWidth().height(205.dp).clip(RoundedCornerShape(22.dp))) {
            Image(painterResource(R.drawable.omra_hero),null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,Navy.copy(alpha=.9f)))))
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text("العمرة مع وليد للأسفار",color=White,fontWeight=FontWeight.ExtraBold,fontSize=21.sp)
                Text("فنادق • مرافقة • نقل • تنظيم",color=White,fontSize=12.sp)
            }
        }
        NativeButton("اطّلع على برامج العمرة الحالية",{external(ctx,BookingLinks.OMRA)},Modifier.fillMaxWidth())
        Text("الأسعار، تواريخ السفر وتوفّر المقاعد تتأكد عند الحجز عبر الوكالة.",fontSize=11.sp,color=Color(0xFF73859A))
        Heading("دليل مناسك العمرة","ملخّص تعريفي مبسّط بالترتيب")
        AppContent.steps.forEachIndexed { i,pair ->
            ElevatedCard(modifier=Modifier.fillMaxWidth(),colors=CardDefaults.elevatedCardColors(containerColor=White),shape=RoundedCornerShape(17.dp)) {
                Row(Modifier.padding(15.dp),verticalAlignment=Alignment.Top) {
                    Box(Modifier.size(38.dp).clip(CircleShape).background(if(i%2==0) Blue else Lime),contentAlignment=Alignment.Center) {
                        Text("${i+1}",fontWeight=FontWeight.Bold,color=if(i%2==0) White else Navy)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                        Text(pair.first,fontSize=16.sp,fontWeight=FontWeight.Bold,color=Navy)
                        Text(pair.second,fontSize=13.sp,lineHeight=24.sp,color=Ink)
                    }
                }
            }
        }
        BookingStrip(onBook={external(ctx,BookingLinks.OMRA)},onCall={external(ctx,BookingLinks.PHONE_MAIN)})
        Spacer(Modifier.height(15.dp))
    }
}

@Composable
private fun TravelScreen() {
    val ctx=LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
        Heading("الرحلات والخدمات ✈️","اكتشف عروض السفر والحجز عبر موقعنا الرسمي")
        ServiceLine("🚌","الرحلات المنظّمة","برامج جماعية وحجوزات حسب العروض المتوفرة") { external(ctx,BookingLinks.ORGANIZED) }
        ServiceLine("🌏","سفر إلى الخارج","وجهات دولية ورحلات سياحية") { external(ctx,BookingLinks.INTERNATIONAL) }
        ServiceLine("🏝️","فنادق تونس","سوسة، الحمامات، جربة، المهدية وأكثر") { external(ctx,BookingLinks.HOTELS) }
        ServiceLine("🛫","تذاكر الطيران","ابحث عن الرحلة المناسبة على الموقع") { external(ctx,BookingLinks.FLIGHTS) }
        ServiceLine("🕋","حجز رحلات العمرة","اطّلع على العروض الحالية واطلب برنامجك") { external(ctx,BookingLinks.OMRA) }
        ServiceLine("📄","الفيزا والاستفسارات","تواصل مع فريق الوكالة لخدمات التأشيرات") { external(ctx,BookingLinks.WHATSAPP) }
        BookingStrip(onBook={external(ctx,BookingLinks.BASE_URL)},onCall={external(ctx,BookingLinks.PHONE_OFFICE)})
        Spacer(Modifier.height(15.dp))
    }
}

@Composable
private fun DuaScreen() {
    val ctx=LocalContext.current
    val prefs=remember { ctx.getSharedPreferences("walid_dua_favorites",Context.MODE_PRIVATE) }
    val favs=remember { mutableStateListOf<Int>().apply { addAll(prefs.getStringSet("ids", emptySet())!!.mapNotNull { it.toIntOrNull() }) } }
    var filter by remember { mutableStateOf("الكل") }
    val filtered=AppContent.duas.filter { when(filter) { "المفضلة"->it.id in favs; "الكل"->true; else->it.category==filter } }
    Column(Modifier.fillMaxSize().padding(horizontal=16.dp)) {
        Heading("الأدعية والأذكار ☾","اقرأ، انسخ، واحفظ الأدعية المفضلة لديك")
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            listOf("الكل","يومي","السفر","العمرة","المفضلة").forEach { item ->
                FilterChip(
                    selected=filter==item,onClick={filter=item},
                    label={Text(item,fontSize=11.sp,maxLines=1)},
                    colors=FilterChipDefaults.filterChipColors(selectedContainerColor=Lime,selectedLabelColor=Navy),
                    modifier=Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        if(filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) { Text("ما عندكش أدعية محفوظة في هذه القائمة.",color=Color.Gray) }
        } else {
            LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp),contentPadding=PaddingValues(bottom=22.dp)) {
                items(filtered,key={it.id}) { item ->
                    ElevatedCard(modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp),colors=CardDefaults.elevatedCardColors(containerColor=White)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
                            Row(verticalAlignment=Alignment.CenterVertically) {
                                Box(Modifier.background(Color(0xFFEAF7FF),RoundedCornerShape(20.dp)).padding(horizontal=12.dp,vertical=5.dp)) {
                                    Text(item.category,fontSize=11.sp,color=Blue,fontWeight=FontWeight.SemiBold)
                                }
                                Spacer(Modifier.weight(1f))
                                Text(item.title,color=Navy,fontSize=17.sp,fontWeight=FontWeight.Bold)
                            }
                            Text(item.text,fontSize=18.sp,color=Ink,lineHeight=34.sp,textAlign=TextAlign.Start)
                            HorizontalDivider(color=Color(0xFFECF2F7))
                            Text(item.source,color=Color(0xFF6D829A),fontSize=11.sp)
                            Row(verticalAlignment=Alignment.CenterVertically) {
                                TextButton(onClick={
                                    val manager=ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    manager.setPrimaryClip(ClipData.newPlainText(item.title,item.text))
                                    Toast.makeText(ctx,"تم نسخ الدعاء",Toast.LENGTH_SHORT).show()
                                }) { Text("نسخ 📋",color=Blue,fontSize=12.sp) }
                                TextButton(onClick={
                                    val share=Intent(Intent.ACTION_SEND).apply { type="text/plain"; putExtra(Intent.EXTRA_TEXT,"${item.title}\n\n${item.text}\n\n${item.source}") }
                                    ctx.startActivity(Intent.createChooser(share,"مشاركة الدعاء"))
                                }) { Text("مشاركة ↗",color=Blue,fontSize=12.sp) }
                                Spacer(Modifier.weight(1f))
                                TextButton(onClick={
                                    if(item.id in favs) favs.remove(item.id) else favs.add(item.id)
                                    prefs.edit().putStringSet("ids",favs.map { it.toString() }.toSet()).apply()
                                }) { Text(if(item.id in favs) "♥ محفوظ" else "♡ حفظ",color=if(item.id in favs) Color(0xFFC65B6E) else Blue,fontSize=12.sp) }
                            }
                        }
                    }
                }
                item { Text(AppContent.NOTICE,color=Color.Gray,fontSize=11.sp,modifier=Modifier.padding(8.dp)) }
            }
        }
    }
}

@Composable
private fun AccountScreen(onSelect:(Int)->Unit) {
    val ctx=LocalContext.current
    val prefs=remember { ctx.getSharedPreferences(DuaNotifications.PREFS,Context.MODE_PRIVATE) }
    var morning by remember { mutableStateOf(prefs.getBoolean(DuaNotifications.MORNING,false)) }
    var evening by remember { mutableStateOf(prefs.getBoolean(DuaNotifications.EVENING,false)) }
    var permitted by remember { mutableStateOf(DuaNotifications.permitted(ctx)) }
    var pendingChoice by remember { mutableStateOf("") }
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permitted=granted
        if (granted) {
            if(pendingChoice==DuaNotifications.MORNING) morning=true
            if(pendingChoice==DuaNotifications.EVENING) evening=true
            if(pendingChoice=="test") DuaNotifications.sendTest(ctx)
            if(pendingChoice!= "test") {
                prefs.edit().putBoolean(DuaNotifications.MORNING,morning).putBoolean(DuaNotifications.EVENING,evening).apply()
                DuaNotifications.applySchedule(ctx)
            }
        } else Toast.makeText(ctx,"لازم توافق على إذن الإشعارات من إعدادات Android.",Toast.LENGTH_LONG).show()
        pendingChoice=""
    }
    fun toggle(key:String,value:Boolean) {
        if(value && !DuaNotifications.permitted(ctx)) {
            pendingChoice=key
            if(Build.VERSION.SDK_INT>=33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        if(key==DuaNotifications.MORNING) morning=value else evening=value
        prefs.edit().putBoolean(key,value).apply()
        DuaNotifications.applySchedule(ctx)
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
        Heading("الإعدادات والتواصل ⚙️","أهم إعدادات التطبيق والإشعارات")
        ElevatedCard(colors=CardDefaults.elevatedCardColors(containerColor=White),shape=RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(17.dp),verticalArrangement=Arrangement.spacedBy(13.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text("🔔",fontSize=29.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("إشعارات الأدعية",fontSize=19.sp,fontWeight=FontWeight.ExtraBold,color=Navy)
                        Text("تذكيرات محلية، حتى بدون إنترنت",fontSize=11.sp,color=Color.Gray)
                    }
                }
                HorizontalDivider(color=Color(0xFFE8F1F8))
                ReminderSwitch("دعاء الصباح","حوالي 9:00 صباحًا",morning) { toggle(DuaNotifications.MORNING,it) }
                ReminderSwitch("ذكر المساء","حوالي 8:00 مساءً",evening) { toggle(DuaNotifications.EVENING,it) }
                Text("توقيت التنبيه تقريبي لأن Android يمكن أن يؤخّر العمل في الخلفية لتوفير البطارية. يلزم السماح بالإشعارات في إعدادات التليفون.",fontSize=11.sp,lineHeight=19.sp,color=Color(0xFF65798C))
                NativeButton("🔔 جرّب إشعارًا الآن",{
                    if(!DuaNotifications.permitted(ctx) && Build.VERSION.SDK_INT>=33) {
                        pendingChoice="test"; launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else DuaNotifications.sendTest(ctx)
                },Modifier.fillMaxWidth(),secondary=true)
                if(!permitted) Text("الإشعارات غير مسموح بها حاليًا على هذا الجهاز.",color=Color(0xFFA05048),fontSize=12.sp)
            }
        }
        Heading("تواصل معانا")
        ServiceLine("📞","58 150 699","الهاتف الرئيسي") {external(ctx,BookingLinks.PHONE_MAIN)}
        ServiceLine("☎️","73 201 660","وكالة سوسة") {external(ctx,BookingLinks.PHONE_OFFICE)}
        ServiceLine("💬","واتساب Walid Voyages","استفسارات العمرة والرحلات") {external(ctx,BookingLinks.WHATSAPP)}
        ServiceLine("🌐","الموقع الرسمي","حجوزات الفنادق والطيران والعمرة") {external(ctx,BookingLinks.BASE_URL)}
        ServiceLine("📍","فروع الوكالة","سوسة • تونس • صفاقس • القصرين • المهدية • المكنين • سليمان • السواسي") {external(ctx,BookingLinks.BASE_URL)}
        Heading("عن التطبيق")
        Text("Walid Voyages Android 1.0.0 — تطبيق Android أصلي. لا يحتاج حسابًا لقراءة الأدعية، ولا يجمع بياناتها. الحجز يتمّ في موقع الوكالة خارج التطبيق. تذكيرات الأدعية اختيارية ومحلية.",fontSize=12.sp,lineHeight=21.sp,color=Ink)
        Text("لن تصلك إشعارات عروض جديدة عن بُعد قبل إضافة خدمة Push مستقلة وربطها بنظام إدارة العروض.",fontSize=11.sp,lineHeight=18.sp,color=Color(0xFF72859A))
        Spacer(Modifier.height(15.dp))
    }
}

@Composable
private fun ReminderSwitch(title:String,subtitle:String,checked:Boolean,onChange:(Boolean)->Unit) {
    Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(title,fontSize=16.sp,color=Navy,fontWeight=FontWeight.Bold)
            Text(subtitle,fontSize=11.sp,color=Color(0xFF73879A))
        }
        Switch(checked=checked,onCheckedChange=onChange,colors=SwitchDefaults.colors(checkedTrackColor=Blue,checkedThumbColor=White))
    }
}
