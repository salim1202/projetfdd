# Projet Fouille de données
Développer une application interfacée, en java, implantant l’algorithme Close.
Les résultats présentés par l’application dépendront d’un seuil défini par l’utilisateur et aussi d’un fichier format txt contenant tous les itemsets dans un format bien défini.
L’application renverra en sortie la liste des règles d’associations générées avec pour chacune d’elles le support, la confiance et le lift correspondant.
l’utilisateur à la possibilité de fixer un seuil de support compris entre 0,0 et 1,0 et de charger son fichier de données au format .txt ayant la structure suivante :
00|a|b|c| où 00 est un identifiant et a, b, c sont des items a, b, c, des chaînes de caractères; tous les éléments étant séparés par des ’|’. Les items son classés par ordre alphabétique lors du pré-traitement.
L’application renverra en sortie la liste des règles d’associations générées :
* Sur l’interface ET 
* Dans un fichier de sortie txt, avec pour chacune des règles d’association son support, sa confiance et son lift
