import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "de.eintosti"
version = "1.0.0-alpha.2"
description = "Create a plot world made up of schematics"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "de.eintosti.schematicplotgenerator.SchematicPlotGenerator"
    apiVersion = "1.21"
    authors = listOf("einTosti")
}