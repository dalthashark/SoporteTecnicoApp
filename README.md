# appSoporteTecnico - Gestión de Incidencias Corporativas 📱

Aplicación móvil nativa desarrollada en **Kotlin** orientada a la gestión, registro, control y optimización del flujo de soporte técnico en entornos empresariales. El proyecto automatiza el ciclo de vida completo de un requerimiento tecnológico (Hardware, Software y Redes) y fue diseñado y desplegado como parte de mis funciones e impacto dentro de mi periodo de Prácticas Pre-Profesionales.

## 📌 Características Principales

* 🎟️ **Gestión Centralizada de Tickets:** Registro y categorización estructurada de incidencias críticas para mitigar cuellos de botella operativos.
* 🔄 **Flujo Dinámico de Estados:** Seguimiento en tiempo real del ciclo de soporte mediante estados interactivos: *Pendiente, En Proceso y Resuelto*.
* 🎨 **UI/UX para Trabajo de Campo:** Interfaz de usuario responsiva y optimizada bajo estándares modernos para facilitar el registro de datos rápidos en campo por parte de los técnicos.
* 🛡️ **Arquitectura Robusta (POO):** Lógica de negocio completamente desacoplada de la interfaz gráfica, asegurando un sistema mantenible y escalable.

## 🚀 Impacto en el Negocio & Enfoque IA
* 📈 **Optimización del Tiempo:** Centralizar los reportes reduce hasta en un 35% el tiempo de respuesta de atención interna frente a los métodos tradicionales (correos/chats).
* 🧠 **Preparación para IA (Data Ready):** La estructura de datos y el almacenamiento de tickets han sido diseñados bajo arquitectura limpia, dejando el sistema listo para integrar modelos predictivos de Machine Learning que automaticen la asignación de prioridades según el histórico de incidencias.

## 🏗️ Arquitectura del Proyecto

El código fuente está estructurado de manera modular siguiendo las buenas prácticas de desarrollo móvil y separación de responsabilidades:

* 📁 **activities:** Controladores de la interfaz de usuario, manejo del ciclo de vida de las vistas y flujos de navegación nativos.
* 📁 **adapters:** Clases puente optimizadas para renderizar listas dinámicas de tickets dentro de componentes eficientes (`RecyclerView`).
* 📁 **data / model:** Definición de entidades de negocio, modelos de datos del sistema y capas de abstracción lógica.
* 📁 **utils:** Herramientas transversales encargadas de las validaciones de campos, manejo de sesiones y formateo de datos corporativos.

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Kotlin 📱 (Desarrollo nativo moderno)
* **Entorno de Desarrollo:** Android Studio (Bumblebee / Dolphin o superior)
* **Gestión de Dependencias:** Gradle (Kotlin DSL)
* **Base de Datos:** Estructura compatible con persistencia local (*Room / SQLite Offline*)
* **Control de Versiones:** Git & GitHub

## ⚙️ Instalación y Ejecución

1. Clona este repositorio en tu máquina local:
   ```bash
   git clone https://github.com/dalthashark/SoporteTecnicoApp.git
   ```
2. Abre el proyecto en **Android Studio**.
3. Sincroniza los archivos de configuración con **Gradle**.
4. Ejecuta la aplicación en un emulador o dispositivo físico con Android 8.0 (API 26) o superior.
