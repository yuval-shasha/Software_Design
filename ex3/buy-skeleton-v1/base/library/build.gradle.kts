plugins {
    id("buildlogic.kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.gitlab.mvysny.konsume-xml:konsume-xml:1.2")
    testImplementation(kotlin("test"))
}