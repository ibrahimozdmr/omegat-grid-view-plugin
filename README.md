# OmegaT Grid View

**See your whole document as a table and edit it in place: source on the left,
translation on the right, right inside [OmegaT](https://omegat.org).**

### [⬇ Download the latest version](https://github.com/ibrahimozdmr/omegat-grid-view-plugin/releases/latest)

[Installation](#installation) · [Using the grid](#using-the-grid) ·
[Changelog](CHANGELOG.md) · [Türkçe](#türkçe)

---

OmegaT's editor shows your text as one long flow of segments. That is great for
translating, but less so for getting an overview: Which sentences are still
open? Where did I use that term? How does this paragraph read from start to
finish?

Grid View adds the two-column table that memoQ and Trados users know. It sits
next to the editor as a regular OmegaT pane, follows you as you move through
the text, and lets you fix a translation without leaving the table.

## Features

- **Source and target side by side.** Every segment of the open document in
  one table, with its number and status (translated, untranslated, active).
  Long sentences wrap onto several lines instead of being cut off.
- **Always in sync with the editor.** Click a row and the editor jumps to that
  segment. Move in the editor and the table highlights and scrolls to it.
  Switch files and the table switches with you.
- **Edit right in the table.** Double-click a translation, type, press Enter.
  The change goes through OmegaT's own editor, so translation memory,
  statistics and autosave work exactly as if you had typed it there.
- **Find what you need.** A search box looks through source, translation and
  segment numbers as you type. A filter shows all, only untranslated or only
  translated segments, and rows come and go as you translate.
- **Fits your screen.** Word wrap on or off, bigger or smaller text from the
  pane's gear menu. The table uses OmegaT's own font by default.
- **Stays out of your way.** On first start it waits as a small tab at the
  bottom of the window, next to Dictionary and Machine Translation. Drag it
  wherever you like; OmegaT remembers the spot.
- **Handles large projects.** Text is read from the project on demand, so
  projects with tens of thousands of segments stay fast.

> **Note:** the panel's labels are currently in Turkish. English names are
> given in brackets below.

## Installation

1. **Download** `omegat-gridview-plugin-<version>.jar` from the
   [latest release](https://github.com/ibrahimozdmr/omegat-grid-view-plugin/releases/latest).
2. **Close OmegaT.**
3. **Copy the JAR** into your OmegaT plugins folder. If the `plugins` folder
   does not exist yet, create it.
   - Windows: `%APPDATA%\OmegaT\plugins` (paste this into the File Explorer
     address bar)
   - Linux / macOS: `~/.omegat/plugins`

   If an older version of the plugin is in that folder, delete it.
4. **Start OmegaT.** A **Grid View** tab appears at the bottom edge of the
   window. Click it to open the panel.

Requires OmegaT 6.0 or 6.1. Don't have OmegaT yet? Get it from
[omegat.org/download](https://omegat.org/download); the packages that include
Java are the easiest choice.

## Using the grid

| What you do | What happens |
| --- | --- |
| Click a row | The OmegaT editor jumps to that segment |
| Move to another segment in the editor | The table highlights that row and scrolls to it |
| Double-click a cell in **Hedef** (Target) | You can edit the translation |
| Press **Enter** while editing | The translation is saved |
| Press **Esc** while editing | Your change is discarded |
| Type in **Ara:** (Search) | Rows are filtered by source, translation or segment number |
| Pick **Tümü / Çevrilmemiş / Çevrilmiş** | Show all / untranslated / translated segments |
| Drag the panel's title bar | Dock the panel wherever you like |

The **Durum** (Status) column shows **Çevrildi** (translated),
**Çevrilmedi** (untranslated) or **Aktif** (the segment open in the editor).

The **gear icon** in the panel's title bar has these options:

- **Yalnızca açık dosyayı göster** (show only the open file): on by default.
  Turn it off to see every file of the project in one table.
- **Satır kaydırma** (word wrap)
- **Yazı boyutunu büyüt / küçült / Varsayılan yazı boyutu**
  (bigger / smaller / default text size)

If you reset the window layout with OmegaT's **Restore Main Window** command,
the panel goes back to its tab at the bottom edge.

## Compatibility

| OmegaT | Status |
| --- | --- |
| 6.1.1 | Tested |
| 6.0.1 | Tested |
| Older versions | Not tested |

## Known limitations

- Cells are edited as plain text. In segments with many OmegaT tags, keep the
  tags intact yourself, or use the editor pane, which highlights them.
- With a filter on, when a row drops out of the list the row below it can
  look taller than it needs to for a moment. It sorts itself out the next
  time the filter, text size or word wrap changes.
- The text size set in the gear menu is reset when OmegaT restarts.

## How it works

- Edits made in the table are sent to OmegaT's editor (go to the segment,
  replace its text, save), so OmegaT handles them like any other edit.
- The table holds a reference to OmegaT's own segment list and reads text and
  status only for the rows on screen; nothing is copied.
- On first start OmegaT would place a new pane in a default spot of its own
  choosing. The plugin steps in and puts it on the bottom edge as a tab
  instead, and puts it back there after a layout reset.

## Building from source

```
./gradlew jar
```

The JAR is produced in `build/libs/`. JDK 17 or newer is needed to run the
build; the plugin itself targets Java 11, the minimum for OmegaT 6.

## Releasing

1. Set the new `version` in `build.gradle.kts`.
2. Add a `## <version>` section to `CHANGELOG.md`.
3. Commit, then push a tag named `v<version>`, for example
   `git tag v1.0.2` and `git push origin v1.0.2`.

The Release workflow builds the JAR and publishes a GitHub release with it,
using that CHANGELOG section as the release notes. It stops if the tag does
not match the version in `build.gradle.kts`.

## License

GNU General Public License v3. See [COPYING](COPYING).

---

# Türkçe

**Belgenin tamamını bir tablo olarak görün ve yerinde düzenleyin: solda
kaynak, sağda çeviri, doğrudan [OmegaT](https://omegat.org) içinde.**

### [⬇ Son sürümü indirin](https://github.com/ibrahimozdmr/omegat-grid-view-plugin/releases/latest)

OmegaT'nin editörü metni uzun bir segment akışı olarak gösterir. Çeviri yaparken
bu çok rahattır, ama genel bir bakış için o kadar değil: Hangi cümleler hâlâ
çevrilmedi? O terimi nerede kullandım? Bu paragraf baştan sona nasıl okunuyor?

Grid View, memoQ ve Trados kullanıcılarının alışık olduğu iki sütunlu tabloyu
ekler. Editörün yanında sıradan bir OmegaT paneli olarak durur, metinde
ilerledikçe sizi takip eder ve tablodan çıkmadan bir çeviriyi düzeltmenizi
sağlar.

## Özellikler

- **Kaynak ve çeviri yan yana.** Açık belgedeki her segment numarası ve
  durumuyla (çevrildi, çevrilmedi, aktif) tek bir tabloda. Uzun cümleler
  kesilmez, alt satırlara sarılır.
- **Editörle hep eşzamanlı.** Bir satıra tıklayın, editör o segmente atlar.
  Editörde ilerleyin, tablo o satırı vurgulayıp oraya kayar. Dosya
  değiştirdiğinizde tablo da değişir.
- **Doğrudan tabloda düzenleme.** Bir çeviriye çift tıklayın, yazın, Enter'a
  basın. Değişiklik OmegaT'nin kendi editöründen geçer; çeviri belleği,
  istatistikler ve otomatik kayıt, editörde yazmışsınız gibi çalışır.
- **Aradığınızı bulun.** Arama kutusu yazdıkça kaynakta, çeviride ve segment
  numaralarında arar. Filtre tümünü, yalnızca çevrilmemişleri ya da yalnızca
  çevrilmişleri gösterir; çeviri yaptıkça satırlar listeye girip çıkar.
- **Ekranınıza uyar.** Panelin dişli menüsünden satır kaydırmayı açıp
  kapatabilir, yazıyı büyütüp küçültebilirsiniz. Tablo varsayılan olarak
  OmegaT'nin yazı tipini kullanır.
- **Yolunuza çıkmaz.** İlk açılışta pencerenin alt kenarında, Sözlük ve
  Makine Çevirisi'nin yanında küçük bir sekme olarak bekler. İstediğiniz yere
  sürükleyin; OmegaT yerini hatırlar.
- **Büyük projelerde de hızlı.** Metin projeden gerektikçe okunur, on binlerce
  segmentli projeler de akıcı kalır.

## Kurulum

1. `omegat-gridview-plugin-<sürüm>.jar` dosyasını
   [son sürüm](https://github.com/ibrahimozdmr/omegat-grid-view-plugin/releases/latest)
   sayfasından **indirin**.
2. **OmegaT'yi kapatın.**
3. **JAR dosyasını** OmegaT eklenti klasörüne **kopyalayın**. `plugins`
   klasörü yoksa oluşturun.
   - Windows: `%APPDATA%\OmegaT\plugins` (Dosya Gezgini'nin adres çubuğuna
     yapıştırın)
   - Linux / macOS: `~/.omegat/plugins`

   Klasörde eklentinin eski bir sürümü varsa silin.
4. **OmegaT'yi açın.** Pencerenin alt kenarında **Grid View** sekmesi belirir.
   Paneli açmak için tıklayın.

OmegaT 6.0 veya 6.1 gerekir. OmegaT yüklü değilse
[omegat.org/download](https://omegat.org/download) adresinden indirebilirsiniz;
Java'yı içinde getiren paketler en kolay seçenektir.

## Tabloyu kullanmak

| Ne yaparsınız | Ne olur |
| --- | --- |
| Bir satıra tıklarsınız | OmegaT editörü o segmente atlar |
| Editörde başka bir segmente geçersiniz | Tablo o satırı vurgular ve oraya kayar |
| **Hedef** sütunundaki bir hücreye çift tıklarsınız | Çeviriyi düzenleyebilirsiniz |
| Düzenlerken **Enter**'a basarsınız | Çeviri kaydedilir |
| Düzenlerken **Esc**'e basarsınız | Değişiklik iptal edilir |
| **Ara:** kutusuna yazarsınız | Satırlar kaynağa, çeviriye veya segment numarasına göre süzülür |
| **Tümü / Çevrilmemiş / Çevrilmiş** seçersiniz | Tüm / çevrilmemiş / çevrilmiş segmentler gösterilir |
| Panelin başlık çubuğunu sürüklersiniz | Paneli istediğiniz yere yerleştirirsiniz |

**Durum** sütunu **Çevrildi**, **Çevrilmedi** veya **Aktif** (editörde açık
olan segment) gösterir.

Panelin başlık çubuğundaki **dişli simgesi** şu seçenekleri sunar:

- **Yalnızca açık dosyayı göster:** varsayılan olarak açıktır. Kapatırsanız
  projedeki tüm dosyalar tek tabloda görünür.
- **Satır kaydırma**
- **Yazı boyutunu büyüt / küçült / Varsayılan yazı boyutu**

Pencere düzenini OmegaT'nin **Ana Pencereyi Eski Haline Getir** komutuyla
sıfırlarsanız, panel alt kenardaki sekmesine geri döner.

## Uyumluluk

| OmegaT | Durum |
| --- | --- |
| 6.1.1 | Test edildi |
| 6.0.1 | Test edildi |
| Daha eski sürümler | Test edilmedi |

## Bilinen sınırlar

- Hücreler düz metin olarak düzenlenir. Çok sayıda OmegaT etiketi (tag)
  içeren segmentlerde etiketleri kendiniz koruyun ya da etiketleri renkli
  gösteren editör panelini kullanın.
- Filtre açıkken bir satır listeden düştüğünde, alttaki satır bir süre
  gereğinden yüksek görünebilir. Filtre, yazı boyutu veya satır kaydırma bir
  sonraki değiştiğinde düzelir.
- Dişli menüsünden ayarlanan yazı boyutu OmegaT yeniden başlatılınca sıfırlanır.

Derleme ve sürüm çıkarma adımları için yukarıdaki İngilizce
[Building from source](#building-from-source) ve [Releasing](#releasing)
bölümlerine bakın.

## Lisans

GNU Genel Kamu Lisansı v3. Bkz. [COPYING](COPYING).
