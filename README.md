# tal-intellij

![Build](https://github.com/xversial/tal-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/29315.svg)](https://plugins.jetbrains.com/plugin/29315)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/29315.svg)](https://plugins.jetbrains.com/plugin/29315)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [x] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains Marketplace.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.
- [ ] Configure the [CODECOV_TOKEN](https://docs.codecov.com/docs/quick-start) secret for automated test coverage reports on PRs

<!-- Plugin description -->
TAL Language Support provides syntax highlighting, commenting, and code folding for `.tal` and `.map` files.

Features:
- Syntax highlighting for keywords, identifiers, numbers, strings, operators, Java-style comments, and `${...}` variable expressions.
- Java-style single-line (`//`) and block (`/* */`) comments including toggle commenting.
- Code folding for `<editor-fold desc="..."> ... </editor-fold>` blocks.
- Code folding for region markers using `// region` ... `// endregion` and `# region` ... `# endregion`.
- Color settings page to customize highlighting colors.

This section is parsed into the plugin manifest during the build.
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "tal-intellij"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29315) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/29315/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/xversial/tal-intellij/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation


### System trust store (fix PKIX/SSL errors behind corporate interceptors)

If your security agent intercepts TLS and you want Gradle to trust your OS certificate store without managing custom truststores:

- macOS: use Keychain as the JSSE trust store
- Windows: use the Windows system root store

This project’s Gradle wrappers support an opt-in switch that passes the proper JVM flags before Gradle starts.

Enable one of the following:

1) Per-shell (recommended):
```
export USE_SYSTEM_TRUST_STORE=true
./gradlew buildPlugin
```

2) Per-project (committed): add to this repo’s `gradle.properties`:
```
useSystemTrustStore=true
```

Notes:
- Linux has no generic system trust store for JSSE; the toggle is ignored on Linux.
- On macOS this uses `-Djavax.net.ssl.trustStore=NONE -Djavax.net.ssl.trustStoreType=KeychainStore`.
- On Windows it uses `-Djavax.net.ssl.trustStore=NONE -Djavax.net.ssl.trustStoreType=Windows-ROOT`.
- If your JDK distribution doesn’t support these store types, leave this off and instead configure a user truststore via `~/.gradle/gradle.properties` with `org.gradle.jvmargs=...` as described in the earlier troubleshooting guidance.
