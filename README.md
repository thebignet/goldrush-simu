goldrush-simu
=============

Goldrush est un jeu de combat de programmes, le jeu se déroule dans une mine austère et sombre où chaque mineur
doit se frayer un chemin jusqu'aux diamants pour en collecter un maximum. Mais attention dans
cette mine c'est «œil pour œil et dent pour dent», si vous croisez le chemin d'un autre mineur mal intentionné
il peut vous prendre pour cible et vous tirer dessus comme un vulgaire lapin dans son terrier.

`goldrush-simu` est le programme permettant de simuler une partie entre plusieurs programmes «mineurs».
Pour participer, un programme communique avec le simulateur par l'entrée et la sortie standard,
afin d'être compatible avec un maximum de langages de programmation (par exemple `stdin`/`stdout` en C).
Les échanges entre les programmes et le simulateur sont régis par un protocole textuel basé lignes.

Le protocole
------------

Les étapes suivantes donne la définition du protocole:

1. Au commencement, le programme «mineur» émet son nom suivi d'un retour chariot (`\n`, caractère ASCII 10): `Joe the mineur\n`

2. le simulateur lui répond en envoyant sur une ligne 3 nombres entiers, respectivement la largeur, la hauteur et
le nombre total de diamants dans la mine. Par exemple `30 20 50\n`

3. le simulateur envoie ensuite au programme son environnement proche, en effet dans la mine il fait sombre et les
mineurs munis de leurs lanternes ne voient qu'à une faible distance. L'environnement du mineur est transmis
de la façon suivante:
<pre>
x y n\n
X E E M M\n
M S E M M\n
M S E M M\n
M S M M M\n
M M M 3 M\n
x1 y1\n
x2 y2\n
</pre>
  * `x y` est la position du mineur sur la carte (0≤x<largeur et 0≤y<hauteur)
  * `n` indique le nombre de joueurs se trouvant dans son entourage
  * la portion de carte entourant le mineur est transmise par un tableau de 5 sur 5 cases, la position `x y`
correspondant à la case du centre, les valeurs possibles des cases sont:
    * `X` marque la position du wagonnet d'un joueur
    * `M` (Mud) de la terre
    * `S` (Stone) une pierre
    * `E` (Empty) du vide
    * un nombre entier `i`, indiquant la présence de `i` diamants
  * suit une série de `n` position, une pour chaque adversaire dans l'entourage du mineur

4. Le programme répond par une commande:
  * `NORTH\n` pour se déplacer vers le nord
  * `SOUTH\n` pour se déplacer vers le sud
  * `EAST\n` pour se déplacer vers l'est
  * `WEST\n` pour se déplacer vers l'ouest
  * `PICK\n` pour ramasser les diamants se trouvant à la position du mineur
  * `DROP\n` pour déposer les diamants à la position du mineur
  * `SHOOT\n` pour tirer une balle de fusil

5. La conversation entre le simulateur et le programme se poursuit en répétant les étapes 3 et 4.

Position de démarrage
---------------------

Au départ, chaque mineur démarre le jeu dans son wagonnet et regarde vers l'est.

Déplacements
------------

Les mienurs peuvent se déplacer librement sur les cases vides, creuser dans la terre et déplacer une pierre
si celle-ci à du vide derrière elle. Il est par contre impossible d'aller sur le wagonnet des adversaires ou dans la même case qu'eux.
Si le déplacement demandé par le mienur n'est pas possible, il reste à sa position.
A chaque déplacement (effectif ou non), le mienur regarde dans la direction du déplacement.

Manipulation des diamants
-------------------------

Un mineur, lorsqu'il ramasse des diamants, les tient dans ses mains. Il ne peut donc en tenir qu'une quantité limitée,
3 pour être précis (un dans chaque main et le troisième... enfin bon, les mineurs sont très agiles!).

Pour être comptabilisés, les diamants doivent être déposés dans le wagonnet du mieur.

Tirs de fusil
-------------

Les coups de fusil partent dans la direction dans laquel le mineur regarde et
se déplacent en ligne droite et dans le vide. Un mineur touché par un coup
de fusil, laisse tomber son butin à l'endroit où il se trouve et retourne dans son wagonnet.

Ordre de jeu
------------

A chaque tour, les joueurs jouent les uns après les autres dans un ordre aléatoire. L'environnement proche reçu
par chaque joueur reflète correctement les actions des autres joueurs ayant jouer avant dans le même tour.

Gagnant de la partie
--------------------

Le gagant de la partie est celui qui collecte le maximum de diamants, la partie étant limitée à 1000 tours.

Lancement du simulateur
-----------------------

Pour lancer le simulateur executer la commande :
`goldrush-simu maps\test.map "python example\my-goldrush.py"`

Il est possible de changer la vitesse d'exécution avec l'option `-t`, la valeur par défaut est 250,
la commande suivante permet d'accélérer la simulation :
`goldrush-simu -t 100 maps\test.map "C:\Python27\python.exe example\my-goldrush.py"`

L'exemple de joueur est développé en python, il vous faut installer l'interpréteur suivant:
http://www.python.org/download/releases/2.7.6/

