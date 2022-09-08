// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public") // 解决速度过慢问题。
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
