# LangApp — Aplicación móvil de aprendizaje de idiomas

Aplicación Android desarrollada en **Kotlin + Jetpack Compose** que consume una API REST construida con **Django REST Framework + PostgreSQL**. Permite a los usuarios aprender idiomas mediante lecciones y ejercicios interactivos, con autenticación JWT y un panel de administración para gestionar el contenido.

Repositorio: https://github.com/luislazo-ute/langapp_lazo.git

---

## Descripción

LangApp es una app de aprendizaje de idiomas. El usuario elige un idioma, avanza por niveles (A1, A2, B1...), entra a las lecciones de cada nivel y resuelve ejercicios de opción múltiple que suman XP. La app distingue entre usuarios normales (consultan contenido y aprenden) y administradores (gestionan todo el contenido vía un panel CRUD dentro de la app).

---

## Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM (Model - View - ViewModel)
- **Inyección de dependencias:** Hilt
- **Red:** Retrofit + OkHttp + Gson
- **Asincronía:** Coroutines + Flow / StateFlow
- **Persistencia local:** Jetpack DataStore (tokens JWT)
- **Backend:** Django REST Framework + PostgreSQL (desplegado)

---

## Requisitos de instalación

- Android Studio (Ladybug o superior)
- JDK 17
- Android SDK API 35, dispositivo o emulador con **API 26+** (Android 8.0)
- Conexión a internet (el backend está desplegado en línea)

---

## Configuración de la URL base del backend

La URL del backend se define en el archivo `local.properties` (raíz del proyecto):

```properties
# Backend desplegado (producción):
API_BASE_URL=https://lazo-idiomas.uaeftt-ute.site/api/

# Si corres el backend localmente con el emulador, usa:
# API_BASE_URL=http://10.0.2.2:8000/api/
```

> `10.0.2.2` es la IP especial del emulador de Android que apunta al `localhost` de tu PC. Para un dispositivo físico, usa la IP local de tu máquina (ej. `http://192.168.1.X:8000/api/`).

El valor se inyecta en tiempo de compilación a `BuildConfig.API_BASE_URL` desde `app/build.gradle.kts`.

---

## Usuario y contraseña de prueba

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | `admin` | `admin` |

El usuario administrador puede crear, editar y eliminar contenido desde el panel de administración (Perfil → Panel de administración). Cualquier usuario nuevo registrado desde la app es un usuario normal (solo consulta).

> **Nota:** el campo de usuario en el login NO auto-capitaliza, así que escribe `admin` en minúsculas tal cual.

---

## Las 7 entidades implementadas

| # | Entidad | Descripción |
|---|---------|-------------|
| 1 | **Languages** (Idiomas) | Idiomas disponibles para aprender (Inglés, Francés...). |
| 2 | **Levels** (Niveles) | Niveles de dificultad por idioma según el marco CEFR (A1, A2, B1...). |
| 3 | **Lessons** (Lecciones) | Unidades de aprendizaje dentro de cada nivel (Saludos, Números...). |
| 4 | **Exercises** (Ejercicios) | Preguntas interactivas de opción múltiple dentro de cada lección. |
| 5 | **Profiles** (Perfiles) | Información y estadísticas del usuario: XP total, racha de días, idioma y nivel actual. |
| 6 | **Enrollments** (Inscripciones) | Registro de los niveles en los que está inscrito el usuario. |
| 7 | **Progress** (Progreso) | Seguimiento de los ejercicios completados y los puntos (XP) obtenidos. |

---

## Operaciones CRUD

La app implementa el ciclo completo sobre las entidades de contenido (Languages, Levels, Lessons, Exercises):

- **Listar** — todas las entidades se listan en sus pantallas.
- **Ver detalle** — navegación jerárquica: Idioma → Niveles → Lecciones → Ejercicios.
- **Crear** — desde el panel de administración (botón flotante ＋).
- **Actualizar** — botón de edición (lápiz) en cada elemento.
- **Eliminar** — botón de borrado (papelera) con diálogo de confirmación.

Las operaciones de creación, edición y borrado están restringidas al administrador.

---

## Listado de pantallas

| Pantalla | Descripción |
|----------|-------------|
| **Login** | Inicio de sesión con JWT. |
| **Registro** | Creación de cuenta de usuario normal. |
| **Inicio (Home)** | Lista de idiomas con barra de búsqueda. |
| **Niveles** | Niveles de un idioma, con botón de inscripción (Enrollment). |
| **Lecciones** | Lecciones de un nivel. |
| **Ejercicios** | Ejercicios interactivos de opción múltiple con barra de progreso. |
| **Progreso** | Lista de ejercicios completados y XP obtenido. |
| **Perfil** | Datos del usuario, XP, racha; acceso al panel admin si es staff. |
| **Panel de administración** | CRUD de idiomas, niveles, lecciones y ejercicios (solo admin). |

---

## Autenticación y seguridad

- Autenticación mediante **JWT** (access + refresh token).
- El token de acceso se almacena de forma persistente con **DataStore**.
- Todas las peticiones protegidas envían el header `Authorization: Bearer <access_token>` mediante un interceptor.
- Cuando el token de acceso expira (HTTP 401), un `Authenticator` lo renueva automáticamente con el refresh token y reintenta la petición.
- La sesión persiste al cerrar y reabrir la app.
- Cierre de sesión limpia los tokens almacenados.
- **Diferenciación de roles:** tras el login se consulta `GET /profiles/me/` para leer `is_staff`. Si el usuario es administrador, se muestra el panel de gestión; si no, los botones de crear/editar/borrar permanecen ocultos.

---

## Búsqueda y paginación

- **Búsqueda:** la pantalla de Inicio incluye una barra de búsqueda conectada al parámetro `?search=` del backend para filtrar idiomas.
- **Paginación:** la app consume los endpoints paginados de Django REST Framework (formato `count / next / previous / results`).
- **Estados:** se muestran indicadores de carga, mensajes cuando no hay datos y pantallas de error con opción de reintentar.

---

## Manejo de errores

La app mapea los códigos HTTP a mensajes claros para el usuario:

| Código | Mensaje |
|--------|---------|
| 400 | Datos inválidos. Revisa los campos. |
| 401 | Usuario o contraseña incorrectos / Sesión expirada. |
| 403 | Sin permisos de administrador. |
| 404 | No encontrado. |
| 500 / 502 / 503 | Error del servidor. Intenta más tarde. |
| Sin conexión | Se captura la excepción de red y se muestra en pantalla. |

Los mensajes se muestran mediante `Snackbar` (acciones del panel admin) y pantallas de error con botón de reintentar (listados).

---

## Ejemplos de consumo de la API con token

**Login (obtener token):**
```http
POST https://lazo-idiomas.uaeftt-ute.site/api/auth/login/
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```
Respuesta:
```json
{
  "access": "eyJhbGciOiJIUzI1NiIs...",
  "refresh": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Listar idiomas (con token):**
```http
GET https://lazo-idiomas.uaeftt-ute.site/api/languages/
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

**Buscar idiomas:**
```http
GET https://lazo-idiomas.uaeftt-ute.site/api/languages/?search=ing
Authorization: Bearer <access_token>
```

**Crear un idioma (solo admin):**
```http
POST https://lazo-idiomas.uaeftt-ute.site/api/languages/
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "nombre": "Francés",
  "codigo": "fr",
  "bandera_emoji": "🇫🇷",
  "activo": true
}
```

**Ver perfil propio (incluye is_staff):**
```http
GET https://lazo-idiomas.uaeftt-ute.site/api/profiles/me/
Authorization: Bearer <access_token>
```

---

## Instrucciones para ejecutar la app

1. Clona el repositorio:
   ```bash
   git clone https://github.com/luislazo-ute/langapp_lazo.git
   ```
2. Abre el proyecto en Android Studio y espera a que sincronice Gradle.
3. Verifica que `local.properties` tenga la línea:
   ```properties
   API_BASE_URL=https://lazo-idiomas.uaeftt-ute.site/api/
   ```
4. Conecta un emulador o dispositivo con Android 8.0 (API 26) o superior.
5. Pulsa **Run ▶**.
6. Inicia sesión con `admin` / `admin` para ver todas las funciones, incluido el panel de administración.

### Generar el APK

En Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

O por línea de comandos:
```bash
./gradlew assembleDebug
```

---

## Arquitectura del proyecto

```
com.langapp/
├── data/
│   ├── remote/
│   │   ├── api/          → interfaces Retrofit (endpoints)
│   │   ├── dto/          → objetos de transferencia (JSON) + mappers
│   │   └── interceptor/  → interceptor JWT (Bearer + refresh)
│   ├── local/            → DataStore (tokens)
│   └── repository/       → implementaciones de repositorios
├── domain/
│   ├── model/            → modelos de negocio
│   └── repository/       → interfaces de repositorios
├── presentation/
│   ├── navigation/       → rutas y NavGraph
│   ├── ui/               → pantallas por módulo
│   ├── viewmodel/        → ViewModels (MVVM)
│   └── components/       → componentes reutilizables
├── di/                   → módulos Hilt
└── theme/                → colores, tipografía, formas
```

---

Desarrollado por Luis Lazo — Universidad UTE.
