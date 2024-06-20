import xyz.jpenilla.resourcefactory.bukkit.Permission
import java.text.SimpleDateFormat
import java.util.*

plugins {
    kotlin("jvm") version "2.0.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

val lowercaseName = project.name.lowercase(Locale.getDefault())
group = "io.github.yin.$lowercaseName"
version = ""; val pluginVersion = SimpleDateFormat("yyyy.MM.dd").format(Date()) + "-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.20.6-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

bukkitPluginYaml {
    apiVersion = "1.16"
    version = pluginVersion
    main = "$group.${project.name}Main"
    authors.add("尹")
    prefix = "代理信息"
    libraries = listOf("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")

    val pre = "${lowercaseName}.command"
    commands {
        register(lowercaseName) {
            aliases = listOf("pis")
            permission = pre
        }
    }
    permissions {
        register(pre) {
            default = Permission.Default.OP
        }
    }
}

kotlin {
    jvmToolchain(21)
}