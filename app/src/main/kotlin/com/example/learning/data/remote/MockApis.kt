package com.example.learning.data.remote

import com.example.learning.data.AuthApi
import com.example.learning.data.CourseApi
import com.example.learning.data.InvalidCredentialsException
import com.example.learning.data.Session
import com.example.learning.domain.Course
import com.example.learning.domain.Lesson
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Mock remote course API. Supports simulated offline mode toggle for testing and demoing.
 */
class MockCourseApi(var failing: Boolean = false) : CourseApi {

    fun toggleOffline(): Boolean {
        failing = !failing
        return failing
    }

    override suspend fun fetchCourses(): List<Course> {
        delay(600) // Realistic network latency
        if (failing) throw IOException("Simulated network failure: Device is offline or server unreachable")
        return MOCK_COURSES
    }
}

class MockAuthApi : AuthApi {
    override suspend fun login(email: String, password: String): Session {
        delay(650) // Realistic auth handshake
        if (password == "wrongpass") throw InvalidCredentialsException()
        return Session(token = "jwt_mock_token_${System.currentTimeMillis()}", email = email)
    }
}

private val PYTHON_LESSONS = listOf(
    "Introduction & Environment Setup",
    "Variables & Data Types",
    "Control Flow & Conditionals",
    "Loops & Iterations",
    "Functions & Scope",
    "Data Structures: Lists & Tuples",
    "Dictionaries & Sets",
    "Object-Oriented Programming (OOP)",
    "Classes & Inheritance",
    "Modules & Standard Library",
    "File I/O & Exception Handling",
    "Virtual Environments & Pip",
    "Decorators & Generators",
    "Asyncio & Concurrency",
    "Unit Testing with PyTest",
    "Working with REST APIs",
    "Database Operations with SQLite",
    "Building CLI Applications",
    "FastAPI Microservices",
    "Capstone Project: Production Deployment",
)

private val GEN_AI_LESSONS = listOf(
    "Introduction to LLMs & GenAI",
    "Transformer Architecture Deep Dive",
    "Prompt Engineering Fundamentals",
    "Few-Shot & Chain-of-Thought",
    "Tokens, Embeddings & Semantic Search",
    "Vector Databases (Pinecone/Chroma)",
    "RAG (Retrieval-Augmented Generation)",
    "Building with LangChain & LlamaIndex",
    "Fine-Tuning Strategies (LoRA & QLoRA)",
    "Function Calling & Tool Use",
    "Multi-Agent Orchestration",
    "Multimodal Models (Vision & Audio)",
    "Model Evaluation & Guardrails",
    "AI Safety, Bias & Hallucination Mitigation",
    "Local Inference with Ollama & vLLM",
    "Final Capstone: Autonomous AI Agent",
)

private val FULL_STACK_LESSONS = listOf(
    "Web Architecture & HTTP Protocols",
    "Modern JavaScript (ES2024+) & TypeScript",
    "React Fundamentals & Component Lifecycle",
    "Advanced Hooks & State Management",
    "Server-Side Rendering with Next.js",
    "TailwindCSS & Modern Design Systems",
    "Node.js Runtime & Express.js Setup",
    "RESTful API Design & Validation",
    "Relational Databases with PostgreSQL",
    "ORM & Query Building with Prisma",
    "Authentication (OAuth2 & JWT)",
    "GraphQL APIs with Apollo",
    "State Management with Zustand & Redux",
    "Real-time Communication with WebSockets",
    "Microservices & Message Queues (Kafka)",
    "Caching Strategies with Redis",
    "Docker Containerization & Multi-stage Builds",
    "Kubernetes Orchestration Basics",
    "CI/CD Pipelines with GitHub Actions",
    "Cloud Infrastructure with AWS/GCP",
    "Serverless Functions & Edge Computing",
    "Web Security (OWASP Top 10)",
    "Performance Optimization & Web Vitals",
    "End-to-End Testing with Playwright",
    "Unit & Integration Testing with Jest",
    "Monitoring & Logging (Datadog/Sentry)",
    "Payment Gateway Integration (Stripe)",
    "Capstone Project: Full Stack SaaS Platform",
)

private fun createLessons(titles: List<LessonTitleConfig>): List<Lesson> =
    titles.mapIndexed { index, config ->
        Lesson(id = index + 1, title = config.title, completed = config.completed)
    }

private data class LessonTitleConfig(val title: String, val completed: Boolean)

private val MOCK_COURSES = listOf(
    Course(
        id = 1,
        title = "Python Programming",
        instructor = "John Smith",
        lessons = PYTHON_LESSONS.mapIndexed { i, title ->
            Lesson(id = i + 1, title = title, completed = i < 13) // 13/20 = 65%
        },
    ),
    Course(
        id = 2,
        title = "Generative AI",
        instructor = "Sarah Williams",
        lessons = GEN_AI_LESSONS.mapIndexed { i, title ->
            Lesson(id = i + 1, title = title, completed = i < 6) // ~40% (6/16 = 38%)
        },
    ),
    Course(
        id = 3,
        title = "Full Stack Development",
        instructor = "David Brown",
        lessons = FULL_STACK_LESSONS.mapIndexed { i, title ->
            Lesson(id = i + 1, title = title, completed = i < 7) // 7/28 = 25%
        },
    ),
)
