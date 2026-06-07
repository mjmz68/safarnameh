package com.example.data

data class BookRecommend(
    val title: String,
    val author: String,
    val translator: String,
    val genre: String,
    val summary: String,
    val rating: Float
)

data class MediaRecommend(
    val title: String,
    val creator: String,
    val type: String, // "فیلم ایرانی", "فیلم خارجی", "سریال ایرانی", "سریال خارجی"
    val genre: String,
    val year: String,
    val summary: String,
    val rating: Float
)

data class PodcastRecommend(
    val title: String,
    val host: String,
    val subject: String,
    val summary: String,
    val popularEpisode: String,
    val rating: Float
)

object RecommendationCatalog {
    val books = listOf(
        BookRecommend(
            title = "انسان در جستجوی معنا",
            author = "ویکتور فرانکل",
            translator = "دکتر نهضت صالحیان",
            genre = "روانشناسی و فلسفه",
            summary = "این کتاب روایت تجربیات تلخ دکتر فرانکل در اردوگاه‌های اجباری نازی‌ها است و توضیح می‌دهد که انسان چگونه با پیدا کردن معنایی برای زندگی، می‌تواند سخت‌ترین رنج‌ها را تحمل کند و زنده بماند.",
            rating = 4.9f
        ),
        BookRecommend(
            title = "تفکر سریع و کند",
            author = "دانیل کانمن",
            translator = "جمال هاشمی",
            genre = "روانشناسی شناختی",
            summary = "برنده جایزه نوبل اقتصاد، سیستم‌های دوگانه ذهن را تشریح می‌کند: سیستم یک که سریع، غریزی و احساسی است و سیستم دو که کندتر، منطقی‌تر و دقیق‌تر عمل می‌کند.",
            rating = 4.7f
        ),
        BookRecommend(
            title = "عشق در زمان وبا",
            author = "گابریل گارسیا مارکز",
            translator = "بهمن فرزانه",
            genre = "رمان و ادبیات داستانی",
            summary = "رمانی به سبک رئالیسم جادویی که داستانی عمیق و پرچالش از عشقی وفادارانه را به تصویر می‌کشد که بیش از نیم قرن طول می‌کشد تا دوباره به ثمر بنشیند.",
            rating = 4.8f
        ),
        BookRecommend(
            title = "بیندیشید و ثروتمند شوید",
            author = "ناپلئون هیل",
            translator = "مهدی قراچه‌داغی",
            genre = "موفقیت و خودپروری",
            summary = "کتابی کلاسیک بر اساس مصاحبه با صدها مرد بزرگ زمان خود که قوانین دوازده‌گانه ذهنی برای رسیدن به ثروت و موفقیت‌های بزرگ را نشان می‌دهد.",
            rating = 4.6f
        ),
        BookRecommend(
            title = "برادران کارامازوف",
            author = "فیودور داستایوفسکی",
            translator = "صالح حسینی",
            genre = "رمان کلاسیک",
            summary = "شاهکاری عظیم از ادبیات روسیه که در قالب داستان یک قتل خانوادگی، به عمیق‌ترین مباحث فلسفی، ایمانی، اخلاقی و شکاکیت بشری می‌پردازد.",
            rating = 5.0f
        )
    )

    val media = listOf(
        MediaRecommend(
            title = "درباره الی",
            creator = "اصغر فرهادی",
            type = "فیلم ایرانی",
            genre = "درام / معمایی",
            year = "1387",
            summary = "روایت سفر چند خانواده طبقه متوسط به شمال که با ناپدید شدن مربی مهدکودکی به نام الی، جو شاد گروه‌شان غرق در قضاوت، دروغ و فروپاشی روانی می‌شود.",
            rating = 4.9f
        ),
        MediaRecommend(
            title = "تلقین (Inception)",
            creator = "کریستوفر نولان",
            type = "فیلم خارجی",
            genre = "علمی تخیلی / هیجانی",
            year = "2010",
            summary = "دزدانی حرفه‌ای که با ورود به خواب‌های عمیق دیگران اقدام به سرقت ایده‌ها می‌کنند، این بار مامور می‌شوند تا ایده‌ای را در ذهن ناخودآگاه یک وارث جوان بکارند.",
            rating = 4.8f
        ),
        MediaRecommend(
            title = "سریال پوست شیر",
            creator = "جمشید محمودی",
            type = "سریال ایرانی",
            genre = "جنایی / معمایی",
            year = "1401",
            summary = "نعیم پس از ۱۵ سال حبس آزاد می‌شود تا دخترش را ببیند، اما فاجعه‌ای خانوادگی و هولناک اتمسفر را دگرگون می‌کند و مامور پلیسی خسته را در مسیر یک تحقیق پیچیده قرار می‌دهد.",
            rating = 4.9f
        ),
        MediaRecommend(
            title = "بریکینگ بد (Breaking Bad)",
            creator = "وینس گیلیگان",
            type = "سریال خارجی",
            genre = "جنایی / درام فوق‌العاده",
            year = "2008 - 2013",
            summary = "معلم شیمی دبیرستان که مبتلا به سرطان ریه شده، تصمیم می‌گیرد برای تامین آینده مالی خانواده‌اش به همراه یکی از دانش‌آموزان قدیمی‌اش شیشه تولید و توزیع کند.",
            rating = 5.0f
        ),
        MediaRecommend(
            title = "در میان ستارگان (Interstellar)",
            creator = "کریستوفر نولان",
            type = "فیلم خارجی",
            genre = "علمی تخیلی / حماسی",
            year = "2014",
            summary = "در حالی که زمین رو به نابودی و اتمام منابع غذایی است، گروهی از فضانوردان راهی سفری بی بازگشت از میان یک کرم‌چاله می‌شوند تا سیاره‌ای مناسب برای بقای انسان بیابند.",
            rating = 4.8f
        )
    )

    val podcasts = listOf(
        PodcastRecommend(
            title = "رادیو دیو (Radio Deev)",
            host = "تیم رادیو دیو",
            subject = "فرهنگ، هنر و موسیقی ملل",
            summary = "پادکستی که در هر اپیزود با تلفیقی از موسیقی سنتی و مدرن، ادبیات و سفرنامه‌ها، شنونده را به سفری خیال‌انگیز به یکی از شهرهای دنیا یا موضوعات فرهنگی می‌برد.",
            popularEpisode = "سفر به شیراز، درگاه آبی اصفهان",
            rating = 4.9f
        ),
        PodcastRecommend(
            title = "چنل‌بی (ChannelB)",
            host = "علی بندری",
            subject = "گزارش‌های مستند واقعی و جنایی",
            summary = "علی بندری در هر اپیزود، به فارسی ماجرای یک گزارش بلند و مستند چاپ شده در نشریات معتبر بین‌المللی را به زبان شیرین قصه تعریف می‌کند که ۱۰۰٪ واقعی هستند.",
            popularEpisode = "ماجرای راه ابریشم (Silk Road)",
            rating = 4.8f
        ),
        PodcastRecommend(
            title = "پادکست رادیو مرز (Radio Marz)",
            host = "مرضیه رسولی",
            subject = "جامعه‌شناسی و تجربیات زیسته",
            summary = "پادکستی درباره فاصله‌ها و تفاوت‌هایی که بین آدم‌ها دیوار می‌کشد. در هر اپیزود افرادی که به خاطر شغل، ظاهر، انتخاب یا شرایط زیستی با سوءتفاهم مواجهند صحبت می‌کنند.",
            popularEpisode = "مهاجرت، مرزهای دوستی",
            rating = 4.7f
        ),
        PodcastRecommend(
            title = "فردوسی‌خوانی (Ferdowsi Khani)",
            host = "امیرحسین ماهدوان",
            subject = "ادبیات کلاسیک ایران",
            summary = "شاهنامه فردوسی به زبان ساده و روان همراه با خوانش صحیح اشعار حماسی و شرح کامل داستان‌های شیرین دیوان پارسی مثل رستم و سهراب و سیاوش.",
            popularEpisode = "آغاز پادشاهی کیومرث و رزم رستم",
            rating = 4.9f
        )
    )
}
