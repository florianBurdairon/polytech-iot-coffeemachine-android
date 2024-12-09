# Projet IoT : Coffee Machine - Application Android

## Installation de l'application

### Téléchargement de l'APK
Afin de pouvoir tester l'application sur votre smartphone, vous pouvez suivre ce lien : **https://appdistribution.firebase.dev/i/cae6b2eb7c0360f4** qui va permettre de télécharger l'APK de l'application. Il suffit de renseigner une adresse email pour recevoir un email contenant le lien de téléchargement.

En effet le code disponible sur ce dépôt (**coffeeMachine_android**) ne peut directement être compilé sur Android Studio. La connexion à Firebase nécessite de générer une empreinte SHA-1 qui sécurise la connexion entre l'application et Firebase pour l'authentification. Or cette empreinte est propre au PC qui construit l'APK de l'application et doit être saisie manuellement sur la console Firebase.

### Configuration nécessaire
Pour fonctionner, l'application doit être installée sur un téléphone possédant un version d'Android contenu entre 12 et 14 (version 15 non testé). Malgré le support d'Android 12, certaines fonctionnalités peuvent ne pas fonctionner correctement notamment l'envoie de données à la machine via Bluetooth Low Energy.
Il faut également que le téléphone supporte le BLE.

### Autorisations
Des autorisations seront demandés pour accéder à quelques fonctionalités de l'application :
- Bluetooth : lors de l'ajout d'un appareil via BLE
- Appareil photo : lors de l'ajout d'un appareil via QR code