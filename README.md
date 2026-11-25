# OmegaT Plugins

A collection of small [OmegaT](https://omegat.org) plugins. Each subfolder
is an independent Gradle module producing its own JAR — install only the
ones you want.

*Türkçe açıklama aşağıdadır.*

## Plugins

- **[grid-view](grid-view/)** — dockable two-column (source | target) grid
  view synchronized with the OmegaT editor, with an editable target column,
  search/filter and word wrap.
- **[reopen-last-project](reopen-last-project/)** — automatically reopens
  the most recently used project on startup.

See each subfolder's own README for details, installation instructions and
known limitations.

## Requirements

- OmegaT 6.0 or later. Tested versions are listed in each plugin's README.

## Building

```
./gradlew jar
```

Builds every plugin; each JAR lands in `<plugin>/build/libs/`. To build a
single plugin: `./gradlew :grid-view:jar` or `./gradlew :reopen-last-project:jar`.

## License

GNU General Public License v3. See [COPYING](COPYING).

---

# OmegaT Eklentileri (Türkçe)

Küçük [OmegaT](https://omegat.org) eklentilerinden oluşan bir koleksiyon.
Her alt klasör bağımsız bir Gradle modülü olup kendi JAR'ını üretir —
yalnızca istediğinizi kurabilirsiniz.

## Eklentiler

- **[grid-view](grid-view/)** — OmegaT editörüyle senkronize çalışan,
  düzenlenebilir hedef sütunlu, arama/filtreli iki sütunlu ızgara görünümü.
- **[reopen-last-project](reopen-last-project/)** — açılışta en son
  kullanılan projeyi otomatik açar.

Kurulum talimatları ve bilinen sınırlar için her alt klasörün kendi
README dosyasına bakın.

## Gereksinimler

- OmegaT 6.0 veya üstü. Test edilen sürümler her eklentinin kendi README dosyasında yazar.

## Derleme

```
./gradlew jar
```

Tüm eklentileri derler; her JAR `<eklenti>/build/libs/` içinde oluşur.
Tek bir eklentiyi derlemek için: `./gradlew :grid-view:jar` veya
`./gradlew :reopen-last-project:jar`.

## Lisans

GNU Genel Kamu Lisansı v3. Bkz. [COPYING](COPYING).
