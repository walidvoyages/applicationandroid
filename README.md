# Walid Voyages Android 🇹🇳

**Véritable application Android native**, écrite en Kotlin + Jetpack Compose.
**Pas de WebView, pas de PWA et pas d'application web.** Le contenu, les menus, les invocations, le guide Omra et les notifications sont natifs. Les **boutons de réservation** ouvrent le site officiel dans le navigateur du téléphone afin de reprendre les services déjà existants.

## Fonctionnalités livrées

- Interface Android RTL en arabe avec couleurs Walid Voyages (#49AAE3, #B5D33F, blanc), logo original, bannière Omra et identité visuelle.
- Accueil : Omra, vols, hôtels, voyages organisés, contacts.
- Omra : accès direct aux programmes du site + guide natif des étapes (7 étapes).
- Voyages : circuits organisés, séjour à l'étranger, hôtels, vols et demandes visa.
- Ad3iya : 12 invocations sélectionnées avec sources, catégories, favoris locaux, copie et partage.
- **Rappels d'invocations** du matin (~09:00) et soir (~20:00), séparément activables, persistant après redémarrage via WorkManager. Les heures sont approximatives, car Android peut retarder les tâches pour économiser l'énergie.
- Bouton « Tester une notification » et demande de permission `POST_NOTIFICATIONS` sur Android 13+.
- Accès téléphone et WhatsApp ; l'application ne requiert ni inscription ni Firebase.
- GitHub Actions pour générer un **APK Android installable** et une GitHub Release test, sans Android Studio local.

## Important sur les notifications

Cette version fonctionne **sans serveur** : les notifications sont générées **sur le téléphone** à partir d'une bibliothèque d'invocations dans le code. Elles ne sont pas des messages commerciaux distants.

Pour envoyer à tous les utilisateurs des **nouvelles offres, annulations, changements de vols, promotions Omra, etc.**, il faut ensuite :
1. un **nouveau projet Firebase** (ne pas réutiliser l'ancien Firebase de Walid Voyages) ;
2. Firebase Cloud Messaging (FCM) et permissions appropriées ;
3. un panneau administrateur ou un serveur de notifications autorisé ;
4. le consentement des utilisateurs, gestion des préférences et politique de confidentialité à jour.

Aucun Firebase ni secret n'a été inventé ou intégré dans cette version.

## Mettre le projet sur GitHub puis obtenir un APK

**0.** Sur [github.com/new](https://github.com/new), créez un dépôt, par exemple `WalidVoyagesAndroid` (Privé ou Public). Ne cochez pas « initialize README » si vous importez le projet complet.

**1.** Décompressez le ZIP du projet. Mettez **tous les fichiers et dossiers** à la racine du dépôt, y compris **`.github/workflows`** (important pour le build) et le dossier `app`. Pour conserver les fichiers cachés, la méthode fiable est `git push` :

```bash
git init
git branch -M main
git add .
git commit -m "Walid Voyages Android - first native release"
git remote add origin https://github.com/VOTRE_COMPTE/WalidVoyagesAndroid.git
git push -u origin main
```

**2.** GitHub → dépôt → **Actions** → `Build native Android APK` → le dernier workflow → **Artifacts** → **WalidVoyages-Android-APK** → télécharger le ZIP d'artifact (contient `app-debug.apk`). GitHub Actions a besoin d'être activé sur le dépôt.

**3.** Sur le téléphone Android, extrayez `app-debug.apk`, ouvrez-le et autorisez l'installation « depuis cette source » si Android le demande. L'application native se lance **depuis l'icône Walid Voyages** ; elle n'est pas consultable dans un onglet GitHub Pages.

**4.** Pour obtenir une URL publique d'un APK de test via **GitHub Releases**, créez un tag :

```bash
git tag v1.0.0
git push origin v1.0.0
```

Attendez le workflow `Publish APK for testers on GitHub Releases`. Le fichier `app-debug.apk` sera disponible dans **Releases** de votre dépôt. **APK de test uniquement**, ne jamais utiliser un debug build comme release Play Store.

## Ouvrir dans Android Studio

- Open → dossier racine `WalidVoyagesAndroid`.
- Installer **Android SDK API 36** et JDK 17.
- Si le projet n'a pas de Gradle Wrapper installé localement, utilisez Gradle 8.13 dans Android Studio ou générez le wrapper avec `gradle wrapper --gradle-version 8.13` après installation de Gradle. Les workflows GitHub n'ont pas besoin d'un wrapper.
- Exécuter sur un vrai Android ou un émulateur, et activer les notifications dans l'application.
- Commande de compilation locale après installation de Gradle 8.13 + Android SDK : `gradle :app:assembleDebug`.

## Avant Google Play Store

- **Créer votre propre clé de signature** et une version Release **AAB** via Android Studio → Build → Generate Signed Bundle / APK → Android App Bundle.
- `applicationId` actuellement `com.walidvoyages.app`, `versionCode` 1, `versionName` 1.0.0. Vérifiez que ce nom de package vous appartient/convient avant premier envoi au Play Store : il devient permanent pour cette app.
- Configurer une **politique de confidentialité publique** accessible par URL et vérifier le formulaire *Data safety* selon les traitements réellement effectués, ainsi que la **politique du site de réservation externe**.
- Ajouter fiches Play Store, captures **réelles**, icône, contacts support, tests sur plusieurs appareils et test interne/fermé selon les obligations du compte.
- Ne **jamais** mettre une clé de signature ou des credentials Firebase dans un dépôt GitHub public.
- Pour le paiement, les opérations ont lieu sur le site externe, et non dans l'application.

## Configuration des liens du site

Modifiez `app/src/main/java/com/walidvoyages/app/WalidData.kt` → `object BookingLinks`.

Les routes présentes proviennent de liens existants sur le domaine historique `https://www.walid-voyages.com/`, connu pour ses menus Omra, Vols, Hôtels, Circuits et Séjours à l'étranger. **Certaines pages PHP n'ont pas pu être vérifiées comme accessibles** lors de la préparation ; testez les boutons sur un téléphone et adaptez les routes si le site a changé. Le domaine `walidvoyages.com` doit être testé avant de le remplacer, car il n'a pas pu être vérifié ici.

Ces liens sont pour la réservation **externe**. Il n'existe **aucune API directe** de stocks, tarifs et paiements dans cette première version. L'application ne promet jamais une disponibilité ou un prix en temps réel qu'elle ne sait pas vérifier.

## Identité visuelle

- `app/src/main/res/drawable-nodpi/walid_logo.jpg`: logo fourni par le client, conservé sans changement graphique.
- `app/src/main/res/drawable-nodpi/omra_hero.jpg`: extrait de la couverture Omra créée pour Walid Voyages.
- `mipmap-*/ic_launcher.png`: icône graphique W simplifiée (remplaçable par fichier PNG officiel).
- Couleurs principales définies en Kotlin dans `MainActivity.kt`.

## Notes de livraison

- **Source prête pour GitHub**. La compilation n'a **pas pu être exécutée dans l'environnement de préparation** faute de SDK Android / Gradle et d'accès aux dépôts Maven. Il faut contrôler le résultat du workflow GitHub avant d'affirmer que l'APK fonctionne.
- La première ouverture vers le site et les notifications doivent être testées sur un Android réel.
- Aucun dépôt GitHub, aucune application Play Store et aucun Firebase n'ont été créés automatiquement au nom du client.
