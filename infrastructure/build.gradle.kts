dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("io.asyncer:r2dbc-mysql")
    implementation(project(":domain"))
    implementation("org.testcontainers:testcontainers-bom:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:r2dbc:1.16.0")
    testImplementation("org.testcontainers:mysql:1.19.3")
    implementation(group = "io.netty", name = "netty-resolver-dns-native-macos", version = "4.1.70.Final", classifier = "osx-aarch_64")
}
