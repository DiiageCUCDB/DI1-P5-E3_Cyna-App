## Guide - Installation de Java 17

### Problème :
Android Gradle Plugin 8.13 requiert Java 17+, mais vous avez Java 11.

### Solution - Télécharger Java 17+ :

1. **Accédez à Oracle Java Downloads**
   - https://www.oracle.com/java/technologies/downloads/
   
2. **Sélectionnez Java 17 (LTS) ou plus récent**
   - Téléchargez le JDK (pas JRE)
   - Version recommandée : Java 21 (LTS) ou Java 17 (LTS)
   
3. **Installez le JDK**
   - Prenez note du chemin d'installation
   - Exemple : `C:\Program Files\Java\jdk-17.x.x` ou `C:\Program Files\Java\jdk-21.x.x`

4. **Mettez à jour gradle.properties**
   
   Ouvrez le fichier : `C:\DIIAGE1\Cyna-App\gradle.properties`
   
   Décommentez et mettez à jour cette ligne :
   ```properties
   org.gradle.java.home=C:/Program Files/Java/jdk-21.x.x
   ```
   
   (Remplacez `jdk-21.x.x` par votre version réelle)

5. **Testez la compilation**
   ```powershell
   cd C:\DIIAGE1\Cyna-App
   ./gradlew build
   ```

### Alternative - Downgrader Android Gradle Plugin (NON recommandé)

Si vous ne pouvez pas installer Java 17+, vous pouvez downgrader le plugin Android Gradle en modifiant :
`app/build.gradle.kts` - remplacer version `8.13.0` par `7.4.2`

Cependant, cela n'est pas recommandé car le plugin 8.13 a des améliorations de sécurité et performance.

