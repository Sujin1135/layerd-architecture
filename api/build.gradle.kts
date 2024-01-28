import com.google.protobuf.gradle.*

plugins {
    id("com.google.protobuf") version "0.9.2"
    kotlin("jvm")
}

repositories {
    google()
}

dependencies {
    implementation("io.grpc:grpc-netty:1.61.0")
    implementation("io.grpc:grpc-protobuf:1.61.0")
    implementation("io.grpc:grpc-stub:1.61.0")
    implementation("io.grpc:grpc-api:1.61.0")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.25.2")
    implementation("com.google.protobuf:protobuf-java:3.25.2")
    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(project(":domain"))
}

buildscript {
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.19")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.2"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.54.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}
