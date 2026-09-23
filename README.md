[![Visitor Badge](https://api.visitorbadge.io/api/VisitorHit?user=cris959&repo=rag-updater-streamlit&countColor=%23ff007f)](https://www.github.com/cris959/rag-updater-streamlit)

# spring-ai-rag

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-F2F4F9?style=for-the-badge&logo=spring&logoColor=6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-PgVector-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Tika](https://img.shields.io/badge/Apache_Tika-Document_Parser-D22128?style=for-the-badge&logo=apache&logoColor=white)
![Ollama](https://img.shields.io/badge/Ollama-Local_LLM-000000?style=for-the-badge&logo=ollama&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

Aplicación de Recuperación Aumentada por Generación (RAG) desarrollada con **Spring Boot 3.4.3**, **Java 21** y **Spring AI**, utilizando modelos locales de inteligencia artificial con **Ollama**, procesamiento de documentos con **Apache Tika** y persistencia vectorial con **PostgreSQL y PgVector**.

## Características Principales
* **IA Local:** Ejecución de modelos de lenguaje (Mistral / Ollama) de forma completamente local.
* **Procesamiento de Documentos:** Ingesta y análisis automatizado de manuales técnicos en PDF (Spring Boot Reference Documentation v3.2.9) mediante Apache Tika.
* **Vector Store:** Almacenamiento y búsqueda de embeddings vectoriales de 768 dimensiones optimizados con la extensión PgVector en PostgreSQL.
* **Arquitectura Contenerizada:** Entorno completamente aislado y reproducible mediante Docker Compose.
___

## Tecnologías y Stack Utilizado

| Tecnología | Versión / Descripción |
| :--- | :--- |
| **Java** | `21` |
| **Spring Boot** | `3.4.3` |
| **Spring AI** | `1.0.0-M6` |
| **Ollama** | Modelos locales de lenguaje y embeddings |
| **PostgreSQL + PgVector** | Almacenamiento y búsqueda vectorial |
| **Apache Tika** | Lector y procesador de documentos |
| **Docker Compose** | Contenerización de servicios |
___

## 📚 Documentación de Referencia Indexada
* **Manual Base:** Spring Boot Reference Documentation
* **Autores:** Phillip Webb, Dave Syer, Josh Long, Stéphane Nicoll, Rob Winch, Andy Wilkinson, Marcel Overdijk, Christian Dupuis, Sébastien Deleuze, Michael Simons, Vedran Pavić, Jay Bryant, Madhura Bhave, Eddú Meléndez, Scott Frederick, Moritz Halbritter
* **Versión:** `3.2.9`
___

## Configuración de Ollama (IA Local)

Este proyecto utiliza **Ollama** ejecutándose en un contenedor Docker para correr modelos de lenguaje de forma local y generar los embeddings y respuestas del RAG.

### 1. Variables de Configuración en `application.properties`
```properties
# Configuración del modelo Ollama (por defecto Mistral)
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=mistral
spring.ai.ollama.embedding.options.model=mistral
```
2. Comandos Útiles para Ollama
°  Verificar que el servicio y los modelos estén activos:

````bash
docker exec -it spring-ai-rag-ollama-1 ollama list
````
° Descargar el modelo localmente (si aún no lo tienes):

````bash
docker exec -it spring-ai-rag-ollama-1 ollama pull mistral
````
# Base de Datos y Persistencia (PostgreSQL + PgVector)
Este proyecto utiliza PostgreSQL con la extensión PgVector ejecutándose en un contenedor Docker para almacenar los embeddings vectoriales.

Configuración en **application.properties**

````properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vectordb
spring.datasource.username=testuser
spring.datasource.password=testpwd
````
Ajuste de Dimensiones del Vector Store (768 dim)
Si necesitas actualizar o alinear la dimensión de los embeddings generados por el modelo en tu base de datos, ejecuta las siguientes sentencias SQL:

````sql
-- Elimina el índice existente si tienes uno
DROP INDEX IF EXISTS vector_store_embedding_idx;

-- Altera la dimensión de la columna
ALTER TABLE vector_store ALTER COLUMN embedding TYPE vector(768);

-- Vuelve a crear el índice HNSW
CREATE INDEX vector_store_embedding_idx ON vector_store USING hnsw (embedding vector_cosine_ops);
````
Comandos Útiles para la Consola (Docker & PostgreSQL)

° Levantar los servicios:

````bash
docker compose up -d
````
° Apagar y limpiar contenedores con volúmenes (si necesitas reiniciar la DB de cero):
````bash
docker compose down -v
````
° Acceder a la consola interactiva de PostgreSQL dentro del contenedor:

````bash
docker exec -it spring-ai-rag-pgvector-1 psql -U testuser -d vectordb
````
° Comandos esenciales dentro de psql:
° Listar tablas: **\dt**
° Ver registros almacenados: **SELECT id, content FROM vector_store**;
° Salir de la consola: **\q**
## 🚀 Ejemplo de Uso (RAG en Acción)
Puedes interactuar con el sistema enviando una petición HTTP (por ejemplo, a través de Postman o cURL) a tu endpoint de chat:
° Endpoint: **POST http://localhost:8000/api/chat**
° Body (JSON):
````json
{
  "question": "How to enable Spring Boot actuator?"
}
````
💡 Respuesta generada por el sistema RAG:
To enable Spring Boot's production-ready features, you can add the spring-boot-starter-actuator dependency to your project. For a Maven-based project, add the following dependency:

````xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
````
For a Gradle project, use the following declaration:
````groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
````
After adding the dependency, Spring Boot provides a set of built-in endpoints that you can enable or disable to monitor and interact with your application...

## Diagrama de la Arquitectura

````mermaid
graph TD
    %% Capa de Cliente
    Client["Client / Postman"] -->|POST /api/chat| API["FastAPI / Spring Boot API"]

    %% Capa de Aplicación RAG
    subgraph App ["Application Layer (RAG Engine)"]
        API -->|1. Envía Pregunta| Retriever["Retriever Component"]
        Retriever -->|2. Busca Chunks Similares| VectorStore[(PostgreSQL + pgvector)]
        VectorStore -->|3. Retorna Chunks Relevantes| Retriever
        Retriever -->|4. Contexto + Pregunta| LLM["LLM (Ollama / Groq)"]
        LLM -->|5. Genera Respuesta Basada en Contexto| API
    end

    %% Capa de Datos / Ingesta Previa
    subgraph Ingestion ["Ingestion Pipeline (Previa)"]
        PDF["Spring Boot PDF Manual (v3.2.9)"] -->|Apache Tika + Text Splitter| Chunks["549 Chunks"]
        Chunks -->|Embedding Model| Embeddings["Vectores (768 dim)"]
        Embeddings -->|Guardar| VectorStore
    end

    API -->|Retorna Respuesta JSON| Client

    style VectorStore fill:#f9f,stroke:#333,stroke-width:2px
    style LLM fill:#bbf,stroke:#333,stroke-width:2px
    style API fill:#bfb,stroke:#333,stroke-width:2px
````
## 📥 Instalación de Ollama

Para ejecutar los modelos de lenguaje y generar los embeddings de forma local, necesitas tener instalado Ollama en tu equipo:

1. Visita la [página oficial de Ollama](https://ollama.com/) para descargar e instalar la versión correspondiente a tu sistema operativo (Windows, macOS o Linux).
2. Una vez instalado, verifica que el servicio esté corriendo en tu terminal ejecutando:

```bash
   ollama --version
```
1- Descarga el modelo principal utilizado en este proyecto (Mistral) ejecutando:
````bash
ollama pull mistral
````
___
## 📄 Licencia
Este proyecto está bajo los términos de la Licencia MIT. Consulta el archivo LICENSE para más detalles.


