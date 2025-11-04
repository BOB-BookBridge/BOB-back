## IntelliJ
 
1. `Settings` > `Editor` > `Code Style` > `Java`
2. `Import Scheme` > `IntelliJ IDEA code style XML` > `style/bob-intellij-code-style.xml`
3. `Imports`:
    - `Class count to use import with '*'` = `999`
    - `Names count to use static import with '*'` = `999`
4. `Settings` > `Editor` > `General` > `Auto Import` > `Java`
    - `Optimize imports on the fly` 체크
    - `Add unambiguous imports on the fly` 체크

---

## Eclipse
1. `Preferences` > `Java` > `Code Style` > `Formatter`
    - `Import…` → `style/bob-eclipse-code-style.xml`
2. `Organize Imports`
    - `Import…` → `style/bob.importorder`
