# game-project-S2

## Introduction

Notre jeu 2D est développé en **Scala** à l'aide de la librairie **GDX2D**, dans le cadre du projet de module du semestre 2.

Notre jeu s'appelle : **Who can catch Mudry??** Il s'agit d'un jeu type loup. On a le 1er joueur qui incarne le Saint-Mudry son objectif et de fuir le 2ème joueur qui incarne le démon Python. 

## Instructions

### Lancement du jeu

1. **Prérequis :** Avoir **Java JDK 17** (ou supérieur) installé.
2. **Ouvrir le projet dans IntelliJ IDEA :**
   * Sélectionnez `File` → `Open` et choisir le dossier du projet.
   * Vérifiez que la bibliothèque `gdx2d-desktop-1.2.2.jar` est correctement intégrées comme librairie
3. **Exécution :** Lance le fichier principal situé dans le dossier des sources (ex. `Main.scala`).

### Comment jouer

Le jeu se contrôle entièrement au clavier :

* **Déplacements :** Touches `W` `A` `S` `D` pour déplacer le premier dans les directions cardinales. Touches fléchées pour le 2ème joueur.

### Fin de la partie

* **Condition de défait :** le joueur incarnant python arrive à attraper le joueur incarnant le Saint-Mudry.
<img width="1383" height="678" alt="EndScreenLoose" src="https://github.com/user-attachments/assets/7cb439b8-f1fa-4476-b961-f617f02d015b" />

* **Condition de victoire :** Si le joueur incranant le Saint-Mudry arrive à fuir le joueur incrant Python pendant 1 minute.
<img width="1383" height="678" alt="EndScreenWin" src="https://github.com/user-attachments/assets/a8dc471d-a79f-4f88-9ba9-46e487131e79" />


---

## Structure du code

Le projet est organisé de manière modulaire afin de séparer la logique du jeu, la physique et le rendu graphique.

### Organisation des dossiers

* **`data/`** : Contient les fichiers de configuration, les fichiers de maps et les assets du jeu.
* **`SpriteSheet/`** : Regroupe l'ensemble des feuilles de sprites pour les animations des joueurs.
* **`src/`** : Dossier racine des fichiers sources Scala.

### Sous-dossiers de `src/`
* **`classes/`** : Regroupe la classe abstraite parent `Entity` et ses déclinaisons (`Player`, `Chaser`) pour gérer les déplacements animés par interpolation sur la carte, ainsi que la logique d'apparition, d'expiration et de collecte des différents bonus.
* **`font/`** : Permet de charger des fichiers de polices personnalisés et de générer dynamiquement des polices de caractères (`BitmapFont`) adaptées à la résolution de l'écran à l'aide de l'utilitaire FreeTypeFontGenerator[cite: 8].
* **`game/`** : Contient la boucle principale du jeu, initialise la fenêtre GDX2D à 60 FPS, gère les transitions d'écrans (Accueil, Jeu, Fin), redistribue les entrées clavier et charge l'environnement sonore ainsi que les règles logiques de la carte (zones marchables).
* **`listener/`** : Contient les gestionnaires d'événements pour les entrées utilisateur (clavier/souris) et la détection des collisions physiques.
* **`map/`** : Assure le chargement et le rendu visuel des cartes au format Tiled (`.tmx`), gère l'extraction des propriétés physiques des tuiles (vitesse, franchissement), et applique des filtres graphiques avancés via des fragments de shaders fragmentaires (`.fp`) pour projeter des effets dynamiques (comme un cône de lumière ou un champ de vision) autour des entités en jeu.
* **`object/`** : Centralise les énumérations et les paramètres de configuration globaux du jeu, notamment la définition des directions de déplacement (`Direction`), les différents types de bonus disponibles (`BonusType`), ainsi que toutes les constantes structurelles (dimensions des sprites, vitesse de base, rayon de lumière, temps de jeu et gestion du chronomètre).
* **`screen/`** : Pilote l'affichage complet et l'interface utilisateur des trois phases majeures du jeu, orchestrant l'écran d'accueil (`HomeScreen`) avec ses menus et sa musique d'introduction, l'arène de jeu principale (`GameScreen`) qui gère la boucle d'actualisation des entités, du chronomètre et l'application des bonus/malus, ainsi que l'écran de fin (`EndScreen`) qui détermine textuellement et musicalement l'état de victoire ou de défaite.
* **`utils/`** : Fonctions d'aide, gestionnaires de caméras, utilitaires d'affichage et outils de débogage.
* **`trait/`** : Regroupe les interfaces et comportements partagés du jeu, incluant la gestion uniforme de la typographie et de l'audio (`ScreenGame`), l'architecture commune de création et de positionnement des boutons d'interface (`MenuScreen`), ainsi que l'ensemble des propriétés et états d'altération (bonus, malus, minuteries associées) des entités mobiles (`Movable`).

---

## Bibliothèques utilisées

* **`gdx2d-desktop-1.2.2.jar`** : Bibliothèque principale du projet. Elle permet d'afficher tous les éléments graphiques 2D, de gérer la physique via le moteur **Box2D**, ainsi que les sons et les interactions utilisateur.
* **`gdx2d-desktop-1.2.2-sources.jar`** : Sources de la bibliothèque GDX2D, utilisées pour la documentation interne et le débogage.

---

## Équipe de développement

* **Ravaka Nasandratriniaina Andriamifidy** : nasandratriniaina.andriamifidy@students.hevs.ch
* **Adrien Gaillard** : adrien.gaillard@students.hevs.ch
