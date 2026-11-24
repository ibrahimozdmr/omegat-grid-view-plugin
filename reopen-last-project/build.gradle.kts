plugins {
    java
    distribution
    id("org.omegat.gradle") version "1.5.11"
}

version = "1.0.0"

omegat {
    // Latest OmegaT artifact published to Maven Central; runtime target is 6.0.1 (API-compatible patch release).
    version = "6.0.0"
    pluginClass = "org.omegat.plugins.reopenlast.ReopenLastProjectPlugin"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// OmegaT 6.0.x runs on Java 11+; keep the plugin bytecode compatible.
tasks.withType<JavaCompile> {
    options.release.set(11)
    options.encoding = "UTF-8"
}

distributions {
    main {
        contents {
            from(tasks["jar"], "README.md", "COPYING", "CHANGELOG.md")
        }
    }
}

val jar by tasks.getting(Jar::class) {
    archiveBaseName.set("omegat-reopen-last-project-plugin")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
