# Walid Voyages Android — projet de politique de confidentialité

**À vérifier et publier par l'entreprise avant mise en ligne sur Google Play.** Ce document décrit uniquement le code fourni dans ce projet.

## Données et fonctionnement

- L'application affiche des informations sur les services et des invocations stockées localement.
- Les rappels d'invocations matin/soir sont facultatifs et générés localement avec Android WorkManager. L'app demande la permission de notifications (Android 13+).
- Les préférences de rappels et d'invocations favorites sont sauvegardées uniquement dans les préférences du téléphone. Elles ne sont pas envoyées à Walid Voyages dans cette version.
- L'application n'embarque pas Firebase, SDK publicitaire, outil d'analyse ou système de compte.
- L'app ne collecte pas de localisation.
- Les boutons de réservation ouvrent un navigateur vers `www.walid-voyages.com` ; les données et traitements éventuels de ce site relèvent de sa politique propre et sont à documenter séparément.
- Les liens téléphoniques et WhatsApp ouvrent les applications correspondantes, dont les règles de confidentialité s'appliquent ensuite.

## Contrôles utilisateur

L'utilisateur peut désactiver les rappels dans l'application ou les notifications dans les réglages Android et peut retirer ses favoris. Désinstaller l'application efface ses préférences locales selon les règles d'Android.

## Contact

Walid Voyages, Sousse, Tunisie. Téléphone : +216 73 201 660 / +216 58 150 699. Site : https://www.walid-voyages.com/

**Avant publication :** compléter l'identité légale, adresse email de confidentialité, droits et procédures applicables, vérifier les fonctionnalités et les tiers réellement intégrés et publier cette politique à une URL HTTPS stable.
