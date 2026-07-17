# 🏋️‍♂️ Fortium — Sthenos Studio 🥇
### *Tu fuerza, bajo control.*

[![Android Nativo](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white&style=for-the-badge)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)](https://developer.android.com/topic/libraries/architecture)
[![Database](https://img.shields.io/badge/Persistence-Room%20%7C%20SQLite-47A248?logo=sqlite&logoColor=white&style=for-the-badge)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-CC%20BY--SA%203.0-orange?style=for-the-badge)](http://creativecommons.org/licenses/by-sa/3.0/es/)

**Fortium** es una aplicación móvil nativa para Android diseñada específicamente para atletas de fuerza, powerlifting y culturismo. Desarrollada bajo la premisa de ingeniería **Offline-First**, la aplicación garantiza un control milimétrico del rendimiento físico y de la sobrecarga progresiva sin depender de servidores externos, priorizando de forma absoluta la privacidad del usuario y la fiabilidad en entornos de baja conectividad (como sótanos o gimnasios de alta densidad).

Este proyecto representa el **Proyecto de Fin de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM)**.

---

## 🚀 Destacados Técnicos (Por qué esto te interesa si eres Reclutador/Tech Lead)

Como desarrollador, diseñé Fortium aplicando las mejores prácticas de la industria móvil, asegurando un código limpio, desacoplado y altamente mantenible:

*   **Arquitectura MVVM Rigurosa:** Separación estricta de responsabilidades entre la Vista (`Activity`/`Fragment`), la Lógica de Presentación (`ViewModel`) y la Capa de Datos (`Repository`).
*   **Filosofía Offline-First & Room:** Implementación de persistencia local relacional avanzada mediante **Room Persistence Library** sobre un motor **SQLite**.
*   **Automatización en Segundo Plano:** Uso de la API **WorkManager** (`AutoBackupWorker`) para planificar y ejecutar copias de seguridad resilientes en formato **JSON** de forma totalmente transparente para el usuario.
*   **Ingeniería de Software & QA:** Control de calidad integral combinando **Pruebas Unitarias (JUnit 4)** para algoritmos lógicos, **Pruebas de Integración** en memoria para la base de datos, y **Pruebas de UI automatizadas (Espresso)**. pipeline CI/CD automatizado con **GitHub Actions**.
*   **Seguridad Digital Nativa:** Blindaje automático contra **Inyección SQL** mediante el mapeo paramétrico de Room y validación/sanitización estricta de entradas en la capa intermedia.

---

## ✨ Funcionalidades Principales

*   **Gestión de Rutinas Personalizadas (CRUD):** Creación y estructuración de planes de entrenamiento definiendo el orden secuencial exacto mediante relaciones de muchos a muchos ($N:M$) normalizadas.
*   **Registro Atómico de Series en Tiempo Real:** Entrada optimizada de variables críticas como peso, repeticiones, y métricas de intensidad avanzada (**RPE** - *Rate of Perceived Exertion* y **RIR** - *Repetitions in Reserve*).
*   **Motor de Estadísticas Local & Estimación de 1RM:** Procesamiento analítico del histórico de carga mediante la fórmula matemática de **Epley** y **Brzycki** para proyectar la fuerza máxima teórica del atleta sin latencia de red.
*   **Temporizador de Descanso Inteligente:** Automatización de los periodos de recuperación entre series recuperando dinámicamente la configuración predefinida por ejercicio.
*   **Soberanía del Dato (Importación/Exportación):** Serialización y deserialización completa de la base de datos a archivos estructurados **JSON** mediante la librería **Gson**, permitiendo la portabilidad absoluta del progreso.

---

## 🛠️ Stack Tecnológico & Herramientas

*   **Lenguaje Principal:** Java (JDK 21)
*   **Entorno de Desarrollo:** Android Studio Ladybug (Target SDK 35 / Min SDK 26)
*   **Persistencia:** Room, SQLite, SharedPreferences
*   **Visualización de Datos:** MPAndroidChart (Procesamiento y renderizado local de gráficas de líneas y barras)
*   **Procesamiento JSON:** Google Gson
*   **Automatización:** Android WorkManager, Gradle Build System
*   **Testing:** JUnit 4, Android Espresso, Mockito
*   **Diseño UI/UX:** Material Design 3 (Estética *Premium Dark* de alto contraste optimizada para entornos deportivos)

---

## 📐 Arquitectura de Software y Modelo de Datos

La aplicación está estructurada con un bajo acoplamiento y alta cohesión. A nivel de paquetes y bases de datos, el flujo sigue este estándar:

```
com.sthenos.fortium
├── data/          # Capa de Acceso a Datos (DAOs, Room Database, Repositorios, Workers)
├── domain/        # Reglas de Negocio Puras (Calculadores matemáticos de 1RM, validadores)
├── model/         # Entidades de Room, Enums de Tipado Fuerte y DTOs para exportación JSON
└── ui/            # Capa de UI (Activities, Fragments, ViewModels organizados por módulo de uso)
```

### Esquema Relacional de Base de Datos
*   **`Usuarios`**: Almacena el perfil antropométrico (peso, altura, género, unidad de medida) necesario para ajustar las fórmulas biológicas de fuerza.
*   **`Ejercicios`**: Catálogo maestro. Incluye un mecanismo de pre-populado automático en la instalación mediante un `RoomDatabase.Callback`. Protege los ejercicios predefinidos contra modificaciones fortuitas (`esPredefinido = true`).
*   **`Rutinas`** y **`RutinaEjercicios`**: Estructuras de planificación resolviendo la relación distributiva $N:M$.
*   **`Sesiones`** y **`Series`**: Registros históricos indexados con claves foráneas (`FK`) configuradas con borrado en cascada para garantizar la integridad referencial absoluta.

---

## 📋 Aseguramiento de la Calidad (Testing & Bugs Solucionados)

El proyecto cuenta con una batería de pruebas automatizadas que validan la resiliencia del software. Puedes consultar el histórico completo de control de errores directamente en mis [GitHub Issues](https://github.com/4rg3nis/Fortium/issues?q=is%3Aissue%20state%3Aclosed%20label%3Abug).

### Muestra de Casos de Prueba (QA):
| ID | Módulo | Descripción | Entrada / Acción | Resultado Esperado |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Lógica | Validación de Fórmula Brzycki | 100kg × 10 reps al RPE 10 | Cálculo preciso de 1RM de 133.37 kg |
| **TC-03** | Lógica | Tolerancia a valores nulos | Introducir 0kg en carga | Intercepción controlada, retorna 0.0 kg sin colapsar |
| **TC-06** | BBDD | Eliminación en cascada | Borrar entidad `Usuario` | Desaparición de registros vinculados en SQLite sin huérfanos |
| **TC-08** | Lógica | Parseo erróneo de fechas | Inyectar cadena "32/13/1999" | Bloqueo try-catch intercepta error y evita el crasheo |

---

## 💾 Despliegue e Instalación

Para compilar de forma local el código fuente de **Fortium**, asegúrate de contar con Android Studio Ladybug o superior y el JDK 21 configurado.

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/4rg3nis/Fortium.git
    ```
2.  **Sincronizar Gradle:** Abre el proyecto en Android Studio y permite que descargue las dependencias indexadas.
3.  **Compilar APK de Producción:** Ve a `Build > Generate Signed Bundle / APK...`, selecciona APK y firma el paquete utilizando tu almacén de claves (Keystore) para generar el instalador optimizado `app-release.apk`.

---

## 📄 Licencia

Este proyecto está bajo la licencia **Creative Commons Reconocimiento-CompartirIgual 3.0 España (CC BY-SA 3.0 ES)**. Siéntete libre de auditar el código, clonarlo y proponer mejoras.

---

### 📬 ¿Interesado en mi perfil técnico? ¡Hablemos!
Estoy graduado en **Desarrollo de Aplicaciones Multiplataforma (DAM)** y buscando incorporarme a un equipo donde pueda aportar mi pasión por el desarrollo nativo, la arquitectura limpia y las soluciones de alto rendimiento.

*   **Desarrollador:** Argenis Javier Lora Bautista
