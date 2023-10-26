/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("io.element.android-compose-library")
    alias(libs.plugins.anvil)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "io.element.android.features.lockscreen.impl"
}

anvil {
    generateDaggerFactories.set(true)
}

dependencies {
    ksp(libs.showkase.processor)
    implementation(projects.anvilannotations)
    anvil(projects.anvilcodegen)
    api(projects.features.lockscreen.api)
    implementation(projects.appconfig)
    implementation(projects.libraries.core)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.featureflag.api)
    implementation(projects.libraries.cryptography.api)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.sessionStorage.api)
    implementation(projects.services.appnavstate.api)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.test.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.molecule.runtime)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.turbine)
    testImplementation(projects.libraries.matrix.test)
    testImplementation(projects.tests.testutils)
    testImplementation(projects.libraries.cryptography.test)
    testImplementation(projects.libraries.cryptography.impl)
    testImplementation(projects.libraries.featureflag.test)
    implementation(projects.libraries.sessionStorage.test)
    implementation(projects.services.appnavstate.test)
}