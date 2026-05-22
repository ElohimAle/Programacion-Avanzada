#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Descarga imágenes reales de productos/marcas para una maqueta o proyecto escolar.

Uso:
  python descargar_imagenes_reales.py

Qué genera:
  - carpeta imagenes_reales/
  - manifest_descargas.csv
  - manifest_descargas.json
  - imagenes_productos_marcas_reales.zip

Notas:
  - Usa búsquedas públicas de imágenes. Verifica derechos si el proyecto deja de ser escolar/prueba.
  - Si una imagen falla, el producto queda registrado como "sin_imagen" en el manifest.
"""
import csv
import json
import mimetypes
import os
import re
import sqlite3
import time
import unicodedata
import zipfile
from http.cookiejar import CookieJar
from pathlib import Path
from urllib.parse import quote, quote_plus, urlencode, urlsplit, urlunsplit
from urllib.request import HTTPCookieProcessor, Request, build_opener, urlopen

BASE = Path(__file__).resolve().parent
PRODUCTOS_PATH = BASE / "productos.json"
OUT_DIR = BASE / "imagenes_reales"
ZIP_PATH = BASE / "imagenes_productos_marcas_reales.zip"
MANIFEST_CSV = BASE / "manifest_descargas.csv"
MANIFEST_JSON = BASE / "manifest_descargas.json"
PROJECT_DIR = BASE.parents[2]
DB_PATH = PROJECT_DIR / "pdv.db"
RESOURCE_PREFIX = "/resources/Productos/imagenes_reales"
FUENTE_PRODUCTOS = "surtitienda"  # opciones: "surtitienda", "json", "db"
SURTITIENDA_API = "https://www.surtitienda.mx/api/catalog_system/pub/products/search"
SURTITIENDA_MAX_PRODUCTOS = int(os.getenv("SURTITIENDA_MAX_PRODUCTOS", "150"))
USAR_INTERNET = False  # activa busquedas extra en OpenFoodFacts/DuckDuckGo si faltan imagenes

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120 Safari/537.36",
    "Accept-Language": "es-MX,es;q=0.9,en;q=0.8",
    "Accept": "application/json,text/html,image/avif,image/webp,image/apng,image/*,*/*;q=0.8",
}

IMAGE_HEADERS = dict(HEADERS)
IMAGE_HEADERS["Accept"] = "image/png,image/jpeg,*/*;q=0.8"


def build_url(url: str, params: dict | None = None) -> str:
    if not params:
        return url
    separator = "&" if "?" in url else "?"
    return f"{url}{separator}{urlencode(params)}"


def safe_url(url: str) -> str:
    parts = urlsplit(url)
    path = quote(parts.path, safe="/:%")
    query = quote(parts.query, safe="=&%")
    return urlunsplit((parts.scheme, parts.netloc, path, query, parts.fragment))


def fetch_bytes(url: str, params: dict | None = None, timeout: int = 20, opener=None):
    req = Request(safe_url(build_url(url, params)), headers=HEADERS)
    open_func = opener.open if opener else urlopen
    with open_func(req, timeout=timeout) as response:
        return response.read(), response.headers


def fetch_json(url: str, params: dict | None = None, timeout: int = 25):
    body, _ = fetch_bytes(url, params=params, timeout=timeout)
    return json.loads(body.decode("utf-8", errors="replace"))

# Pistas de marcas comunes en México para que la búsqueda no agarre imágenes genéricas.
def marca_hint(nombre: str, categoria: str) -> str:
    n = nombre.lower()
    c = categoria.lower()
    if "leche" in n: return "Lala Alpura"
    if "yogurt" in n: return "Danone Yoplait Lala"
    if "queso" in n: return "NocheBuena Lala Fud"
    if "mantequilla" in n: return "Gloria Lala"
    if "crema" in n and "café" not in n: return "Lala Alpura"
    if "huevo" in n: return "Bachoco San Juan"
    if "pan" in n or "tortilla" in n or "tostadas" in n: return "Bimbo Tía Rosa Milpa Real Mission"
    if "arroz" in n: return "Verde Valle SOS"
    if "frijol" in n: return "Verde Valle La Sierra Isadora"
    if "azúcar" in n: return "Zulka Great Value"
    if "aceite" in n: return "Nutrioli Capullo 1-2-3"
    if "sal" in n: return "La Fina"
    if "harina de trigo" in n: return "Tres Estrellas"
    if "harina de maíz" in n: return "Maseca"
    if "pasta" in n: return "La Moderna Barilla"
    if "café" in n: return "Nescafé Legal Punta del Cielo"
    if "té" in n: return "McCormick Therbal"
    if "chocolate en polvo" in n: return "Choco Milk Nesquik"
    if "atún" in n: return "Dolores Herdez Tuny"
    if "sardinas" in n: return "La Costeña"
    if "chiles" in n or "elote" in n or "puré" in n or "piña" in n or "champiñones" in n: return "La Costeña Herdez Del Monte"
    if "mayonesa" in n: return "McCormick Hellmanns"
    if "catsup" in n: return "Clemente Jacques Heinz"
    if "mostaza" in n: return "Frenchs McCormick"
    if "salsa" in n: return "Valentina La Costeña Herdez"
    if "vinagre" in n: return "Barrilito Clemente Jacques"
    if "mole" in n: return "Doña María"
    if "cereal" in n: return "Kelloggs Nestlé"
    if "avena" in n: return "Quaker"
    if "hot cakes" in n: return "Gamesa Aunt Jemima Pronto"
    if "maple" in n: return "Aunt Jemima Great Value"
    if "mermelada" in n: return "McCormick Smucker's"
    if "cajeta" in n: return "Coronado"
    if "papas" in n: return "Sabritas Barcel"
    if "totopos" in n: return "Doritos Tostitos"
    if "chicharrón" in n: return "Barcel Sabritas"
    if "cacahuates" in n: return "Mafer Nipón"
    if "palomitas" in n: return "Act II"
    if "galletas" in n: return "Gamesa Oreo Marinela"
    if "chocolate" in n: return "Carlos V Hershey's"
    if "gomitas" in n: return "Panditas Ricolino"
    if "malvaviscos" in n: return "De la Rosa"
    if "refresco" in n: return "Coca-Cola Pepsi Fanta Sprite"
    if "agua" in n: return "Bonafont Ciel Epura"
    if "jugo" in n or "néctar" in n: return "Jumex Del Valle"
    if "energizante" in n: return "Monster Red Bull Vive100"
    if "isotónica" in n: return "Gatorade Powerade"
    if "polvo para preparar" in n: return "Tang Zuko"
    if "cerveza" in n: return "Corona Tecate Victoria Modelo"
    if "detergente" in n: return "Ariel Ace Persil"
    if "suavizante" in n: return "Downy Suavitel"
    if "cloro" in n: return "Cloralex"
    if "limpiador" in n: return "Fabuloso Pinol Windex"
    if "lavatrastes" in n: return "Axion Salvo"
    if "insecticida" in n: return "Raid Baygon"
    if "desinfectante" in n: return "Lysol"
    if "fibras" in n: return "Scotch-Brite"
    if "bolsas" in n: return "Glad Great Value"
    if "zote" in n: return "Zote"
    if "papel higiénico" in n: return "Regio Pétalo Kleenex"
    if "servilletas" in n or "toallas de papel" in n or "pañuelos" in n: return "Kleenex Regio Pétalo"
    if "jabón" in n: return "Dove Palmolive Escudo"
    if "shampoo" in n: return "Head & Shoulders Sedal"
    if "acondicionador" in n: return "Pantene Sedal"
    if "crema corporal" in n: return "Nivea Lubriderm"
    if "pasta dental" in n: return "Colgate Oral-B"
    if "cepillo dental" in n: return "Oral-B Colgate"
    if "enjuague" in n: return "Listerine"
    if "desodorante" in n: return "Axe Rexona Dove"
    if "gel fijador" in n: return "Ego Moco de Gorila"
    if "rastrillos" in n: return "Gillette Bic"
    if "toallas femeninas" in n or "protectores" in n: return "Saba Always Kotex"
    if "perro" in n: return "Pedigree Dog Chow"
    if "gato" in n: return "Whiskas Cat Chow"
    if "fórmula infantil" in n: return "NAN Enfamil"
    if "pañales" in n: return "Huggies Pampers"
    if "toallitas" in n: return "Huggies Pampers"
    if "papilla" in n: return "Gerber"
    if "pilas" in n: return "Duracell Energizer"
    if "encendedor" in n: return "BIC"
    if "cerillos" in n: return "La Central"
    if "veladoras" in n: return "Veladora México"
    if "gel antibacterial" in n: return "Escudo Blumen"
    return "marca mexicana producto"


def slugify(text: str) -> str:
    text = text.lower().strip()
    repl = str.maketrans("áéíóúüñ", "aeiouun")
    text = text.translate(repl)
    text = re.sub(r"[^a-z0-9]+", "_", text).strip("_")
    return text[:90]


FONT = {
    "A": ["01110", "10001", "10001", "11111", "10001", "10001", "10001"],
    "B": ["11110", "10001", "10001", "11110", "10001", "10001", "11110"],
    "C": ["01111", "10000", "10000", "10000", "10000", "10000", "01111"],
    "D": ["11110", "10001", "10001", "10001", "10001", "10001", "11110"],
    "E": ["11111", "10000", "10000", "11110", "10000", "10000", "11111"],
    "F": ["11111", "10000", "10000", "11110", "10000", "10000", "10000"],
    "G": ["01111", "10000", "10000", "10011", "10001", "10001", "01111"],
    "H": ["10001", "10001", "10001", "11111", "10001", "10001", "10001"],
    "I": ["11111", "00100", "00100", "00100", "00100", "00100", "11111"],
    "J": ["00111", "00010", "00010", "00010", "00010", "10010", "01100"],
    "K": ["10001", "10010", "10100", "11000", "10100", "10010", "10001"],
    "L": ["10000", "10000", "10000", "10000", "10000", "10000", "11111"],
    "M": ["10001", "11011", "10101", "10101", "10001", "10001", "10001"],
    "N": ["10001", "11001", "10101", "10011", "10001", "10001", "10001"],
    "O": ["01110", "10001", "10001", "10001", "10001", "10001", "01110"],
    "P": ["11110", "10001", "10001", "11110", "10000", "10000", "10000"],
    "Q": ["01110", "10001", "10001", "10001", "10101", "10010", "01101"],
    "R": ["11110", "10001", "10001", "11110", "10100", "10010", "10001"],
    "S": ["01111", "10000", "10000", "01110", "00001", "00001", "11110"],
    "T": ["11111", "00100", "00100", "00100", "00100", "00100", "00100"],
    "U": ["10001", "10001", "10001", "10001", "10001", "10001", "01110"],
    "V": ["10001", "10001", "10001", "10001", "10001", "01010", "00100"],
    "W": ["10001", "10001", "10001", "10101", "10101", "10101", "01010"],
    "X": ["10001", "10001", "01010", "00100", "01010", "10001", "10001"],
    "Y": ["10001", "10001", "01010", "00100", "00100", "00100", "00100"],
    "Z": ["11111", "00001", "00010", "00100", "01000", "10000", "11111"],
    "0": ["01110", "10001", "10011", "10101", "11001", "10001", "01110"],
    "1": ["00100", "01100", "00100", "00100", "00100", "00100", "01110"],
    "2": ["01110", "10001", "00001", "00010", "00100", "01000", "11111"],
    "3": ["11110", "00001", "00001", "01110", "00001", "00001", "11110"],
    "4": ["00010", "00110", "01010", "10010", "11111", "00010", "00010"],
    "5": ["11111", "10000", "10000", "11110", "00001", "00001", "11110"],
    "6": ["01110", "10000", "10000", "11110", "10001", "10001", "01110"],
    "7": ["11111", "00001", "00010", "00100", "01000", "01000", "01000"],
    "8": ["01110", "10001", "10001", "01110", "10001", "10001", "01110"],
    "9": ["01110", "10001", "10001", "01111", "00001", "00001", "01110"],
    " ": ["00000", "00000", "00000", "00000", "00000", "00000", "00000"],
}


def ascii_text(text: str) -> str:
    normalized = unicodedata.normalize("NFKD", text.upper())
    return "".join(ch for ch in normalized if not unicodedata.combining(ch))


def wrap_words(text: str, max_chars: int) -> list[str]:
    words = ascii_text(text).split()
    lines = []
    current = ""
    for word in words:
        candidate = f"{current} {word}".strip()
        if len(candidate) <= max_chars:
            current = candidate
        else:
            if current:
                lines.append(current)
            current = word[:max_chars]
        if len(lines) == 2:
            break
    if current and len(lines) < 3:
        lines.append(current)
    return lines[:3]


def write_png(path: Path, width: int, height: int, pixels: bytearray):
    def chunk(kind: bytes, data: bytes) -> bytes:
        import zlib

        crc = zlib.crc32(kind + data) & 0xFFFFFFFF
        return len(data).to_bytes(4, "big") + kind + data + crc.to_bytes(4, "big")

    import zlib

    raw = bytearray()
    row_size = width * 3
    for y in range(height):
        raw.append(0)
        start = y * row_size
        raw.extend(pixels[start:start + row_size])

    data = b"\x89PNG\r\n\x1a\n"
    data += chunk(b"IHDR", width.to_bytes(4, "big") + height.to_bytes(4, "big") + bytes([8, 2, 0, 0, 0]))
    data += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
    data += chunk(b"IEND", b"")
    path.write_bytes(data)


def set_pixel(pixels: bytearray, width: int, height: int, x: int, y: int, color: tuple[int, int, int]):
    if 0 <= x < width and 0 <= y < height:
        idx = (y * width + x) * 3
        pixels[idx:idx + 3] = bytes(color)


def fill_rect(pixels, width, height, x, y, w, h, color):
    for yy in range(max(0, y), min(height, y + h)):
        for xx in range(max(0, x), min(width, x + w)):
            set_pixel(pixels, width, height, xx, yy, color)


def draw_rect(pixels, width, height, x, y, w, h, color, thickness=2):
    fill_rect(pixels, width, height, x, y, w, thickness, color)
    fill_rect(pixels, width, height, x, y + h - thickness, w, thickness, color)
    fill_rect(pixels, width, height, x, y, thickness, h, color)
    fill_rect(pixels, width, height, x + w - thickness, y, thickness, h, color)


def draw_circle(pixels, width, height, cx, cy, radius, color):
    r2 = radius * radius
    for y in range(cy - radius, cy + radius + 1):
        for x in range(cx - radius, cx + radius + 1):
            if (x - cx) ** 2 + (y - cy) ** 2 <= r2:
                set_pixel(pixels, width, height, x, y, color)


def draw_text(pixels, width, height, x, y, text, color, scale=3):
    cursor = x
    for ch in ascii_text(text):
        glyph = FONT.get(ch, FONT[" "])
        for gy, row in enumerate(glyph):
            for gx, bit in enumerate(row):
                if bit == "1":
                    fill_rect(pixels, width, height, cursor + gx * scale, y + gy * scale, scale, scale, color)
        cursor += 6 * scale


def palette_for(text: str):
    palettes = [
        ((237, 86, 73), (255, 244, 229)),
        ((42, 127, 98), (232, 246, 238)),
        ((40, 91, 161), (233, 240, 252)),
        ((222, 171, 35), (255, 248, 220)),
        ((142, 82, 157), (246, 236, 248)),
        ((31, 135, 160), (229, 247, 250)),
        ((172, 65, 89), (252, 235, 240)),
        ((83, 111, 57), (239, 246, 229)),
    ]
    return palettes[sum(ord(ch) for ch in text) % len(palettes)]


def draw_product_shape(pixels, width, height, nombre: str, primary, bg):
    n = ascii_text(nombre)
    dark = tuple(max(0, c - 45) for c in primary)
    light = tuple(min(255, c + 80) for c in primary)

    if any(word in n for word in ["LECHE", "YOGURT", "SHAMPOO", "JUGO", "AGUA", "REFRESCO", "CERVEZA"]):
        fill_rect(pixels, width, height, 136, 48, 48, 18, dark)
        fill_rect(pixels, width, height, 126, 66, 68, 108, primary)
        fill_rect(pixels, width, height, 136, 92, 48, 42, bg)
        draw_rect(pixels, width, height, 126, 66, 68, 108, dark, 3)
    elif any(word in n for word in ["ATUN", "SARDINAS", "CHILES", "ELOTE", "PURE", "CHAMPINONES"]):
        fill_rect(pixels, width, height, 100, 78, 120, 78, primary)
        fill_rect(pixels, width, height, 100, 78, 120, 16, light)
        fill_rect(pixels, width, height, 116, 104, 88, 28, bg)
        draw_rect(pixels, width, height, 100, 78, 120, 78, dark, 3)
    elif any(word in n for word in ["PAPEL", "SERVILLETAS", "TOALLAS", "PANUELOS"]):
        draw_circle(pixels, width, height, 160, 112, 58, primary)
        draw_circle(pixels, width, height, 160, 112, 24, bg)
        draw_circle(pixels, width, height, 160, 112, 10, dark)
    elif any(word in n for word in ["HUEVO", "PAPAS", "CACAHUATES", "GALLETAS", "BOLSAS"]):
        fill_rect(pixels, width, height, 100, 68, 120, 104, primary)
        fill_rect(pixels, width, height, 116, 88, 88, 54, bg)
        draw_rect(pixels, width, height, 100, 68, 120, 104, dark, 3)
    else:
        fill_rect(pixels, width, height, 108, 54, 104, 122, primary)
        fill_rect(pixels, width, height, 122, 82, 76, 52, bg)
        draw_rect(pixels, width, height, 108, 54, 104, 122, dark, 3)


def generate_placeholder_image(producto: dict, dest: Path):
    width, height = 320, 240
    nombre = producto["nombre"]
    categoria = producto.get("categoria", "Producto")
    primary, bg = palette_for(categoria + nombre)
    pixels = bytearray(bg * (width * height))

    fill_rect(pixels, width, height, 0, 0, width, 36, primary)
    fill_rect(pixels, width, height, 0, height - 48, width, 48, primary)
    draw_product_shape(pixels, width, height, nombre, primary, bg)
    draw_text(pixels, width, height, 14, 10, f'P{int(producto["id"]):04d}', (255, 255, 255), scale=3)

    y = 194
    for line in wrap_words(nombre, 22):
        draw_text(pixels, width, height, 18, y, line, (255, 255, 255), scale=2)
        y += 16

    write_png(dest, width, height, pixels)
    return dest


def get_duckduckgo_image_urls(query: str, max_urls: int = 8):
    """Devuelve URLs de imágenes desde DuckDuckGo sin API key."""
    if not USAR_INTERNET:
        return []

    opener = build_opener(HTTPCookieProcessor(CookieJar()))
    html_bytes, _ = fetch_bytes(
        "https://duckduckgo.com/",
        params={"q": query, "iax": "images", "ia": "images"},
        timeout=20,
        opener=opener,
    )
    html = html_bytes.decode("utf-8", errors="replace")
    m = re.search(r"vqd=['\"]([^'\"]+)", html)
    if not m:
        return []
    vqd = m.group(1)
    params = {"l": "mx-es", "o": "json", "q": query, "vqd": vqd, "f": ",,,", "p": "1"}
    body, _ = fetch_bytes("https://duckduckgo.com/i.js", params=params, timeout=20, opener=opener)
    data = json.loads(body.decode("utf-8", errors="replace"))
    urls = []
    for item in data.get("results", []):
        u = item.get("image")
        if u and u.startswith("http"):
            urls.append(u)
        if len(urls) >= max_urls:
            break
    return urls


def try_openfoodfacts(nombre: str):
    """Intenta imagen de Open Food Facts. Funciona mejor para comida/bebida."""
    if not USAR_INTERNET:
        return None

    url = "https://world.openfoodfacts.org/cgi/search.pl"
    params = {
        "search_terms": nombre,
        "search_simple": 1,
        "action": "process",
        "json": 1,
        "page_size": 5,
        "countries_tags": "en:mexico",
    }
    try:
        body, _ = fetch_bytes(url, params=params, timeout=20)
        data = json.loads(body.decode("utf-8", errors="replace"))
        for p in data.get("products", []):
            img = p.get("image_front_url") or p.get("image_url")
            if img:
                return img
    except Exception:
        return None
    return None


def download_image(url: str, dest_base: Path):
    try:
        req = Request(safe_url(url), headers=IMAGE_HEADERS)
        r = urlopen(req, timeout=30)
        ctype = r.headers.get("content-type", "").split(";")[0].lower()
        ext = mimetypes.guess_extension(ctype) or ".jpg"
        if ext == ".jpe": ext = ".jpg"
        if ext == ".webp":
            ext = ".jpg"
        if ext not in [".jpg", ".jpeg", ".png"]:
            ext = ".jpg"
        dest = dest_base.with_suffix(ext)
        total = 0
        with r, open(dest, "wb") as f:
            while True:
                chunk = r.read(8192)
                if not chunk:
                    break
                if chunk:
                    total += len(chunk)
                    f.write(chunk)
                    if total > 8_000_000:  # evita imágenes enormes
                        break
        if total < 2000:
            dest.unlink(missing_ok=True)
            return None
        return dest
    except Exception:
        return None


def normalizar_categoria(raw):
    if isinstance(raw, list) and raw:
        raw = raw[-1]
    if not raw:
        return "Producto"
    partes = [p.strip() for p in str(raw).split("/") if p.strip()]
    return partes[-1] if partes else "Producto"


def precio_desde_vtex(producto):
    for item in producto.get("items", []):
        for seller in item.get("sellers", []):
            offer = seller.get("commertialOffer", {})
            price = offer.get("Price") or offer.get("ListPrice")
            if price:
                return float(price)
    return 10.0


def imagen_desde_vtex(producto):
    for item in producto.get("items", []):
        for image in item.get("images", []):
            url = image.get("imageUrl") or image.get("imageTag")
            if url and str(url).startswith("http"):
                return url
    return ""


def cargar_productos_surtitienda(max_productos=SURTITIENDA_MAX_PRODUCTOS):
    productos = []
    pagina = 0
    por_pagina = 50

    while len(productos) < max_productos:
        desde = pagina * por_pagina
        hasta = min(desde + por_pagina - 1, max_productos - 1)
        params = {
            "_from": desde,
            "_to": hasta,
            "O": "OrderByScoreDESC",
        }
        data = fetch_json(SURTITIENDA_API, params=params)
        if not data:
            break

        for producto in data:
            item = (producto.get("items") or [{}])[0]
            nombre = producto.get("productName") or item.get("nameComplete") or item.get("name")
            if not nombre:
                continue

            pid = len(productos) + 1
            productos.append({
                "id": pid,
                "codigo": f"P{pid:04d}",
                "sku": item.get("itemId") or producto.get("productId") or "",
                "nombre": nombre.strip(),
                "categoria": normalizar_categoria(producto.get("categories")),
                "marca": producto.get("brand") or "",
                "precio": precio_desde_vtex(producto),
                "imagen_url": imagen_desde_vtex(producto),
            })

            if len(productos) >= max_productos:
                break

        if len(data) < por_pagina:
            break
        pagina += 1

    if not productos:
        raise RuntimeError("Surti-Tienda no devolvio productos.")
    return productos


def cargar_productos():
    if FUENTE_PRODUCTOS == "surtitienda":
        try:
            return cargar_productos_surtitienda()
        except Exception as e:
            print(f"No pude leer Surti-Tienda, uso respaldo local: {e}")

    if FUENTE_PRODUCTOS == "db":
        if not DB_PATH.exists():
            raise SystemExit(f"No se encontro {DB_PATH}.")
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            rows = conn.execute("SELECT codigo, nombre FROM productos ORDER BY codigo").fetchall()
        productos = []
        for index, row in enumerate(rows, start=1):
            codigo = row["codigo"]
            match = re.search(r"\d+", codigo or "")
            pid = int(match.group(0)) if match else index
            productos.append({"id": pid, "codigo": codigo, "nombre": row["nombre"], "categoria": "Producto"})
        return productos

    if PRODUCTOS_PATH.exists():
        productos = json.loads(PRODUCTOS_PATH.read_text(encoding="utf-8"))
        for p in productos:
            p.setdefault("codigo", f'P{int(p["id"]):04d}')
        return productos

    if not DB_PATH.exists():
        raise SystemExit(f"No se encontro {PRODUCTOS_PATH} ni {DB_PATH}.")

    with sqlite3.connect(DB_PATH) as conn:
        conn.row_factory = sqlite3.Row
        rows = conn.execute("SELECT codigo, nombre FROM productos ORDER BY codigo").fetchall()

    productos = []
    for index, row in enumerate(rows, start=1):
        codigo = row["codigo"]
        match = re.search(r"\d+", codigo or "")
        pid = int(match.group(0)) if match else index
        productos.append({"id": pid, "nombre": row["nombre"], "categoria": "Producto"})
    return productos


def actualizar_base_de_datos(rows):
    if not DB_PATH.exists():
        print(f"  No encontre base de datos para actualizar: {DB_PATH}")
        return 0

    with sqlite3.connect(DB_PATH) as conn:
        conn.execute(
            "CREATE TABLE IF NOT EXISTS productos ("
            "codigo TEXT PRIMARY KEY, "
            "nombre TEXT NOT NULL, "
            "precio REAL NOT NULL DEFAULT 0, "
            "cantidad INTEGER NOT NULL DEFAULT 0, "
            "stock_minimo INTEGER NOT NULL DEFAULT 0, "
            "imagen TEXT NOT NULL DEFAULT '')"
        )
        columnas = [r[1] for r in conn.execute("PRAGMA table_info(productos)").fetchall()]
        if "stock_minimo" not in columnas:
            conn.execute("ALTER TABLE productos ADD COLUMN stock_minimo INTEGER NOT NULL DEFAULT 0")
        if "imagen" not in columnas:
            conn.execute("ALTER TABLE productos ADD COLUMN imagen TEXT NOT NULL DEFAULT ''")

        actualizados = 0
        for row in rows:
            if not row["archivo"]:
                continue
            codigo = row.get("codigo") or f'P{int(row["id"]):04d}'
            ruta_recurso = f'{RESOURCE_PREFIX}/{Path(row["archivo"]).name}'
            precio_demo = float(row.get("precio") or (10.0 + (int(row["id"]) % 15) * 2.0))
            conn.execute(
                "INSERT OR IGNORE INTO productos "
                "(codigo, nombre, precio, cantidad, stock_minimo, imagen) "
                "VALUES (?, ?, ?, ?, ?, ?)",
                (codigo, row["nombre"], precio_demo, 20, 5, ruta_recurso),
            )
            cur = conn.execute(
                "UPDATE productos SET nombre = ?, precio = ?, imagen = ? WHERE codigo = ?",
                (row["nombre"], precio_demo, ruta_recurso, codigo),
            )
            actualizados += cur.rowcount
        conn.commit()
    return actualizados


def main():
    productos = cargar_productos()
    OUT_DIR.mkdir(exist_ok=True)
    rows = []
    print(f"Fuente de productos: {FUENTE_PRODUCTOS}")
    print(f"Carpeta destino: {OUT_DIR}")

    for p in productos:
        pid = int(p["id"])
        nombre = p["nombre"]
        categoria = p["categoria"]
        base_name = f"{pid:03d}_{slugify(nombre)}"
        dest_base = OUT_DIR / base_name
        print(f"[{pid:03d}/{len(productos)}] Preparando imagen: {nombre}")

        urls = []
        if p.get("imagen_url"):
            urls.append(p["imagen_url"])

        off_url = try_openfoodfacts(nombre)
        if off_url:
            urls.append(off_url)

        query = f'{marca_hint(nombre, categoria)} "{nombre}" producto supermercado México'
        try:
            urls.extend(get_duckduckgo_image_urls(query, max_urls=10))
        except Exception as e:
            print(f"  DuckDuckGo falló: {e}")

        saved = None
        source_url = ""
        for u in urls:
            saved = download_image(u, dest_base)
            if saved:
                source_url = u
                break

        if not saved:
            saved = generate_placeholder_image(p, dest_base.with_suffix(".png"))
            source_url = "generada_localmente"
            print(f"  Generada localmente: {saved.name}")
        else:
            print(f"  Descargada: {saved.name}")

        rows.append({
            "id": pid,
            "codigo": p.get("codigo") or f"P{pid:04d}",
            "sku": p.get("sku", ""),
            "nombre": nombre,
            "categoria": categoria,
            "marca": p.get("marca", ""),
            "precio": p.get("precio", ""),
            "archivo": str(saved.relative_to(BASE)) if saved else "",
            "estado": "descargado" if source_url.startswith("http") else "generado",
            "fuente_url": source_url,
            "busqueda": query,
        })
        if USAR_INTERNET:
            time.sleep(0.8)  # amable con los servidores

    with MANIFEST_CSV.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(rows[0].keys()))
        writer.writeheader()
        writer.writerows(rows)
    MANIFEST_JSON.write_text(json.dumps(rows, ensure_ascii=False, indent=2), encoding="utf-8")

    with zipfile.ZipFile(ZIP_PATH, "w", zipfile.ZIP_DEFLATED) as z:
        for file in OUT_DIR.glob("*.*"):
            z.write(file, file.relative_to(BASE))
        z.write(MANIFEST_CSV, MANIFEST_CSV.name)
        z.write(MANIFEST_JSON, MANIFEST_JSON.name)

    total = sum(1 for r in rows if r["estado"] == "descargado")
    generadas = sum(1 for r in rows if r["estado"] == "generado")
    actualizadas = actualizar_base_de_datos(rows)
    print(f"Imagenes generadas localmente: {generadas}")
    print(f"Base de datos actualizada: {actualizadas} productos en {DB_PATH}")
    print(f"Listo: {total} imagenes descargadas, {generadas} imagenes generadas")
    total = total + generadas
    print(f"\nListo: {total}/{len(productos)} imágenes descargadas")
    print(f"ZIP: {ZIP_PATH}")

if __name__ == "__main__":
    main()
