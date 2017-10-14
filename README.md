## Plugin Bukkit pour la protection des terrains
[Terrains] est le plugin [[ChunkOwn]](https://dev.bukkit.org/projects/chunkown) créé par [@Codisimus](https://github.com/Codisimus) forké par ArVdC pour MC 1.12 dans une version française.
![Terrains](https://media-elerium.cursecdn.com/avatars/72/496/636163394454471337.png "Codisimus ChunkOwn")

### FONCTIONNALITES
Acheter un [terrain] (= 1 chunk de 16x16 blocs) pour le protéger des autres joueurs.

Partager vos [terrains] avec votre liste d'amis.

Créez des [terrains] publics pour permettre à tous les joueurs d'interagir dans certaines zones.

Créez des [terrains] privés pour empécher même vos amis d'interagir dans certaines zones.

Créer un point de téléport personnel appelé [domicile], à l'intérieur de l'un de vos [terrains].

Restez informé de toute intrusion d'un joueur sur l'un de vos [terrains].


### COMMANDES JOUEURS
**/tr info:** Voir les limites du terrain actuel et ses caractéristiques (appartenance, statut, etc.)

**/tr acheter:** Acheter le terrain actuel.

**/tr vendre:** Vendre le terrain actuel.

**/tr toutVendre:** Vendre l'ensemble de vos terrains.

**/tr liste:** Lister l'ensemble de vos terrains.

**/tr partagePlus <joueur>:** Ajouter un joueur à votre liste de partage.

**/tr partageMoins <joueur>:** Supprimer un joueur de votre liste de partage.

**/tr partageListe:** Lister les joueurs avec qui vous partagez vos terrains.

**/tr statutNormal:** Seuls les joueurs figurant dans la liste de partage du propriétaire pourront accomplir toutes les actions sur ce terrain.

**/tr statutPublic:** Tous les joueurs pourront effectuer des actions de base sur ce terrain, mais pas y construire.

**/tr statutPrive:** En dehors du propriétaire, aucun joueur ne pourra effectuer la moindre action sur ce terrain.

**/tr alarm:** Activer ou non le système d'alarme qui vous préviendra de toute intrusion sur vos terrains.

**/tr domicile \[\<joueur>]:** Vous téléporter à votre domicile ou celui d'un ami.

**/tr domicileSet:** Mémoriser votre position actuelle comme étant votre domicile.


### COMMANDES ADMINS
**/tr toutVendre \<joueur>:** Revendre tous les terrains d'un joueur.

**/tr reload:** Reloader le plugin, permet de restaurer les fichiers manquants.


### PERMISSIONS
**terrains.resident:** Nécessaire pour utiliser la commande /tr acheter.

**terrains.ninja:** N'actionne pas l'alarme lorsque vous traversez le [terrain] d'un autre joueur.

**terrains.free:** Acquérir les [terrains] gratuitement.

**terrains.admin:** Interagir librement dans les [terrains] des autres joueurs et pouvoir les revendre.

**terrains.limit.n:** _n_ détermine le nombre de [terrains] qu'un joueur peut posséder. Si _n_ vaut -1 alors il n'y a pas de limite.


### COMPATIBILITE
* v1.0.0: Le plugin a été créé pour MC 1.12+ mais cela devrait fonctionner avec des versions plus anciennes. _N'a jamais été retro-testé_


### TODO
* Debuger tout le plugin, spécialement là où 2 joueurs interagissent (toutVendre et domicile \<joueur>)
* Demander à Codisimus la permission de publier (quand le travail sera plus avancé)
* Créer des zones colorées représentant les [terrains] sur Dynmap.

